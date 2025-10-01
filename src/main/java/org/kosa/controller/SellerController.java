package org.kosa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kosa.dto.seller.SellerReq;
import org.kosa.dto.seller.SellerRes;
import org.kosa.dto.signUp.SignUpReq;
import org.kosa.service.SellerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Seller", description = "판매자 관리 API")
@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
@Validated
public class SellerController {

    private final SellerService sellerService;

    @Operation(summary = "전체 판매자 목록 조회", description = "시스템에 등록된 모든 판매자 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("")
    public List<SellerRes> listAll() {
        return sellerService.listAll();
    }

    @Operation(summary = "판매자 정보 조회", description = "Member ID를 사용하여 특정 판매자의 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{memberId}")
    public SellerRes get(@Parameter(description = "조회할 판매자의 회원 ID", required = true) @PathVariable Long memberId) {
        return sellerService.getByMemberId(memberId);
    }

    @Operation(summary = "판매자 등록", description = "기존 회원을 판매자로 등록합니다.")
    @ApiResponse(responseCode = "201", description = "등록 성공")
    @PostMapping("")
    public ResponseEntity<?> signUpSeller(@RequestBody SignUpReq req) {

        SellerRes res = sellerService.create(req);
        return ResponseEntity
                .created(URI.create("/api/sellers/" + res.getMemberId()))
                .body(res);
    }

    @Operation(summary = "판매자 정보 수정", description = "기존 판매자의 정보를 부분적으로 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PatchMapping("/{memberId}")
    public SellerRes update(
            @Parameter(description = "수정할 판매자의 회원 ID", required = true) @PathVariable Long memberId,
            @RequestBody SellerReq req) {
        return sellerService.update(memberId, req);
    }

    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    @GetMapping("/seller/dashboard")
    public ResponseEntity<?> sellerDashboard() {
        return ResponseEntity.ok("판매자만 접근 가능합니다");
    }


}
