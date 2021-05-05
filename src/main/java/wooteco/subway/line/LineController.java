package wooteco.subway.line;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.service.ResponseError;

import java.net.URI;
import java.util.Collections;

@RestController
public class LineController {

    Logger logger = LoggerFactory.getLogger(LineController.class);

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> create(@RequestBody LineSaveRequestDto requestDto) {
        Line newLine = LineDao.save(new Line(requestDto.getName(), requestDto.getColor()));
        LineResponse response = new LineResponse(newLine.id(), newLine.name(), newLine.color(), Collections.emptyList());
        return ResponseEntity.created(URI.create("/lines/" + newLine.id())).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseError> handleException(IllegalArgumentException e) {
        logger.info(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseError(e.getMessage()));
    }
}
