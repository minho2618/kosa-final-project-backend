package org.kosa.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.kosa.dto.member.SignUpReq;
import org.kosa.entity.Member;
import org.kosa.exception.DuplicateException;
import org.kosa.exception.InvalidInputException;
import org.kosa.exception.RecordNotFoundException;
import org.kosa.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void signUp(SignUpReq signUpReq) throws DuplicateException, InvalidInputException {
        Member cMember = signUpReq.toMember();
        String pwdEnc = bCryptPasswordEncoder.encode(signUpReq.getPassword());
        cMember.setPassword(pwdEnc);
        memberRepository.save(cMember);
    }
}
