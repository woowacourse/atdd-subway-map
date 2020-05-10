package wooteco.subway.admin.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LineStations {
    private static final Long INIT_PRE_STATION_ID = null;

    private Set<LineStation> lineStations;

    public LineStations(Set<LineStation> lineStations) {
        this.lineStations = lineStations;
    }

    public void addLineStation(LineStation lineStation) {
        validateLineStation(lineStation);
        lineStations.stream()
                .filter(station -> station.isDuplicatedPreStation(lineStation))
                .findAny()
                .ifPresent(station -> station.updatePreLineStation(lineStation.getStationId()));
        lineStations.add(lineStation);
    }

    private void validateLineStation(LineStation lineStation) {
        if (isNotConnectable(lineStation)) {
            throw new IllegalArgumentException("노선에 선행역이 존재하지 않습니다.");
        }
        if (isDuplicated(lineStation)) {
            throw new IllegalArgumentException("이미 노선에 역이 존재합니다.");
        }
    }

    private boolean isDuplicated(LineStation lineStation) {
        return lineStations.stream()
                .anyMatch(station -> station.isSameStation(lineStation));
    }

    private boolean isNotConnectable(LineStation lineStation) {
        return lineStation.isNotFirstStation() && lineStations.stream()
                .noneMatch(station -> station.isNextStation(lineStation));
    }

    public void removeLineStationById(Long stationId) {
        lineStations.stream()
                .filter(station -> station.isSameStationId(stationId))
                .findAny()
                .ifPresent(removedStation -> {
                    updateByRemove(stationId, removedStation);
                    lineStations.remove(removedStation);
                });
    }

    private void updateByRemove(Long stationId, LineStation station) {
        lineStations.stream()
                .filter(nextStation -> nextStation.isSamePreStationId(stationId))
                .findAny()
                .ifPresent(nextStation -> nextStation.updatePreLineStation(station.getPreStationId()));
    }

    public List<Station> createSortedStations(List<Station> stations) {
        List<Station> newStations = new ArrayList<>();
        for (Long id : getLineStationsId()) {
            addByLineStationID(stations, newStations, id);
        }
        return newStations;
    }

    private void addByLineStationID(List<Station> stations, List<Station> newStations, Long id) {
        stations.stream()
                .filter(station -> station.isSameStationId(id))
                .findAny()
                .ifPresent(newStations::add);
    }

    public List<Long> getLineStationsId() {
        List<Long> lineStationsId = new ArrayList<>();
        addStationId(lineStationsId, INIT_PRE_STATION_ID);
        return lineStationsId;
    }

    private void addStationId(List<Long> ids, Long preStationId) {
        lineStations.stream()
                .filter(station -> station.isSamePreStationId(preStationId))
                .findAny()
                .ifPresent(station -> {
                    ids.add(station.getStationId());
                    addStationId(ids, station.getStationId());
                });
    }

    public int size() {
        return lineStations.size();
    }
}
