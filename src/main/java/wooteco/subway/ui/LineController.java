package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.application.LineService;
import wooteco.subway.dto.LineRequest;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService = new LineService();

    @PostMapping
    public ResponseEntity<?> createLines(@RequestBody LineRequest lineRequest) {

        lineService.save(lineRequest.getName(), lineRequest.getColor());

        return ResponseEntity.badRequest().build();
    }
}
