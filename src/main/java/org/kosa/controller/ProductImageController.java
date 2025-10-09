package org.kosa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.kosa.dto.image.ImageUploadResponse;
import org.kosa.dto.productImage.ProductImageReq;
import org.kosa.dto.productImage.ProductImageRes;
import org.kosa.entity.ProductImage;
import org.kosa.service.GcsImageService;
import org.kosa.service.ProductImageService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;

import java.net.URI;
import java.net.URLConnection;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Tag(name = "ProductImage", description = "상품 이미지 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/images")
@Validated
public class ProductImageController {

    private final ProductImageService productImageService;
    private final GcsImageService gcsImageService;
    private final Storage storage; // GcsConfig에서 Bean 주입

    // https://storage.googleapis.com/{bucket}/{object}
    private static final Pattern GCS_HTTPS =
            Pattern.compile("^https://storage\\.googleapis\\.com/([^/]+)/(.+)$");

    /* ========================= 조회 ========================= */

    @Operation(summary = "상품별 이미지 목록 조회", description = "특정 상품의 이미지를 정렬순으로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{productId}")
    public List<ProductImageRes> list(
            @Parameter(description = "상품 ID") @PathVariable Long productId) {
        return productImageService.listByProduct(productId);
    }

    @Operation(summary = "상품 대표 이미지 조회", description = "정렬 1순위 이미지를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{productId}/cover")
    public ProductImageRes cover(
            @Parameter(description = "상품 ID") @PathVariable Long productId) {
        return productImageService.getCover(productId);
    }

    /* ========================= 업로드/교체/수정/삭제 ========================= */

    @Operation(summary = "상품 이미지 업로드(순서 보존)", description = "FormData: files[], RequestParam: groupId(=productId)")
    @ApiResponse(responseCode = "201", description = "업로드 성공")
    @PostMapping(
            path = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ImageUploadResponse uploadOrdered(
            @RequestParam @NotBlank String groupId,
            @RequestPart("files") List<MultipartFile> files
    ) {
        // 내부적으로 GCS 업로드 + DB(ProductImage) 저장까지 처리
        return gcsImageService.uploadOrdered("product", groupId, files);
    }

    @Operation(summary = "상품 이미지 전체 교체")
    @ApiResponse(responseCode = "200", description = "교체 성공")
    @PostMapping("/{productId}/replace")
    public List<ProductImageRes> replaceAll(
            @Parameter(description = "상품 ID") @PathVariable Long productId,
            @RequestBody List<@Valid ProductImageReq> reqs) {
        return productImageService.replaceAll(productId, reqs);
    }

    @Operation(summary = "상품 이미지 정보 수정")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PutMapping("/{productId}/{imageId}")
    public ProductImageRes update(
            @Parameter(description = "상품 ID") @PathVariable Long productId,
            @Parameter(description = "이미지 ID") @PathVariable Long imageId,
            @RequestBody @Valid ProductImageReq req) {
        return productImageService.update(imageId, req);
    }

    @Operation(summary = "상품 이미지 순서 이동 (1-based)")
    @ApiResponse(responseCode = "204", description = "이동 성공")
    @PostMapping("/{productId}/{imageId}/move/{pos}")
    public ResponseEntity<Void> move(
            @PathVariable Long productId,
            @PathVariable Long imageId,
            @PathVariable int pos) {
        productImageService.move(imageId, pos);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "상품 이미지 삭제")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{productId}/{imageId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        productImageService.delete(imageId);
        return ResponseEntity.noContent().build();
    }

    /* ========================= URL 목록(선택) ========================= */

    @Operation(summary = "상품 이미지 URL 목록", description = "업로드 순서대로 URL 리스트를 반환합니다.")
    @GetMapping("/download/{groupId}")
    public List<ImageUploadResponse.Item> getImages(@PathVariable String groupId) {
        List<ProductImageRes> records = productImageService
                .listByProduct(Long.parseLong(groupId));

        // DB의 url 필드가 이미 전체 HTTPS 경로이므로 그대로 반환
        return records.stream()
                .map(r -> new ImageUploadResponse.Item(
                        r.getSortOrder(),
                        r.getUrl(),           // originalUrl
                        r.getUrl(),           // publicUrl (public-read가 아니라면 프런트는 /stream/{id} 사용)
                        null                  // signedUrl 필요 시 gcsImageService에서 발급해 채우기
                ))
                .collect(Collectors.toList());
    }

    /* ========================= 이미지 서빙(프록시) ========================= */

    @Operation(summary = "이미지 스트리밍(서버 프록시) - ID 기반",
            description = "브라우저는 이 엔드포인트만 호출하고, 서버가 GCS에서 바이트를 읽어 응답합니다.")
    @GetMapping("/stream/{imageId}")
    public ResponseEntity<byte[]> streamById(@PathVariable Long imageId) {
        ProductImage entity = productImageService
                .findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var parts = parseGcsHttps(entity.getUrl());
        Blob blob = storage.get(parts.bucket(), parts.object());
        if (blob == null || !blob.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        byte[] bytes = blob.getContent();
        String contentType = Optional.ofNullable(blob.getContentType())
                .orElseGet(() -> URLConnection.guessContentTypeFromName(parts.object()));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        contentType != null ? contentType : "application/octet-stream"))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(1)).cachePublic())
                .body(bytes);
    }

    @Operation(summary = "이미지 스트리밍(서버 프록시) - URL 직접 전달",
            description = "src가 GCS면 서버가 바이트 스트림으로 응답, 그 외(assets/static/uploads 등)는 안전하게 리다이렉트합니다.")
    @GetMapping("/proxy")
    public ResponseEntity<?> proxyByUrl(@RequestParam("src") String src) {
        if (src == null || src.isBlank()) {
            return ResponseEntity.notFound().build();
        }

        // 1) gs:// → https://storage.googleapis.com/ 로 변환
        if (src.startsWith("gs://")) {
            src = "https://storage.googleapis.com/" + src.substring("gs://".length());
        }

        // 2) 내부 정적/상대경로는 프록시하지 않고 리다이렉트 (무한루프 방지: 우리 API로 안 돌려보냄)
        if (src.startsWith("/assets/")
                || src.startsWith("/static/")
                || src.startsWith("/uploads/")
                || src.startsWith("/files/")
                || src.startsWith("/images/")) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(src))
                    .build();
        }

        // 3) 절대 URL이지만 GCS가 아니면(원한다면) 리다이렉트로만 처리하여 SSRF 위험 줄이기
        if (src.startsWith("http://") || src.startsWith("https://")) {
            if (!src.startsWith("https://storage.googleapis.com/")) {
                // 필요 시 차단(400)도 가능. 여기선 리다이렉트로 안전 처리.
                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(src))
                        .build();
            }
        }

        // 4) 이제 GCS(https://storage.googleapis.com/...)만 스트리밍
        var parts = parseGcsHttps(src); // 여기서만 GCS 패턴 강제
        Blob blob = storage.get(parts.bucket(), parts.object());
        if (blob == null || !blob.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        byte[] bytes = blob.getContent();
        String contentType = Optional.ofNullable(blob.getContentType())
                .orElseGet(() -> URLConnection.guessContentTypeFromName(parts.object()));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(1)).cachePublic())
                .body(bytes);
    }

    /* ========================= 유틸 ========================= */

    private record GcsParts(String bucket, String object) {}
    private GcsParts parseGcsHttps(String httpsUrl) {
        Matcher m = GCS_HTTPS.matcher(httpsUrl);
        if (!m.matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported GCS URL: " + httpsUrl);
        }
        return new GcsParts(m.group(1), m.group(2));
    }
}
