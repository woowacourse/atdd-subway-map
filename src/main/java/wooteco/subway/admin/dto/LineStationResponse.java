package wooteco.subway.admin.dto;

import java.util.ArrayList;
import java.util.List;

import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;

public class LineStationResponse {
    private Long stationId;
    private String stationsName;
    private Long preStationId;
    private String preStationName;
    private int distance;
    private int duration;

    public LineStationResponse() {
    }

    public LineStationResponse(Long stationId, String stationsName, Long preStationId, String preStationName,
            int distance, int duration) {
        this.stationId = stationId;
        this.stationsName = stationsName;
        this.preStationId = preStationId;
        this.preStationName = preStationName;
        this.distance = distance;
        this.duration = duration;
    }

    public static LineStationResponse of(LineStation lineStation, Station station, Station preStation) {
        return new LineStationResponse(lineStation.getStationId(), station.getName(), lineStation.getPreStationId(),
                preStation.getName(), lineStation.getDistance(), lineStation.getDuration());
    }

    public static LineStationResponse of(LineStation lineStation, Station station) {
        return new LineStationResponse(lineStation.getStationId(), station.getName(), null,
                null, lineStation.getDistance(), lineStation.getDuration());
    }

    public static List<LineStationResponse> listOf(List<LineStation> lineStations, List<Station> stations) {
        List<LineStationResponse> lineStationResponses = new ArrayList<>();
        lineStationResponses.add(LineStationResponse.of(lineStations.get(0), stations.get(0)));
        for (int i = 1; i < lineStations.size(); i++) {
            LineStation lineStation = lineStations.get(i);
            lineStationResponses.add(LineStationResponse.of(lineStation, stations.get(i), stations.get(i - 1)));
        }
        return lineStationResponses;
    }

    public Long getStationId() {
        return stationId;
    }

    public String getStationsName() {
        return stationsName;
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
