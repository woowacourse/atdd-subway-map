package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineWithStationsResponse;
import wooteco.subway.service.LineService;

@RestController
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineWithStationsResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineWithStationsResponse lineWithStationsResponse = lineService.createLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineWithStationsResponse.getId()))
                .body(lineWithStationsResponse);
    }

    @GetMapping("/lines")
    public List<LineWithStationsResponse> getAllLines() {
        return lineService.findAllLines();
    }

    @GetMapping("/lines/{lineId}")
    public LineWithStationsResponse getLineById(@PathVariable Long lineId) {
        return lineService.findLineById(lineId);
    }

    @PutMapping("/lines/{lineId}")
    public void updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        lineService.updateLine(lineId, lineRequest);
    }

    @DeleteMapping("/lines/{lineId}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long lineId) {
        lineService.deleteLine(lineId);
        return ResponseEntity.noContent().build();
    }
}
