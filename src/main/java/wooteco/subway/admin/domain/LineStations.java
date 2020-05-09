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
                .ifPresent(station -> lineStations.remove(station));
    }

    public List<Long> getLineStationsId() {
        List<Long> lineStationsId = new ArrayList<>();
        sort(lineStationsId, INIT_PRE_STATION_ID);
        return lineStationsId;
    }

    private void sort(List<Long> ids, Long preStationId) {
        lineStations.stream()
                .filter(station -> station.isSamePreStationId(preStationId))
                .findAny()
                .ifPresent(station -> {
                    ids.add(station.getStationId());
                    sort(ids, station.getStationId());
                });
    }

    public int size() {
        return lineStations.size();
    }
}
