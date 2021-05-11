package wooteco.subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.section.SectionRequest;
import wooteco.subway.section.SectionService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;
    private final SectionService sectionService;

    @Autowired
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
    public ResponseEntity<LineResponse> showLineInfo(@PathVariable long id) {
        Line line = lineService.showLineInfo(id);
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
    public ResponseEntity<String> addSection(@RequestBody SectionRequest sectionRequest, @PathVariable long lineId) {
        long upStationId = sectionRequest.getUpStationId();
        long downStationId = sectionRequest.getDownStationId();
        int distance = sectionRequest.getDistance();

        long existStationId = sectionService.findExistStation(lineId, upStationId, downStationId);
        return checkAndAddSection(lineId, existStationId, upStationId, downStationId, distance);
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<String> delete(@PathVariable long lineId, @RequestParam long stationId) {
        checkSectionCount(lineId);
        List<Long> upStationIds = sectionService.findBeforeUpStationId(lineId, stationId);
        List<Long> downStationIds = sectionService.findBeforeDownStationId(lineId, stationId);

        return checkUpDownAndDelete(lineId, upStationIds, downStationIds, stationId);

    }

    private ResponseEntity<String> checkAndAddSection(long lineId, long existStationId, long upStationId, long downStationId, int distance) {
        if (existStationId == upStationId) {
            return addDownStation(lineId, upStationId, downStationId, distance, existStationId);
        }

        if (existStationId == downStationId) {
            return addUpStation(lineId, upStationId, downStationId, distance, existStationId);
        }

        return ResponseEntity.badRequest().build();
    }

    private ResponseEntity<String> addDownStation(long lineId, long upStationId, long downStationId, int distance, long existStationId) {
        List<Long> beforeDownStations = sectionService.findBeforeDownStationId(lineId, upStationId);
        if (beforeDownStations.isEmpty()) {
            sectionService.save(lineId, upStationId, downStationId, distance);
            return ResponseEntity.ok().build();
        }
        long beforeDownStationId = beforeDownStations.get(0);
        int beforeDistance = isAppropriateDistance(distance, sectionService.findBeforeDistance(lineId, existStationId, beforeDownStationId));
        sectionService.addSection(lineId, upStationId, downStationId, beforeDownStationId, distance, beforeDistance - distance);
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<String> addUpStation(long lineId, long upStationId, long downStationId, int distance, long existStationId) {
        List<Long> beforeUpStations = sectionService.findBeforeUpStationId(lineId, downStationId);
        if (beforeUpStations.isEmpty()) {
            sectionService.save(lineId, upStationId, downStationId, distance);
            return ResponseEntity.ok().build();
        }
        long beforeUpStationId = beforeUpStations.get(0);
        int beforeDistance = isAppropriateDistance(distance, sectionService.findBeforeDistance(lineId, beforeUpStationId, existStationId));
        sectionService.addSection(lineId, beforeUpStationId, upStationId, downStationId, beforeDistance - distance, distance);
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<String> checkUpDownAndDelete(long lineId, List<Long> upStationIds, List<Long> downStationIds, long stationId) {
        if (upStationIds.isEmpty()) {
            sectionService.delete(lineId, stationId, downStationIds.get(0));
            return ResponseEntity.ok().build();
        }

        if (downStationIds.isEmpty()) {
            sectionService.delete(lineId, upStationIds.get(0), stationId);
            return ResponseEntity.ok().build();
        }
        return deleteSection(lineId, upStationIds.get(0), downStationIds.get(0), stationId);

    }

    private void checkSectionCount(long lineId) {
        if (sectionService.count(lineId) == 1) {
            throw new IllegalArgumentException("구간이 하나뿐이라 더이상 지울 수 없습니다.");
        }
    }

    private ResponseEntity<String> deleteSection(long lineId, Long upStationId, Long downStationId, long stationId) {
        int firstDistance = sectionService.findBeforeDistance(lineId, upStationId, stationId);
        int secondDistance = sectionService.findBeforeDistance(lineId, stationId, downStationId);

        sectionService.delete(lineId, upStationId, stationId);
        sectionService.delete(lineId, stationId, downStationId);
        sectionService.save(lineId, upStationId, downStationId, firstDistance + secondDistance);
        return ResponseEntity.ok().build();
    }

    private int isAppropriateDistance(int distance, int beforeDistance) {
        if (beforeDistance - distance < 1) {
            throw new IllegalArgumentException("거리를 확인해주세요. 기존 거리보다 길거나 같을 수 없습니다.");
        }
        return beforeDistance;
    }

}
