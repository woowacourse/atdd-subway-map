package wooteco.subway.admin.controller;

import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.admin.exception.AlreadyExistNameException;
import wooteco.subway.admin.exception.NotExistIdException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(DbActionExecutionException.class)
    public ResponseEntity<Model> dbUniqueException(Model model) {
        model.addAttribute("error", "중복된 역 이름을 넣었습니다!");
        return ResponseEntity.badRequest().body(model);
    }

    @ExceptionHandler(AlreadyExistNameException.class)
    public ResponseEntity<Model> alreadyExistName(Model model) {
        model.addAttribute("error", "중복된 노선 이름을 넣었습니다!");
        return ResponseEntity.badRequest().body(model);
    }

    @ExceptionHandler(NotExistIdException.class)
    public ResponseEntity<Model> notExistId(Model model) {
        model.addAttribute("error", "중복된 노선 이름을 넣었습니다!");
        return ResponseEntity.badRequest().body(model);
    }
}
