package org.kosa.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.dto.member.MemberUpdateReq;
import org.kosa.dto.member.SignUpReq;
import org.kosa.service.MemberService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/members")
@CrossOrigin(origins = {"*"}, maxAge = 6000)
public class MemberController {
    private final MemberService memberService;

    @PostMapping("")
    public ResponseEntity<?> signUp(@RequestBody SignUpReq signUpReq){
        log.info("signUp======>{}", signUpReq);
        return new ResponseEntity<>(memberService.signUp(signUpReq), HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<?> findAll(
            @PageableDefault(page = 0, size = 10, sort = "memberId") Pageable pageable){
        log.info("findAllMember!");
        return new ResponseEntity<>(memberService.findAllMember(pageable), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        log.info("findMemberById===>{}", id);
        return new ResponseEntity<>(memberService.findByMemberId(id), HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> findByEmail(@PathVariable String email){
        log.info("findMemberByEmail===>{}", email);
        return new ResponseEntity<>(memberService.findByEmail(email), HttpStatus.OK);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateMember(@PathVariable Long id, @RequestBody MemberUpdateReq req){
        log.info("updateMember, id====>{}", id);
        log.info("updateMember, req====>{}", req);
        return new ResponseEntity<>(memberService.updateMember(id, req), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        log.info("deleteMember====>{}", id);
        memberService.deleteMember(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/check/{email}")
    public String duplicateCheck(@PathVariable String email){
        log.info("duplicateCheck===>{}", email);
        return memberService.duplicateCheck(email);
    }
}
