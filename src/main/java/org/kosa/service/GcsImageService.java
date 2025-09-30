package org.kosa.service;

import com.google.cloud.storage.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.kosa.dto.image.ImageUploadResponse;
import org.kosa.dto.product.ProductRes;
import org.kosa.entity.*;
import org.kosa.repository.ProductImageRepository;
import org.kosa.repository.ProductQuestionPhotoRepository;
import org.kosa.repository.ProductQuestionRepository;
import org.kosa.repository.ReviewPhotoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GcsImageService {

    private final Storage storage;

    @Value("${app.gcs.bucket}")
    private String bucket;

    @Value("${app.gcs.base-dir}")
    private String baseDir;

    @Value("${app.gcs.public-read:true}")
    private boolean publicRead;

    @Value("${app.gcs.signed-url-exp-seconds:86400}")
    private long signedUrlExpSeconds;

    // (선택) DB에 기록할 때 사용. DB를 쓰지 않는다면 생성자에서 주입 제거.
    //private final Optional<ImageRecordRepository> imageRecordRepository = Optional.empty();
    private final ProductImageRepository productImageRepository;
    private final ProductQuestionPhotoRepository questionPhotoRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;

    private final ProductQuestionRepository productQuestionRepository;

    /**
     * 여러 장의 이미지를 업로드한 "순서"대로 저장하고, 그 순서를 응답에 반영합니다.
     * @param groupId 이 업로드 묶음을 식별할 ID (게시글ID, 주문ID, 혹은 랜덤UUID)
     * @param files 업로드할 이미지 목록(프런트에서 전송 순서가 곧 저장 순서)
     */
    public ImageUploadResponse uploadOrdered(String group, String groupId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        final String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        final String groupPath = sanitize(group + "/" + groupId); // 경로에 쓸 수 있도록 정리
        final String prefix = baseDir.replaceAll("^/+", "").replaceAll("/+$", "");

        List<ImageUploadResponse.Item> items = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);

            // 1) 유효성 검사: 빈 파일, 확장자/MIME 타입
            if (file.isEmpty()) throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
            String original = Optional.ofNullable(file.getOriginalFilename()).orElse("unnamed");
            String ext = FilenameUtils.getExtension(original).toLowerCase(Locale.ROOT);
            if (!List.of("jpg","jpeg","png","gif","webp","bmp","heic","avif").contains(ext)) {
                throw new IllegalArgumentException("허용되지 않은 이미지 형식: " + ext);
            }

            // 2) 객체명: [baseDir]/[yyyy/MM/dd]/[groupId]/[timestamp]_[index(4자리)].ext
            String objectName = String.format(
                    "%s/%s/%s/%d_%s.%s",
                    prefix, datePath, groupPath,
                    System.currentTimeMillis(),
                    String.format("%04d", i), // 순서 고정
                    ext
            );

            // 3) 메타데이터 구성
            BlobInfo.Builder builder = BlobInfo.newBuilder(BlobId.of(bucket, objectName))
                    .setContentType(resolveContentType(ext));

            BlobInfo blobInfo = builder.build();

            // 4) 업로드 (이미 존재하면 덮어쓰기)
            Blob blob = storage.create(
                    blobInfo,
                    getBytes(file) // MultipartFile → byte[]
            );

            // 5) 접근 권한 처리
            String publicUrl = null;
            String signedUrl = null;

            if (publicRead) {
                // 객체 ACL을 공개 읽기 가능으로 설정 (버킷이 uniform access인 경우 버킷 정책 사용 권장)
                try {
                    storage.createAcl(blob.getBlobId(), Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
                } catch (StorageException ignored) {
                    // Uniform bucket-level access가 활성화된 경우 개별 객체 ACL 설정이 막힐 수 있음
                    // 이 때는 버킷 정책(정책 바인딩)으로 공개 설정을 해야 함. 여기서는 무시.
                }
                publicUrl = String.format("https://storage.googleapis.com/%s/%s", bucket, objectName);
            } else {
                // 비공개라면 서명 URL을 발급하여 접근하게 할 수 있음
                URL url = storage.signUrl(
                        blobInfo,
                        // Duration.ofSeconds(signedUrlExpSeconds),
                        3600,
                        TimeUnit.SECONDS
                );
                signedUrl = url.toString();
            }

            // 6) (선택) DB에 순서/메타 기록
            /*imageRecordRepository.ifPresent(repo -> {
                repo.save(ImageRecord.builder()
                        .groupId(groupId)
                        .displayOrder(i)
                        .bucket(bucket)
                        .objectName(objectName)
                        .contentType(blob.getContentType())
                        .size(blob.getSize())
                        .build());
            });*/
            switch (group) {
                case "product":
                    productImageRepository.save(ProductImage
                            .builder()
                            .sortOrder(i)
                            .url(publicRead ? publicUrl : signedUrl)
                            .product(Product.builder()
                                    .productId(Long.getLong(groupId))
                                    .build())
                            .build());
                    break;
                case "question":
                    questionPhotoRepository.save(ProductQuestionPhoto
                            .builder()
                            .sortOrder(i)
                            .url(publicRead ? publicUrl : signedUrl)
                            .productQuestion(productQuestionRepository.findByIdWithDetails(Long.getLong(groupId)))
                            .build());
                    break;
                case "review":
                    reviewPhotoRepository.save(ReviewPhoto
                            .builder()
                            .sortOrder(i)
                            .url(publicRead ? publicUrl : signedUrl)
                            .review(Review.builder()
                                    .reviewId(Long.getLong(groupId))
                                    .build())
                            .build());
                    break;
                default:
            }


            // 7) 응답 아이템 추가
            items.add(new ImageUploadResponse.Item(
                    i,
                    objectName,
                    publicUrl,
                    signedUrl
            ));
        }

        // 안전하게 order로 정렬하여 반환(이미 i 순서대로지만, 방어적 코드)
        items.sort(Comparator.comparingInt(ImageUploadResponse.Item::order));
        return new ImageUploadResponse(groupId, items);
    }

    private static byte[] getBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (Exception e) {
            throw new RuntimeException("파일 읽기 실패: " + file.getOriginalFilename(), e);
        }
    }

    private static String sanitize(String input) {
        // 경로 안전 문자만 허용(영문, 숫자, -, _)
        return input == null ? "unknown" : input.replaceAll("[^a-zA-Z0-9-_]", "_");
    }

    private static String resolveContentType(String ext) {
        return switch (ext) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG_VALUE;
            case "png" -> MediaType.IMAGE_PNG_VALUE;
            case "gif" -> MediaType.IMAGE_GIF_VALUE;
            case "webp" -> "image/webp";
            case "bmp" -> "image/bmp";
            case "heic" -> "image/heic";
            case "avif" -> "image/avif";
            default -> MediaType.APPLICATION_OCTET_STREAM_VALUE;
        };
    }
}
