package wooteco.subway.admin.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LineStations {
    public static final long START_STATION = -1L;

    private Set<LineStation> stations;

    public LineStations() {
        stations = new HashSet<>();
    }

    public LineStations(Set<LineStation> stations) {
        this.stations = stations;
    }

    private LineStation findByPreStationId(Long preStationId) {
        return stations.stream()
            .filter(lineStation -> lineStation.getPreStationId().equals(preStationId))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("LineStation 을 PreStationId 로 찾을 수 없습니다."));
    }

    private LineStation findById(Long stationId) {
        return stations.stream()
            .filter(lineStation -> lineStation.getStationId().equals(stationId))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("LineStation 을 StationId로 찾을 수 없습니다."));
    }

    public void addLineStation(LineStation addLineStation) {
        if (isSamePreStationIdWith(addLineStation.getPreStationId())) {
            LineStation updateLineStation = findByPreStationId(addLineStation.getPreStationId());
            updateLineStation.updatePreLineStation(addLineStation.getStationId());
        }
        this.stations.add(addLineStation);
    }

    public void removeLineStationById(Long stationId) {
        LineStation LineStation = findById(stationId);

        if (isSamePreStationIdWith(stationId)) {
            LineStation nextLineStation = findByPreStationId(LineStation.getStationId());
            nextLineStation.updatePreLineStation(LineStation.getPreStationId());
        }
        this.stations.remove(LineStation);
    }

    public List<Long> getLineStationIds() {
        LineStation firstLineStation = findByPreStationId(START_STATION);

        List<Long> stationIds = new ArrayList<>();
        stationIds.add(firstLineStation.getStationId());

        Long lastStationId = stationIds.get(stationIds.size() - 1);

        while (isSamePreStationIdWith(lastStationId)) {
            stationIds.add(findByPreStationId(lastStationId).getStationId());
            lastStationId = stationIds.get(stationIds.size() - 1);
        }

        return stationIds;
    }

    private boolean isSamePreStationIdWith(Long stationId) {
        return stations.stream()
            .anyMatch(lineStation -> lineStation.getPreStationId().equals(stationId));
    }

    public boolean isStationsEmpty() {
        return stations.isEmpty();
    }

    public Set<LineStation> getStations() {
        return stations;
    }
}
