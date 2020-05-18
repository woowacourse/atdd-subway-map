package wooteco.subway.admin.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice(annotations = RestController.class)
public class ExceptionHandleController {
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorMessage<IllegalArgumentException> handleRuntimeException(IllegalArgumentException e, HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(400);
        return new ErrorMessage<>(e.getMessage(), e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorMessage<MethodArgumentNotValidException> handleValid(MethodArgumentNotValidException e, HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(400);
        return new ErrorMessage<>(e.getBindingResult().getAllErrors().get(0).getDefaultMessage(), e);
    }

    @ExceptionHandler({DataAccessException.class, DbActionExecutionException.class})
    public ErrorMessage<?> handleDBException(Exception e, HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(500);
        return new ErrorMessage<>("데이터 요청 처리 중 오류가 발생했습니다.", e);
    }
}
