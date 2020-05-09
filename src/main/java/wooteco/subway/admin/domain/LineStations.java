package wooteco.subway.admin.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class LineStations {
    public static final Long INIT_PRE_STATION_ID = null;
    private Set<LineStation> lineStations;

    public LineStations(Set<LineStation> lineStations) {
        this.lineStations = lineStations;
    }

    public void addLineStation(LineStation lineStation) {
        lineStations.stream()
                .filter(station -> Objects.equals(station.getPreStationId(), lineStation.getPreStationId()))
                .findAny()
                .ifPresent(station -> station.updatePreLineStation(lineStation.getStationId()));
        lineStations.add(lineStation);
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
                .filter(station -> Objects.equals(station.getId(), id))
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
