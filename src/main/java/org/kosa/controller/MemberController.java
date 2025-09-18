package org.kosa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.dto.member.MemberRes;
import org.kosa.dto.member.MemberUpdateReq;
import org.kosa.dto.member.SignUpReq;
import org.kosa.service.MemberService;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member", description = "회원 관리 API")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/members")
@CrossOrigin(origins = {"*"}, maxAge = 6000)
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "회원가입", description = "새로운 회원을 시스템에 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = MemberRes.class))),
            @ApiResponse(responseCode = "417", description = "중복된 이메일", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("")
    public ResponseEntity<?> signUp(@RequestBody SignUpReq signUpReq){
        log.info("signUp======>{}", signUpReq);
        return new ResponseEntity<>(memberService.signUp(signUpReq), HttpStatus.CREATED);
    }

    @Operation(summary = "전체 회원 목록 조회", description = "시스템에 등록된 모든 활성 회원을 페이지 단위로 조회합니다.")
    @PageableAsQueryParam
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("")
    public ResponseEntity<?> findAll(
            @Parameter(hidden = true) @PageableDefault(page = 0, size = 10, sort = "memberId") Pageable pageable){
        log.info("findAllMember!");
        return new ResponseEntity<>(memberService.findAllMember(pageable), HttpStatus.OK);
    }

    @Operation(summary = "ID로 회원 조회", description = "회원 ID를 이용하여 특정 회원의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = MemberRes.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(@Parameter(description = "조회할 회원의 ID", required = true) @PathVariable Long id){
        log.info("findMemberById===>{}", id);
        return new ResponseEntity<>(memberService.findByMemberId(id), HttpStatus.OK);
    }

    @Operation(summary = "Email로 회원 조회", description = "회원 이메일을 이용하여 특정 회원의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = MemberRes.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<?> findByEmail(@Parameter(description = "조회할 회원의 이메일", required = true) @PathVariable String email){
        log.info("findMemberByEmail===>{}", email);
        return new ResponseEntity<>(memberService.findByEmail(email), HttpStatus.OK);
    }

    @Operation(summary = "회원 정보 수정", description = "기존 회원의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "수정 성공", content = @Content(schema = @Schema(implementation = MemberRes.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateMember(
            @Parameter(description = "수정할 회원의 ID", required = true) @PathVariable Long id,
            @RequestBody MemberUpdateReq req){
        log.info("updateMember, id====>{}", id);
        log.info("updateMember, req====>{}", req);
        return new ResponseEntity<>(memberService.updateMember(id, req), HttpStatus.ACCEPTED);
    }

    @Operation(summary = "회원 탈퇴", description = "회원을 시스템에서 논리적으로 삭제합니다 (Soft Delete).")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteMember(@Parameter(description = "삭제할 회원의 ID", required = true) @PathVariable Long id) {
        log.info("deleteMember====>{}", id);
        memberService.deleteMember(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "이메일 중복 확인", description = "회원가입 시 이메일이 중복되는지 확인합니다.")
    @ApiResponse(responseCode = "200", description = "확인 성공", content = @Content(schema = @Schema(type = "string", example = "아이디 사용 가능")))
    @GetMapping("/check/{email}")
    public String duplicateCheck(@Parameter(description = "중복 확인할 이메일", required = true) @PathVariable String email){
        log.info("duplicateCheck===>{}", email);
        return memberService.duplicateCheck(email);
    }
}