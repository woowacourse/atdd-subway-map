package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineWithStationsResponse;
import wooteco.subway.admin.service.LineService;

import javax.validation.Valid;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;

@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineWithStationsResponse> createLine(@RequestBody @Valid LineRequest view) {
        Line line = view.toLine();
        Line persistLine = lineService.save(line);

        return ResponseEntity
                .created(URI.create("/lines/" + persistLine.getId()))
                .body(LineWithStationsResponse.of(persistLine));
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineWithStationsResponse>> showLines() {
        /*
        * lineService.showLines() 서비스객체의 리턴값으로 presentation에 그대로 전달하네요.
        이는 뷰와 도메인이 강결합이 될 것 같은데요,
        도메인 객체와 화면에 돌려줄 객체를 분리해보아요. :)
        * */
        List<LineWithStationsResponse> lineWithStationsResponses = lineService.showLines();

        return ResponseEntity
                .ok()
                .body(lineWithStationsResponses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineWithStationsResponse> findLineWithStationsBy(@PathVariable(name = "id") Long id) {
        LineWithStationsResponse lineWithStationsResponse = lineService.findLineWithStationsBy(id);

        return ResponseEntity
                .ok()
                .body(lineWithStationsResponse);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<LineResponse> updateLineBy(@PathVariable(name = "id") Long id, @RequestBody LineRequest view) {
        Line persistLine = lineService.updateLine(id, view.toLine());
        return ResponseEntity
                .ok()
                .body(LineResponse.of(persistLine));
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLineBy(@PathVariable(name = "id") Long id) {
        lineService.deleteLineBy(id);

        return ResponseEntity
                .ok()
                .build();
    }
}
