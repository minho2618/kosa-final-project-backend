package org.kosa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.dto.question.QuestionReq;
import org.kosa.service.QuestionService;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Question", description = "1:1 문의 API")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionService questionService;

    @Operation(summary = "1:1 문의 생성", description = "새로운 1:1 문의를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @PostMapping("")
    public ResponseEntity<?> createQuestion(@RequestBody QuestionReq req){
        log.info("createQuestion===>{}", req);
        return new ResponseEntity<>(questionService.createQuestion(req), HttpStatus.CREATED);
    }

    @Operation(summary = "1:1 문의 수정", description = "기존 1:1 문의의 제목과 내용을 수정합니다.")
    @ApiResponse(responseCode = "202", description = "수정 성공")
    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateQuestion(
            @Parameter(description = "수정할 문의의 ID", required = true) @PathVariable Long id,
            @RequestBody QuestionReq req){
        log.info("updateQuestion===>{}", id);
        log.info("updateQuestion===>{}", req);
        return new ResponseEntity<>(questionService.updateQuestion(id, req), HttpStatus.ACCEPTED);
    }

    @Operation(summary = "1:1 문의 삭제", description = "1:1 문의와 관련 답변을 함께 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteQuestion(
            @Parameter(description = "삭제할 문의의 ID", required = true) @PathVariable Long id){
        log.info("deleteQuestion===>{}", id);
        questionService.deleteQuestion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "전체 1:1 문의 목록 조회", description = "페이지 단위로 전체 1:1 문의 목록을 조회합니다.")
    @PageableAsQueryParam
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("")
    public ResponseEntity<?> findAllQuestion(
            @Parameter(hidden = true) @PageableDefault(page = 0, size = 10, sort = "questionId") Pageable pageable){
        log.info("findAllQuestion===>");
        return new ResponseEntity<>(questionService.findAllQuestion(pageable), HttpStatus.OK);
    }

    @Operation(summary = "ID로 1:1 문의 조회", description = "문의 ID를 사용하여 특정 1:1 문의를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/id/{id}")
    public ResponseEntity<?> findByQuestionId(
            @Parameter(description = "조회할 문의의 ID", required = true) @PathVariable Long id){
        log.info("findByQuestionId===>{}", id);
        return new ResponseEntity<>(questionService.findByQuestionId(id), HttpStatus.OK);
    }

    @Operation(summary = "제목으로 1:1 문의 검색", description = "제목에 포함된 키워드로 1:1 문의를 검색합니다.")
    @PageableAsQueryParam
    @ApiResponse(responseCode = "200", description = "검색 성공")
    @GetMapping("/title/{title}")
    public ResponseEntity<?> findByTitle(
            @Parameter(description = "검색할 제목 키워드", required = true) @PathVariable String title,
            @Parameter(hidden = true) @PageableDefault(page = 0, size = 10, sort = "questionId") Pageable pageable) {
        log.info("findByTitle===>{}", title);
        return new ResponseEntity<>(questionService.findByTitle(title, pageable), HttpStatus.OK);
    }

    @Operation(summary = "회원 ID로 1:1 문의 목록 조회", description = "특정 회원이 작성한 모든 1:1 문의 목록을 조회합니다.")
    @PageableAsQueryParam
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/memberId/{id}")
    public ResponseEntity<?> findByMemberId(
            @Parameter(description = "문의 목록을 조회할 회원의 ID", required = true) @PathVariable Long id,
            @Parameter(hidden = true) @PageableDefault(page = 0, size = 10, sort = "questionId") Pageable pageable){
        log.info("findByMemberId===>{}", id);
        return new ResponseEntity<>(questionService.findByMemberId(id, pageable), HttpStatus.OK);
    }
}