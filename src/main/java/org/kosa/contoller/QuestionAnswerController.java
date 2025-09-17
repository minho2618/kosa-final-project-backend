package org.kosa.contoller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.dto.questionAnswer.QuestionAnswerReq;
import org.kosa.service.QuestionAnswerService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/questionAnswers")
public class QuestionAnswerController {
    private final QuestionAnswerService questionAnswerService;

    @PostMapping("")
    public ResponseEntity<?> createReply(@RequestBody QuestionAnswerReq req){
        log.info("createReply===>{}", req);
        return new ResponseEntity<>(questionAnswerService.createReply(req), HttpStatus.CREATED);
    }

    @GetMapping("/questionId/{id}")
    public ResponseEntity<?> findReplyByQuestionId(@PathVariable Long id) {
        log.info("findByQuestionId===>{}", id);
        return new ResponseEntity<>(questionAnswerService.findByQuestionId(id), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findReplyByAnswerId(@PathVariable Long id){
        log.info("findByReplyId====>{}", id);
        return new ResponseEntity<>(questionAnswerService.findByAnswerId(id), HttpStatus.OK);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateReply(@PathVariable Long id, @RequestBody QuestionAnswerReq req) {
        log.info("updateReply===>{}", req);
        return new ResponseEntity<>(questionAnswerService.updateReply(id, req), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteReply(@PathVariable Long id) {
        log.info("deleteReply===>{}", id);
        questionAnswerService.deleteReply(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
