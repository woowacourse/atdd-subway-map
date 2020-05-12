package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.admin.exception.AlreadyExistNameException;
import wooteco.subway.admin.exception.NotExistIdException;

@RestControllerAdvice
public class ControllerAdvice {
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
