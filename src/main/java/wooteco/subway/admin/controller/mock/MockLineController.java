package wooteco.subway.admin.controller.mock;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lines")
public class MockLineController {
 /*
	private static Map<Long, Station> stations = new HashMap<>();
	private static Map<Long, List<Long>> lineStations = new HashMap<>();

	static {
		stations.put(1L, new Station("잠실역"));
		stations.put(2L, new Station("종합운동장역"));
	}

    @PostMapping("{lineId}/stations/{stationId}")
    public ResponseEntity<Void> addStationToLine(@PathVariable Long lineId,
        @PathVariable Long stationId) {
        List<Long> stations = new ArrayList<>();
        stations.add(stationId);
        lineStations.put(lineId, stations);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    @GetMapping("{lineId}/stations")
    public ResponseEntity<List<StationResponse>> findStationsByLineId(@PathVariable Long lineId) {
        List<StationResponse> stationsResponses = lineStations.get(lineId).stream()
            .map(stationId -> stations.get(stationId))
            .map(StationResponse::of)
            .collect(Collectors.toList());
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(stationsResponses);
    }

    @DeleteMapping("{lineId}/stations/{stationsId}")
    public ResponseEntity<Void> deleteStationByLineId(@PathVariable Long lineId,
        @PathVariable Long stationsId) {
        List<Long> stations = lineStations.get(lineId);
        stations.remove(stationsId);
        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
    }

    private Map<Long, Line> lines = new HashMap<>();

    @GetMapping("")
    public ResponseEntity<List<LineResponse>> getLines() {
        List<LineResponse> lineResponses = new ArrayList<>();
        for (Long id : lines.keySet()) {
            Line line = lines.get(id);
            lineResponses.add(new LineResponse(id, line.getName(), line.getColor(),
                line.getStartTime(), line.getEndTime(), line.getIntervalTime(), line.getCreatedAt()
                , line.getUpdatedAt(), new HashSet<>()));
        }
        return new ResponseEntity<>(lineResponses, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<Void> createLine(@RequestBody LineRequest request) {
        Line line = new Line(request.getName(), request.getColor(), request.getStartTime(),
            request.getEndTime(),
            request.getIntervalTime());
        lines.put((long) lines.size() + 1, line);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> findById(@PathVariable Long id) {
        Line line = lines.get(id);
        LineResponse lineResponse = new LineResponse(id, line.getName(), line.getColor(),
            line.getStartTime(), line.getEndTime(), line.getIntervalTime(), line.getCreatedAt()
            , line.getUpdatedAt(), new HashSet<>());
        return new ResponseEntity<>(lineResponse, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLines(@PathVariable Long id,
        @RequestBody LineRequest request) {

        Line line = lines.get(id);
        Line dummyLine = new Line(line.getName(), request.getColor(), request.getStartTime(),
            request.getEndTime(), request.getIntervalTime());

        line.update(dummyLine);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lines.remove(id);
        return ResponseEntity.ok().build();
    }
     */
}
