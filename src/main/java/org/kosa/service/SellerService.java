package org.kosa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.dto.member.MemberSignUpInfo;
import org.kosa.dto.seller.SellerReq;
import org.kosa.dto.seller.SellerRes;
import org.kosa.dto.seller.SellerSignUpInfo;
import org.kosa.dto.signUp.SignUpReq;
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
    public SellerRes create(SignUpReq req) {
        MemberSignUpInfo memberSignUpInfo = req.getMemberSignUpInfo();
        SellerSignUpInfo sellerSignUpInfo = req.getSellerSignUpInfo();
        Member cMember = MemberSignUpInfo.toMember(memberSignUpInfo);
        Member member = memberRepository.save(cMember);
        Seller cSeller = SellerSignUpInfo.toSeller(sellerSignUpInfo);
        cSeller.setMember(member);
        if(sellerSignUpInfo.getSellerRegNo().isEmpty())
            cSeller.setRole(SellerRole.Unauthenticated);
        else
            cSeller.setRole(SellerRole.authenticated);
        Seller seller = sellerRepository.save(cSeller);
        log.info("판매자 등록 완료: memberId={}", seller.getMemberId());
        return SellerRes.toSellerRes(seller);
    }

    // ========= 수정(부분 수정) =========
    @Transactional
    public SellerRes update(Long memberId, SellerReq req) {
        Seller uSeller = sellerRepository.findById(memberId)
                .orElseThrow(() -> new RecordNotFoundException("판매자 없음", "Not Found Seller"));
        applyPatch(uSeller, req);
        // 더티체킹으로 UPDATE 반영
        return SellerRes.toSellerRes(uSeller);
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
        if (req.getSellerAddress() != null)entity.setSellerAddress(req.getSellerAddress());
        if (req.getPostalCode() != null)   entity.setPostalCode(req.getPostalCode());
        if (req.getCountry() != null)      entity.setCountry(req.getCountry());
        // memberId는 PK이자 @MapsId라 수정 대상에서 제외(변경하려면 삭제 후 재생성 정책 권장)
    }
}
