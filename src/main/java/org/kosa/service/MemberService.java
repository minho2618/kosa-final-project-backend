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

    public Page<MemberRes> findAllMember(Pageable pageable) {
        Page<Member> list = memberRepository.findAll(pageable);
        return list.map(MemberRes::toMemberRes);
    }

    public MemberRes findByMemberId(Long id) throws RecordNotFoundException{
        Member fMember = memberRepository.findByMemberId(id).orElseThrow(()->
                new RecordNotFoundException("해당 ID의 회원을 찾을 수 없습니다.", "Not Found Member Id")
        );
        return MemberRes.toMemberRes(fMember);
    }
}
