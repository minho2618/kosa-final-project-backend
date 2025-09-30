package org.kosa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.kosa.dto.image.ImageUploadResponse;
import org.kosa.dto.reviewPhoto.ReviewPhotoReq;
import org.kosa.dto.reviewPhoto.ReviewPhotoRes;
import org.kosa.entity.ProductQuestionPhoto;
import org.kosa.entity.ReviewPhoto;
import org.kosa.service.GcsImageService;
import org.kosa.service.ReviewPhotoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "ReviewPhoto", description = "리뷰 사진 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/photos")
@Validated
public class ReviewPhotoController {

    private final ReviewPhotoService reviewPhotoService;
    private final GcsImageService gcsImageService;

    @Operation(summary = "리뷰의 사진 목록 조회", description = "특정 리뷰에 첨부된 모든 사진을 정렬 순서대로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public List<ReviewPhotoRes> list(@Parameter(description = "사진 목록을 조회할 리뷰 ID") @PathVariable Long reviewId) {
        return reviewPhotoService.listByReview(reviewId);
    }

    @Operation(summary = "리뷰의 대표 사진 조회", description = "특정 리뷰의 대표 사진(정렬 1순위)을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/cover")
    public ReviewPhotoRes cover(@Parameter(description = "대표 사진을 조회할 리뷰 ID") @PathVariable Long reviewId) {
        return reviewPhotoService.getCover(reviewId);
    }

    @Operation(summary = "리뷰 사진 추가", description = "특정 리뷰에 사진을 추가합니다.")
    @ApiResponse(responseCode = "201", description = "추가 성공")
    @PostMapping
    public ResponseEntity<ReviewPhotoRes> add(
            @Parameter(description = "사진을 추가할 리뷰 ID") @PathVariable Long reviewId,
            @RequestBody @Valid ReviewPhotoReq req) {
        ReviewPhotoRes res = reviewPhotoService.add(reviewId, req);
        return ResponseEntity
                .created(URI.create(String.format("/api/reviews/%d/photos/%d", reviewId, res.getPhotoId())))
                .body(res);
    }

    @Operation(summary = "리뷰 사진 전체 교체", description = "기존 사진을 모두 삭제하고 새 사진 목록으로 교체합니다.")
    @ApiResponse(responseCode = "200", description = "교체 성공")
    @PostMapping("/replace")
    public List<ReviewPhotoRes> replaceAll(
            @Parameter(description = "사진을 교체할 리뷰 ID") @PathVariable Long reviewId,
            @RequestBody List<@Valid ReviewPhotoReq> reqs) {
        return reviewPhotoService.replaceAll(reviewId, reqs);
    }

    @Operation(summary = "리뷰 사진 정보 수정", description = "특정 사진의 URL 또는 정렬 순서를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PutMapping("/{photoId}")
    public ReviewPhotoRes update(
            @Parameter(description = "소속 리뷰 ID") @PathVariable Long reviewId,
            @Parameter(description = "수정할 사진 ID") @PathVariable Long photoId,
            @RequestBody @Valid ReviewPhotoReq req) {
        return reviewPhotoService.update(photoId, req);
    }

    @Operation(summary = "리뷰 사진 삭제", description = "특정 사진을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "소속 리뷰 ID") @PathVariable Long reviewId,
            @Parameter(description = "삭제할 사진 ID") @PathVariable Long photoId) {
        reviewPhotoService.delete(photoId);
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
        return gcsImageService.uploadOrdered("review", groupId, files);
    }

    /**
     * 특정 groupId의 이미지들을 업로드 순서대로 조회
     * -> URL 목록을 반환
     */
    @GetMapping("/download/{groupId}")
    public List<ImageUploadResponse.Item> getImages(@PathVariable String groupId) {
        List<ReviewPhotoRes> records =  reviewPhotoService
                .listByReview(Long.parseLong(groupId));

        return records.stream()
                .map(r -> new ImageUploadResponse.Item(
                        r.getSortOrder(),
                        r.getUrl(),
                        // Public-read라면
                        "https://storage.googleapis.com/" + "review" + "/" + r.getPhotoId(),
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