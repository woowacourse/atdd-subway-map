package wooteco.subway.admin.domain;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LineStations {
    private Set<LineStation> lineStations;

    public LineStations(Set<LineStation> lineStations) {
        this.lineStations = lineStations;
    }

    public void addLineStation(LineStation lineStation) {
        Set<LineStation> newStations = new LinkedHashSet<>();
        int updated = 0;
        for (LineStation station : lineStations) {
            if (lineStation.getPreStationId() == null) {
                updated = 1;
                newStations.add(lineStation);
                newStations.add(station);
                station.updatePreLineStation(lineStation.getStationId());
                continue;
            }
            if (lineStation.getPreStationId().equals(station.getPreStationId())) {
                updated = 1;
                newStations.add(lineStation);
                newStations.add(station);
                station.updatePreLineStation(lineStation.getStationId());
                continue;
            }
            newStations.add(station);
        }
        if (updated == 0) {
            newStations.add(lineStation);
        }
        if (lineStations.size() == 0) {
            newStations.add(lineStation);
        }
        lineStations = newStations;
    }

    public void removeLineStationById(Long stationId) {
        LineStation lineStation = lineStations.stream()
                .filter(station -> station.isSameId(stationId))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
        lineStations.remove(lineStation);
    }

    public List<Long> getLineStationsId() {
        return lineStations.stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());
    }

    public int size() {
        return lineStations.size();
    }
}
