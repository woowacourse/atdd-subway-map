package wooteco.subway.admin.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.relational.core.mapping.MappedCollection;

import wooteco.subway.admin.exception.DuplicateLineStationException;
import wooteco.subway.admin.exception.EntityNotFoundException;

public class LineStations {
    private static final int FIRST_LINE_STATION_INDEX = 0;

    @MappedCollection(keyColumn = "line_key")
    private List<LineStation> lineStations;

    public LineStations() {
        this.lineStations = new ArrayList<>();
    }

    public LineStations(List<LineStation> lineStations) {
        this.lineStations = lineStations;
    }

    public void add(LineStation lineStation) {
        validateDuplicate(lineStation);
        if (lineStation.hasPreStation()) {
            addAfterFirst(lineStation);
        } else {
            addLineStationAtFirst(lineStation);
        }
    }

    public void removeById(Long stationId) {
        LineStation lineStation = findLineStationByStationId(stationId);
        int targetIndex = lineStations.indexOf(lineStation);
        if (targetIndex != lineStations.size() - 1) {
            int nextIndex = targetIndex + 1;
            LineStation nextLineStation = lineStations.get(nextIndex);
            nextLineStation.updatePreLineStation(lineStations.get(targetIndex).getPreStationId());
        }
        lineStations.remove(lineStation);
    }

    public List<LineStation> getAll() {
        return lineStations;
    }

    public List<Long> getIds() {
        return lineStations.stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());
    }

    private void validateDuplicate(LineStation lineStation) {
        if (hasDuplicate(lineStation)) {
            throw new DuplicateLineStationException();
        }
    }

    private boolean hasDuplicate(LineStation lineStation) {
        return lineStations.stream()
                .anyMatch(station -> station.isSameStation(lineStation));
    }

    private void addAfterFirst(LineStation lineStation) {
        for (int index = FIRST_LINE_STATION_INDEX; index < lineStations.size(); index++) {
            LineStation currentLineStation = lineStations.get(index);
            int nextIndex = index + 1;
            if (containsPreStationInBetween(lineStation, currentLineStation, nextIndex)) {
                addLineInBetween(lineStation, nextIndex);
                break;
            }
            if (containsPreStationAtLast(lineStation, currentLineStation, nextIndex)) {
                lineStations.add(lineStation);
                break;
            }
        }
    }

    private void addLineStationAtFirst(LineStation lineStation) {
        lineStations.add(FIRST_LINE_STATION_INDEX, lineStation);
        if (lineStations.size() > 1) {
            LineStation nextLineStation = lineStations.get(FIRST_LINE_STATION_INDEX + 1);
            nextLineStation.updatePreLineStation(lineStation.getStationId());
        }
    }

    private boolean containsPreStationInBetween(LineStation lineStation, LineStation currentLineStation,
            int nextIndex) {
        return currentLineStation.isSameStationId(lineStation.getPreStationId()) && nextIndex < lineStations.size();
    }

    private boolean containsPreStationAtLast(LineStation lineStation, LineStation currentLineStation, int nextIndex) {
        return currentLineStation.isSameStationId(lineStation.getPreStationId())
                && nextIndex == lineStations.size();
    }

    private void addLineInBetween(LineStation lineStation, int nextIndex) {
        LineStation target = lineStations.get(nextIndex);
        target.updatePreLineStation(lineStation.getStationId());
        lineStations.add(nextIndex, lineStation);
    }

    private LineStation findLineStationByStationId(Long stationId) {
        return lineStations.stream()
                .filter(station -> station.isSameStationId(stationId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("노선에 속한 역을 찾을 수 없습니다."));
    }
}
