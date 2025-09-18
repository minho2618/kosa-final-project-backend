package org.kosa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.dto.questionAnswer.QuestionAnswerReq;
import org.kosa.service.QuestionAnswerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "QuestionAnswer", description = "1:1 문의 답변 API")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/questionAnswers")
public class QuestionAnswerController {
    private final QuestionAnswerService questionAnswerService;

    @Operation(summary = "1:1 문의 답변 생성", description = "특정 1:1 문의에 대한 답변을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @PostMapping("")
    public ResponseEntity<?> createReply(@RequestBody QuestionAnswerReq req){
        log.info("createReply===>{}", req);
        return new ResponseEntity<>(questionAnswerService.createReply(req), HttpStatus.CREATED);
    }

    @Operation(summary = "문의 ID로 답변 조회", description = "특정 1:1 문의에 달린 답변을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/questionId/{id}")
    public ResponseEntity<?> findReplyByQuestionId(
            @Parameter(description = "답변을 조회할 1:1 문의의 ID", required = true) @PathVariable Long id) {
        log.info("findByQuestionId===>{}", id);
        return new ResponseEntity<>(questionAnswerService.findByQuestionId(id), HttpStatus.OK);
    }

    @Operation(summary = "답변 ID로 답변 조회", description = "답변 ID를 사용하여 특정 답변을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/id/{id}")
    public ResponseEntity<?> findReplyByAnswerId(
            @Parameter(description = "조회할 답변의 ID", required = true) @PathVariable Long id){
        log.info("findByReplyId====>{}", id);
        return new ResponseEntity<>(questionAnswerService.findByAnswerId(id), HttpStatus.OK);
    }

    @Operation(summary = "1:1 문의 답변 수정", description = "기존 답변의 내용을 수정합니다.")
    @ApiResponse(responseCode = "202", description = "수정 성공")
    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateReply(
            @Parameter(description = "수정할 답변의 ID", required = true) @PathVariable Long id,
            @RequestBody QuestionAnswerReq req) {
        log.info("updateReply===>{}", req);
        return new ResponseEntity<>(questionAnswerService.updateReply(id, req), HttpStatus.ACCEPTED);
    }

    @Operation(summary = "1:1 문의 답변 삭제", description = "답변을 시스템에서 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteReply(
            @Parameter(description = "삭제할 답변의 ID", required = true) @PathVariable Long id) {
        log.info("deleteReply===>{}", id);
        questionAnswerService.deleteReply(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}