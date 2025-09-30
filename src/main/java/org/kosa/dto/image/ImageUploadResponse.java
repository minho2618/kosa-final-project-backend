package org.kosa.dto.image;

import java.util.List;

/**
 * 업로드 결과 응답 DTO
 */
public record ImageUploadResponse(
        String groupId,               // 한 번 업로드한 묶음 식별자(예: 주문ID/게시글ID/랜덤UUID)
        List<Item> items              // 업로드된 각 이미지 정보(순서 포함)
) {
    public record Item(
            int order,               // 업로드 순서(0부터 시작)
            String objectName,       // GCS 객체 경로(버킷 내)
            String publicUrl,        // 공개 URL(선택, public-read=true일 때)
            String signedUrl         // 서명 URL(선택, public-read=false일 때)
    ) {}
}