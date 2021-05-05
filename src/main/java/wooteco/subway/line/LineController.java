package wooteco.subway.line;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.service.ResponseError;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {
    Logger logger = LoggerFactory.getLogger(LineController.class);

    @PostMapping
    public ResponseEntity<LineResponse> create(@RequestBody LineSaveRequestDto requestDto) {
        Line newLine = LineDao.save(new Line(requestDto.getName(), requestDto.getColor()));
        // TODO : 구간에 포함된 지하철 역 조회 로직
        LineResponse response = new LineResponse(newLine.id(), newLine.name(), newLine.color(), Collections.emptyList());
        return ResponseEntity.created(URI.create("/lines/" + newLine.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        List<Line> lines = LineDao.findAll();
        // TODO : 구간에 포함된 지하철 역 조회 로직
        List<LineResponse> lineResponses = lines.stream()
                .map(it -> new LineResponse(it.id(), it.name(), it.color(), Collections.emptyList()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseError> handleException(IllegalArgumentException e) {
        logger.info(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseError(e.getMessage()));
    }
}
