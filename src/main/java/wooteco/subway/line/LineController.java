package wooteco.subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        long upStationId = lineRequest.getUpStationId();
        long downStationId = lineRequest.getDownStationId();
        String lineName = lineRequest.getName();
        String lineColor = lineRequest.getColor();

        LineResponse lineResponse = lineService.createLine(upStationId, downStationId, lineName, lineColor);

        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.showLines();
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable long id) {
        LineResponse lineResponse = lineService.showLine(id);
        return ResponseEntity.ok().body(lineResponse);
    }

//    @PutMapping("{id}")
//    public ResponseEntity updateLine(@RequestBody LineRequest lineRequest, @PathVariable long id) {
//        Optional<Line> validLine = LineDao.findById(id);
//        if(!validLine.isPresent()){
//            throw new IllegalArgumentException("노선 업데이트에 실패하였습니다.");
//        }
//
//        Line line = validLine.get();
//        Line newLine = new Line(line.getId(), lineRequest.getName(), lineRequest.getColor(), line.getStations());
//
//        LineDao.delete(line);
//        LineDao.add(newLine);
//
//        return ResponseEntity.ok().build();
//    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteLine(@PathVariable long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }
}
