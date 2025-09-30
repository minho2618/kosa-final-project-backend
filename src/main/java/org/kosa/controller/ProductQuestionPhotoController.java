package org.kosa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.dto.image.ImageUploadResponse;
import org.kosa.dto.productQuestionPhoto.ProductQuestionPhotoRes;
import org.kosa.entity.ProductQuestionPhoto;
import org.kosa.exception.RecordNotFoundException;
import org.kosa.service.GcsImageService;
import org.kosa.service.ProductQuestionAnswerService;
import org.kosa.service.ProductQuestionPhotoService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "ProductQuestionPhoto", description = "상품 문의 사진 API")
@RestController
@RequestMapping("/api/product-questions")
@RequiredArgsConstructor
@Slf4j
public class ProductQuestionPhotoController {

    private final ProductQuestionPhotoService photoService;
    private final GcsImageService gcsImageService;

    // 단일 사진 업로드
    @Operation(summary = "단일 사진 업로드", description = "단일한 사진을 업로드합니다.")
    @ApiResponse(responseCode = "201", description = "업로드 성공")
    @PostMapping(value = "/{questionId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPhoto(
            @PathVariable Long questionId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) Integer sortOrder) {
        ProductQuestionPhoto savedPhoto = photoService.savePhoto(questionId, file, sortOrder);

        return ResponseEntity
                .status(201)
                .body(savedPhoto);
    }

    // 여러 사진 업로드
    @Operation(summary = "여러 사진 업로드", description = "여러장의 사진을 업로드합니다.")
    @ApiResponse(responseCode = "201", description = "업로드 성공")
    @PostMapping(value = "/{questionId}/photos/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPhotos(
            @PathVariable Long questionId,
            @RequestPart("files") List<MultipartFile> files) {
        List<ProductQuestionPhoto> savedPhotos = photoService.savePhotos(questionId, files);

        return ResponseEntity
                .status(201)
                .body(savedPhotos);
    }

    // 질문의 모든 사진 조회
    @Operation(summary = "질문의 모든 사진 조회", description = "해당 질문의 모든 사진을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{questionId}/photos")
    public ResponseEntity<List<ProductQuestionPhoto>> getPhotos(@PathVariable Long questionId) {
        List<ProductQuestionPhoto> photos = photoService.findByProductQuestionOrderBySortOrder(questionId);
        
        return ResponseEntity
                .status(200)
                .body(photos);
    }

    // 특정 사진 조회
    @Operation(summary = "특정 사진 조회", description = "특정한 사진을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/photos/{photoId}")
    public ResponseEntity<ProductQuestionPhoto> getPhoto(@PathVariable Long photoId) {
        ProductQuestionPhoto photo = photoService.findByProductQuestionPhotoId(photoId);

        return ResponseEntity
                .status(200)
                .body(photo);
    }

    // 사진 삭제
    @Operation(summary = "특정 사진 삭제", description = "특정한 사진을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/photos/{photoId}")
    public ResponseEntity<?> deletePhoto(@PathVariable Long photoId) {
        boolean deleted = photoService.deletePhoto(photoId);
        Map<String, String> response = new HashMap<>();
        if (deleted) {
            response.put("message", "사진이 삭제되었습니다");
        } else {
            response.put("message", "파일 삭제에 실패했지만 DB에서는 제거되었습니다");
        }

        return ResponseEntity
                .status(200)
                .body(response);
    }

    // 질문의 모든 사진 삭제
    @Operation(summary = "질문의 모든 사진 삭제", description = "질문의 모든 사진을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/{questionId}/photos")
    public ResponseEntity<Map<String, String>> deleteAllPhotos(@PathVariable Long questionId) {
        try {
            photoService.deletePhotosByQuestion(questionId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "모든 사진이 삭제되었습니다");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("사진 삭제 실패", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "사진 삭제 중 오류가 발생했습니다");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // 사진 정렬 순서 변경
    @Operation(summary = "사진 정렬 순서 변경", description = "사진의 정렬 순서를 변경합니다.")
    @ApiResponse(responseCode = "200", description = "변경 성공")
    @PutMapping("/photos/{photoId}/sort-order")
    public ResponseEntity<?> updateSortOrder(
            @PathVariable Long photoId,
            @RequestParam int sortOrder) {

        int updatedCount = photoService.updateSortOrder(photoId, sortOrder);

        Map<String, String> response = new HashMap<>();
        if (updatedCount > 0) {
            response.put("message", "정렬 순서가 변경되었습니다");
        } else {
            response.put("error", "존재하지 않는 사진입니다");
            throw new RecordNotFoundException("UpdateSortOrder Error", "존재하지 않는 사진입니다");
        }

        return ResponseEntity
                .status(200)
                .body(response);
    }

    // 이미지 파일 조회 (실제 이미지 반환)
    @Operation(summary = "이미지 파일 조회 (실제 이미지 반환)", description = "이미지 파일을 조회 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/images/{filename}")
    public ResponseEntity<?> getImage(@PathVariable String filename) {
        Resource resource = photoService.loadImage(filename);

        String contentType = determineContentType(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
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
        return gcsImageService.uploadOrdered("question", groupId, files);
    }

    /**
     * 특정 groupId의 이미지들을 업로드 순서대로 조회
     * -> URL 목록을 반환
     */
    @GetMapping("/download/{groupId}")
    public List<ImageUploadResponse.Item> getImages(@PathVariable String groupId) {
        List<ProductQuestionPhoto> records =  photoService
                .findByProductQuestionOrderBySortOrder(Long.getLong(groupId));

        return records.stream()
                .map(r -> new ImageUploadResponse.Item(
                        r.getSortOrder(),
                        r.getUrl(),
                        // Public-read라면
                        "https://storage.googleapis.com/" + "question" + "/" + r.getPhotoId(),
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