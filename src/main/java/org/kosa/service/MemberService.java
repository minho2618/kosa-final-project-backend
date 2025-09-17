package org.kosa.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.kosa.dto.member.MemberRes;
import org.kosa.dto.member.MemberUpdateReq;
import org.kosa.dto.member.SignUpReq;
import org.kosa.entity.Member;
import org.kosa.exception.DuplicateException;
import org.kosa.exception.InvalidInputException;
import org.kosa.exception.RecordNotFoundException;
import org.kosa.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public MemberRes signUp(SignUpReq signUpReq) throws DuplicateException, InvalidInputException {
        if(memberRepository.existsByEmail(signUpReq.getEmail()))
            throw new DuplicateException("중복된 아이디", "Duplicate Email");
        Member rMember = SignUpReq.toMember(signUpReq);
        String pwdEnc = bCryptPasswordEncoder.encode(signUpReq.getPassword());
        rMember.setPassword(pwdEnc);
        Member cMember = memberRepository.save(rMember);
        return MemberRes.toMemberRes(cMember);
    }

    @Transactional
    public MemberRes updateMember(Long id, MemberUpdateReq memberUpdateReq){
        Member uMember = memberRepository.findByMemberId(id).orElseThrow(()->
                new InvalidInputException("잘못된 입력입니다.", "Invalid Input!")
        );
        uMember.setName(memberUpdateReq.getName());
        uMember.setPhoneNum(memberUpdateReq.getPhoneNum());
        uMember.setAddress(memberUpdateReq.getAddress());
        return MemberRes.toMemberRes(uMember);
    }

    @Transactional
    public void deleteMember(Long id){
        memberRepository.softDeleteById(id);
    }

    public Page<MemberRes> findAllMember(Pageable pageable) {
        Page<Member> list = memberRepository.findAllMember(pageable);
        return list.map(MemberRes::toMemberRes);
    }

    public MemberRes findByEmail(String email){
        Member fMember = memberRepository.findByEmail(email);
        if (fMember == null)
            throw  new RecordNotFoundException("해당 Email의 회원을 찾을 수 없습니다.", "Not Found Email");
        return MemberRes.toMemberRes(fMember);
    }
    public MemberRes findByMemberId(Long id) {
        Member fMember = memberRepository.findByMemberId(id).orElseThrow(()->
                new RecordNotFoundException("해당 ID의 회원을 찾을 수 없습니다.", "Not Found Member Id")
        );
        return MemberRes.toMemberRes(fMember);
    }

    public String duplicateCheck(String email) {
        Member rMember = memberRepository.duplicateCheck(email);
        if (rMember == null || rMember.equals("")) return "아이디 사용 가능";
        else return "아이디 사용 불가";
    }
}
