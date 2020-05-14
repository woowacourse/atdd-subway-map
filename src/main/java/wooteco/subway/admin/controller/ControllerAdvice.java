package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.admin.exception.AlreadyExistNameException;
import wooteco.subway.admin.exception.NotExistIdException;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Model> unExpectedException(Model model) {
        model.addAttribute("error", "개발자도 예측하지 못한 예외입니다!");
        return ResponseEntity.badRequest().body(model);
    }

    @ExceptionHandler(AlreadyExistNameException.class)
    public ResponseEntity<Model> alreadyExistName(Model model, AlreadyExistNameException exception) {
        model.addAttribute("error", exception.getMessage());
        return ResponseEntity.badRequest().body(model);
    }

    @ExceptionHandler(NotExistIdException.class)
    public ResponseEntity<Model> notExistId(Model model, NotExistIdException exception) {
        model.addAttribute("error", exception.getMessage());
        return ResponseEntity.badRequest().body(model);
    }
}
