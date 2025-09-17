package org.kosa.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kosa.dto.seller.SellerReq;
import org.kosa.dto.seller.SellerRes;
import org.kosa.service.SellerService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
@Validated
public class SellerController {

    private final SellerService sellerService;

    /** 전체 목록 */
    @GetMapping
    public List<SellerRes> listAll() {
        return sellerService.listAll();
    }

    /** 단건 조회 (memberId = Seller PK) */
    @GetMapping("/{memberId}")
    public SellerRes get(@PathVariable Long memberId) {
        return sellerService.getByMemberId(memberId);
    }

    /** 생성 */
    @PostMapping
    public ResponseEntity<SellerRes> create(@RequestBody @Valid SellerReq req) {
        SellerRes res = sellerService.create(req);
        return ResponseEntity
                .created(URI.create("/api/sellers/" + res.getMemberId()))
                .body(res);
    }

    /** 부분 수정 (필드만 전달된 것 갱신) */
    @PatchMapping("/{memberId}")
    public SellerRes update(@PathVariable Long memberId, @RequestBody SellerReq req) {
        return sellerService.update(memberId, req);
    }

    /** 삭제 */
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> delete(@PathVariable Long memberId) {
        sellerService.delete(memberId);
        return ResponseEntity.noContent().build();
    }
}
/*
사용 예시 (Postman)

생성: POST /api/sellers (JSON Body는 SellerReq 구조)

수정: PATCH /api/sellers/{memberId} (변경할 필드만 보내기)

조회: GET /api/sellers/{memberId}

목록: GET /api/sellers

삭제: DELETE /api/sellers/{memberId}

보안 적용 시 Spring Security에서 /api/sellers/** 접근권한 설정만 잊지 마세요.
 */