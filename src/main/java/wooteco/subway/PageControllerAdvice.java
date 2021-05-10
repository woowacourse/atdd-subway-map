package wooteco.subway;

import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.xml.transform.Result;
import java.sql.SQLException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class PageControllerAdvice {
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> dataExceptionHandle() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<String> sqlExceptionHandle() {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(DuplicateName.class)
    public ResponseEntity<String> duplicateNameHandle() {
        return ResponseEntity.badRequest().build();
    }
}
