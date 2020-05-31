package wooteco.subway.admin.domain;

import java.util.*;

public class LineStations {
    private Set<LineStation> stations;

    public LineStations(Set<LineStation> stations) {
        this.stations = stations;
    }

    public static LineStations empty() {
        return new LineStations(new HashSet<>());
    }

    public Set<LineStation> getStations() {
        return stations;
    }

    public void add(LineStation targetLineStation) {
        updatePreStationOfNextLineStation(targetLineStation.getPreStation(), targetLineStation.getStation());
        stations.add(targetLineStation);
    }

    private void remove(LineStation targetLineStation) {
        updatePreStationOfNextLineStation(targetLineStation.getStation(), targetLineStation.getPreStation());
        stations.remove(targetLineStation);
    }

    public void removeById(Long targetStationId) {
        extractByStationId(targetStationId)
                .ifPresent(this::remove);
    }

    public List<Long> getStationIds() {
        List<Long> result = new ArrayList<>();
        extractNext(null, result);
        return result;
    }

    private void extractNext(Long preStationId, List<Long> ids) {
        stations.stream()
                .filter(it -> Objects.equals(it.getPreStation(), preStationId))
                .findFirst()
                .ifPresent(it -> {
                    Long nextStationId = it.getStation();
                    ids.add(nextStationId);
                    extractNext(nextStationId, ids);
                });
    }

    private void updatePreStationOfNextLineStation(Long targetStationId, Long newPreStationId) {
        extractByPreStationId(targetStationId)
                .ifPresent(it -> it.updatePreLineStation(newPreStationId));
    }

    private Optional<LineStation> extractByStationId(Long stationId) {
        return stations.stream()
                .filter(it -> Objects.equals(it.getStation(), stationId))
                .findFirst();
    }

    private Optional<LineStation> extractByPreStationId(Long preStationId) {
        return stations.stream()
                .filter(it -> Objects.equals(it.getPreStation(), preStationId))
                .findFirst();
    }

    public int getTotalDistance() {
        return stations.stream().mapToInt(it -> it.getDistance()).sum();
    }

    public int getTotalDuration() {
        return stations.stream().mapToInt(it -> it.getDuration()).sum();
    }
}
