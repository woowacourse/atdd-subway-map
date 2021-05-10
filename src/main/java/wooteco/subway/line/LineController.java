package wooteco.subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.section.SectionService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        long upStationId = lineRequest.getUpStationId();
        long downStationId = lineRequest.getDownStationId();
        String lineName = lineRequest.getName();
        String lineColor = lineRequest.getColor();
        int distance = lineRequest.getDistance();

        Line line = lineService.createLine(upStationId, downStationId, lineName, lineColor, distance);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(new LineResponse(line));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.showLines().stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable long id) {
        Line line = lineService.showLine(id);
        return ResponseEntity.ok().body(new LineResponse(line));
    }

    @PutMapping("{id}")
    public ResponseEntity<String> updateLine(@RequestBody LineRequest lineRequest, @PathVariable long id) {
        String lineName = lineRequest.getName();
        String lineColor = lineRequest.getColor();
        lineService.updateLine(id, lineName, lineColor);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteLine(@PathVariable long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<String> addSection(@RequestBody LineRequest lineRequest, @PathVariable long lineId) {
        long upStationId = lineRequest.getUpStationId();
        long downStationId = lineRequest.getDownStationId();
        int distance = lineRequest.getDistance();

        long existStationId = sectionService.findExistStation(lineId, upStationId, downStationId);
        if(existStationId == upStationId) {
            return addUpStation(lineId, upStationId, downStationId, distance, existStationId);
        }

        if (existStationId == downStationId) {
            return addDownStation(lineId, upStationId, downStationId, distance, existStationId);
        }

        return ResponseEntity.ok().build();
    }

    private ResponseEntity<String> addDownStation(long lineId, long upStationId, long downStationId, int distance, long existStationId) {
        List<Long> beforeUpStations = sectionService.findBeforeUpStationId(lineId, downStationId);
        if(beforeUpStations.isEmpty()) {
            sectionService.save(lineId, upStationId, downStationId, distance);
            return ResponseEntity.ok().build();
        }
        long beforeUpStationId = beforeUpStations.get(0);
        int beforeDistance = isAppropriateDistance(distance, sectionService.findBeforeDistance(lineId, beforeUpStationId, existStationId));
        sectionService.delete(lineId, beforeUpStationId, downStationId);
        sectionService.save(lineId, beforeUpStationId, upStationId, beforeDistance-distance);
        sectionService.save(lineId, upStationId, downStationId, distance);
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<String> addUpStation(long lineId, long upStationId, long downStationId, int distance, long existStationId) {
        List<Long> beforeStations = sectionService.findBeforeDownStationId(lineId, upStationId);
        if(beforeStations.isEmpty()) {
            sectionService.save(lineId, upStationId, downStationId, distance);
            return ResponseEntity.ok().build();
        }
        long beforeStationId = beforeStations.get(0);
        int beforeDistance = isAppropriateDistance(distance, sectionService.findBeforeDistance(lineId, existStationId, beforeStationId));
        sectionService.delete(lineId, existStationId, beforeStationId);
        sectionService.save(lineId, upStationId, downStationId, distance);
        sectionService.save(lineId, downStationId, beforeStationId, beforeDistance- distance);
        return ResponseEntity.ok().build();
    }

    private int isAppropriateDistance(int distance, int beforeDistance) {
        if (distance - beforeDistance < 1) {
            throw new IllegalArgumentException("거리를 확인해주세요. 기존 거리보다 길거나 같을 수 없습니다.");
        }
        return beforeDistance;
    }

}
