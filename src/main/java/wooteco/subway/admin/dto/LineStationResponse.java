package wooteco.subway.admin.dto;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;

public class LineStationResponse {
    private Long id;
    private String name;
    private Long preStationId;
    private String preStationName;
    private int distance;
    private int duration;

    private LineStationResponse() {
    }

    public LineStationResponse(Long id, String name, Long preStationId, String preStationName,
            int distance, int duration) {
        this.id = id;
        this.name = name;
        this.preStationId = preStationId;
        this.preStationName = preStationName;
        this.distance = distance;
        this.duration = duration;
    }

    public static LineStationResponse of(LineStation lineStation, Station station, Station preStation) {
        if (Objects.isNull(preStation)) {
            return of(lineStation, station);
        }
        return new LineStationResponse(lineStation.getStationId(), station.getName(), lineStation.getPreStationId(),
                preStation.getName(), lineStation.getDistance(), lineStation.getDuration());
    }

    private static LineStationResponse of(LineStation lineStation, Station station) {
        return new LineStationResponse(lineStation.getStationId(), station.getName(), null,
                null, lineStation.getDistance(), lineStation.getDuration());
    }

    public static List<LineStationResponse> listOf(List<LineStation> lineStations, Map<Long, Station> stations) {
        if (lineStations.isEmpty()) {
            return Collections.emptyList();
        }
        return generateLineStations(lineStations, stations);
    }

    private static List<LineStationResponse> generateLineStations(List<LineStation> lineStations,
            Map<Long, Station> stations) {
        return lineStations.stream()
                .map(lineStation -> LineStationResponse.of(lineStation,
                        stations.get(lineStation.getStationId()),
                        stations.get(lineStation.getPreStationId())))
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public String getPreStationName() {
        return preStationName;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }
}
