package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.LineService;
import wooteco.subway.service.StationService;

import java.net.URI;

@RestController
public class LineController {

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createStation(@RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = LineService.createLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }
}
