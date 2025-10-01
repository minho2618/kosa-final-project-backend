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
import org.kosa.entity.ProductQuestionPhoto;
import org.kosa.service.ProductImageService;
import org.springframework.http.HttpStatus;
import org.kosa.service.GcsImageService;
import org.kosa.service.ProductImageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "ProductImage", description = "상품 이미지 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/images")
@Validated
public class ProductImageController {

    private final ProductImageService productImageService;
    private final GcsImageService gcsImageService;

    @Operation(summary = "상품별 이미지 목록 조회", description = "특정 상품에 속한 모든 이미지를 정렬 순서대로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public List<ProductImageRes> list(@Parameter(description = "이미지 목록을 조회할 상품 ID") @PathVariable Long productId) {
        return productImageService.listByProduct(productId);
    }

    @Operation(summary = "상품 대표 이미지 조회", description = "특정 상품의 대표 이미지(정렬 1순위)를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/cover")
    public ProductImageRes cover(@Parameter(description = "대표 이미지를 조회할 상품 ID") @PathVariable Long productId) {
        return productImageService.getCover(productId);
    }

    @Operation(summary = "상품 이미지 추가", description = "특정 상품에 새로운 이미지를 추가합니다. (sortOrder 미지정 시 마지막 순서로 자동 지정)")
    @ApiResponse(responseCode = "201", description = "추가 성공")
    @PostMapping("/{productId}")
    public ResponseEntity<?> add(
            @PathVariable Long productId,
            @RequestPart("files") List<MultipartFile> files) {
         List<ProductImage> res = productImageService.add(productId, files);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }


    @Operation(summary = "상품 이미지 전체 교체", description = "특정 상품의 모든 이미지를 삭제하고 새로운 이미지 목록으로 교체합니다.")
    @ApiResponse(responseCode = "200", description = "교체 성공")
    @PostMapping("/replace")
    public List<ProductImageRes> replaceAll(
            @Parameter(description = "이미지를 교체할 상품 ID") @PathVariable Long productId,
            @RequestBody List<@Valid ProductImageReq> reqs) {
        return productImageService.replaceAll(productId, reqs);
    }

    @Operation(summary = "상품 이미지 정보 수정", description = "특정 이미지의 URL, 대체 텍스트, 정렬 순서를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PutMapping("/{imageId}")
    public ProductImageRes update(
            @Parameter(description = "소속 상품 ID") @PathVariable Long productId,
            @Parameter(description = "수정할 이미지 ID") @PathVariable Long imageId,
            @RequestBody @Valid ProductImageReq req) {
        return productImageService.update(imageId, req);
    }

    @Operation(summary = "상품 이미지 순서 이동", description = "특정 이미지의 정렬 순서를 지정된 위치(1-based)로 이동시킵니다.")
    @ApiResponse(responseCode = "204", description = "이동 성공")
    @PostMapping("/{imageId}/move/{pos}")
    public ResponseEntity<Void> move(
            @Parameter(description = "소속 상품 ID") @PathVariable Long productId,
            @Parameter(description = "이동할 이미지 ID") @PathVariable Long imageId,
            @Parameter(description = "이동할 위치 (1부터 시작)") @PathVariable int pos) {
        productImageService.move(imageId, pos);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "상품 이미지 삭제", description = "특정 이미지를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "소속 상품 ID") @PathVariable Long productId,
            @Parameter(description = "삭제할 이미지 ID") @PathVariable Long imageId) {
        productImageService.delete(imageId);
        return ResponseEntity.noContent().build();
    }

    // Google Cloud Storage
    /**
     * 여러 장 이미지를 업로드 순서대로 저장합니다.
     * - 프런트에서 'files' 필드를 업로드 순서대로 전송하면 그 순서가 보존됩니다.
     * - groupId는 동일 묶음을 식별하기 위한 값(게시글 ID/주문 ID/UUID 등)
     */
    @PostMapping(
            path = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ImageUploadResponse uploadOrdered(
            @RequestParam @NotBlank String groupId,
            @RequestPart("files") List<MultipartFile> files
    ) {
        return gcsImageService.uploadOrdered("product", groupId, files);
    }

    /**
     * 특정 groupId의 이미지들을 업로드 순서대로 조회
     * -> URL 목록을 반환
     */
    @GetMapping("/download/{groupId}")
    public List<ImageUploadResponse.Item> getImages(@PathVariable String groupId) {
        List<ProductImageRes> records =  productImageService
                .listByProduct(Long.parseLong(groupId));

        return records.stream()
                .map(r -> new ImageUploadResponse.Item(
                        r.getSortOrder(),
                        r.getUrl(),
                        // Public-read라면
                        "https://storage.googleapis.com/" + "product" + "/" + r.getImageId(),
                        // Private라면 signedUrl 발급하는 로직 필요
                        null
                ))
                .collect(Collectors.toList());
    }


    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }
}