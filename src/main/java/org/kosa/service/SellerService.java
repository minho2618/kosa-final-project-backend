package org.kosa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.dto.seller.SellerReq;
import org.kosa.dto.seller.SellerRes;
import org.kosa.entity.Member;
import org.kosa.entity.Seller;
import org.kosa.enums.SellerRole;
import org.kosa.exception.DuplicateException;
import org.kosa.exception.InvalidInputException;
import org.kosa.exception.RecordNotFoundException;
import org.kosa.repository.MemberRepository;
import org.kosa.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerService {

    private final SellerRepository sellerRepository;
    private final MemberRepository memberRepository;

    // ========= 조회 =========
    @Transactional(readOnly = true)
    public SellerRes getByMemberId(Long memberId) {
        Seller seller = sellerRepository.findById(memberId)
                .orElseThrow(() -> new RecordNotFoundException("판매자 없습니다", "Not Found Seller"));
        return SellerRes.toSellerRes(seller);
    }

    @Transactional(readOnly = true)
    public List<SellerRes> listAll() {
        return sellerRepository.findAll().stream()
                .map(SellerRes::toSellerRes)
                .toList();
    }

    // ========= 생성 =========
    @Transactional
    public SellerRes create(SellerReq req) {
        validateCreate(req);

        Long memberId = req.getMemberId();
        if (sellerRepository.existsById(memberId)) {
            throw new DuplicateException("이미 판매자로 등록된 회원입니다.", "Not Found");
        }

        Member member = ensureMember(memberId);

        SellerRole role = (req.getRole() == null) ? SellerRole.BASIC : req.getRole();

        Seller seller = Seller.builder()
                // @MapsId 구조
                .member(member)
                .memberId(member.getMemberId())
                .sellerName(req.getSellerName())
                .sellerIntro(req.getSellerIntro())
                .sellerRegNo(req.getSellerRegNo())
                .sellerAddress(req.getSellerAddress())
                .postalCode(req.getPostalCode())
                .country(req.getCountry())
                .role(role)
                .build();

        Seller saved = sellerRepository.save(seller);
        log.info("판매자 등록 완료: memberId={}", saved.getMemberId());
        return SellerRes.toSellerRes(saved);
    }

    // ========= 수정(부분 수정) =========
    @Transactional
    public SellerRes update(Long memberId, SellerReq req) {
        Seller entity = sellerRepository.findById(memberId)
                .orElseThrow(() -> new RecordNotFoundException("판매자 없음", "Not Found Seller"));

        applyPatch(entity, req);
        // 더티체킹으로 UPDATE 반영
        return SellerRes.toSellerRes(entity);
    }

    // ========= 삭제 =========
    @Transactional
    public void delete(Long memberId) {
        Seller entity = sellerRepository.findById(memberId)
                .orElseThrow(() -> new RecordNotFoundException("판매자 없음", "Not Found Seller"));
        sellerRepository.delete(entity);
        log.info("판매자 삭제: memberId={}", memberId);
    }

    // ========= 내부 유틸 =========
    private Member ensureMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new RecordNotFoundException("회원 없음", "Not Found Member"));
    }

    private void validateCreate(SellerReq req) {
        if (req == null) throw new InvalidInputException("요청이 비어 있습니다.", "Not Found Seller");
        if (req.getMemberId() == null) throw new InvalidInputException("memberId는 필수입니다.", "Not Found");
        if (req.getSellerName() == null || req.getSellerName().isBlank())
            throw new InvalidInputException("sellerName은 필수입니다.", "Not Found");
    }

    private void applyPatch(Seller entity, SellerReq req) {
        if (req.getSellerName() != null)   entity.setSellerName(req.getSellerName());
        if (req.getSellerIntro() != null)  entity.setSellerIntro(req.getSellerIntro());
        if (req.getSellerRegNo() != null)  entity.setSellerRegNo(req.getSellerRegNo());
        if (req.getSellerAddress() != null)entity.setSellerAddress(req.getSellerAddress());
        if (req.getPostalCode() != null)   entity.setPostalCode(req.getPostalCode());
        if (req.getCountry() != null)      entity.setCountry(req.getCountry());
        if (req.getRole() != null)         entity.setRole(req.getRole());
        // memberId는 PK이자 @MapsId라 수정 대상에서 제외(변경하려면 삭제 후 재생성 정책 권장)
    }
}
