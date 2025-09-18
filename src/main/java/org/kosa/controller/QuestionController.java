package org.kosa.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.dto.question.QuestionReq;
import org.kosa.service.QuestionService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping("")
    public ResponseEntity<?> createQuestion(@RequestBody QuestionReq req){
        log.info("createQuestion===>{}", req);
        return new ResponseEntity<>(questionService.createQuestion(req), HttpStatus.CREATED);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long id, @RequestBody QuestionReq req){
        log.info("updateQuestion===>{}", id);
        log.info("updateQuestion===>{}", req);
        return new ResponseEntity<>(questionService.updateQuestion(id, req), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id){
        log.info("deleteQuestion===>{}", id);
        questionService.deleteQuestion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("")
    public ResponseEntity<?> findAllQuestion(@PageableDefault(page = 0, size = 10, sort = "questionId") Pageable pageable){
        log.info("findAllQuestion===>");
        return new ResponseEntity<>(questionService.findAllQuestion(pageable), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findByQuestionId(@PathVariable Long id){
        log.info("findByQuestionId===>{}", id);
        return new ResponseEntity<>(questionService.findByQuestionId(id), HttpStatus.OK);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<?> findByTitle(@PathVariable String title, @PageableDefault(page = 0, size = 10, sort = "questionId") Pageable pageable) {
        log.info("findByTitle===>{}", title);
        return new ResponseEntity<>(questionService.findByTitle(title, pageable), HttpStatus.OK);
    }

    @GetMapping("/memberId/{id}")
    public ResponseEntity<?> findByMemberId(@PathVariable Long id, @PageableDefault(page = 0, size = 10, sort = "questionId") Pageable pageable){
        log.info("findByMemberId===>{}", id);
        return new ResponseEntity<>(questionService.findByMemberId(id, pageable), HttpStatus.OK);
    }
}
