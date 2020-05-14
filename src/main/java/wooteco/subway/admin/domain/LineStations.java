package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.MappedCollection;
import wooteco.subway.admin.exceptions.DuplicateLineStationException;
import wooteco.subway.admin.exceptions.LineNotFoundException;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LineStations {
    private static final int FIRST_INDEX = 0;
    private static final int NEXT_STATION_INDEX = 1;

    @MappedCollection(keyColumn = "line_key")
    private List<LineStation> stations;

    private LineStations(List<LineStation> stations) {
        this.stations = stations;
    }

    static LineStations createEmpty() {
        return new LineStations(new LinkedList<>());
    }

    void add(LineStation lineStation) {
        validateDuplicate(lineStation);

        int addIndex = findNextIndex(lineStation);
        update(lineStation, addIndex);
    }

    private void validateDuplicate(LineStation lineStation) {
        if (isDuplicate(lineStation)) {
            throw new DuplicateLineStationException(lineStation.getPreStationId(), lineStation.getStationId());
        }
    }

    private boolean isDuplicate(LineStation lineStation) {
        return stations.stream()
                .anyMatch(station -> station.isSame(lineStation) && station.isPreStationSame(lineStation));
    }

    private int findNextIndex(LineStation lineStation) {
        if (lineStation.isFirstOnLine()) {
            return FIRST_INDEX;
        }

        int targetIndex = IntStream.range(0, stations.size())
                .filter(index -> stations.get(index).isPreStationOf(lineStation))
                .findFirst()
                .orElseThrow(() -> new LineNotFoundException(lineStation.getPreStationId()));
        return targetIndex + NEXT_STATION_INDEX;
    }

    private void update(LineStation lineStation, int targetIndex) {
        stations.add(targetIndex, lineStation);

        updateIfNotLast(targetIndex, lineStation.getStationId());
    }

    private void updateIfNotLast(int targetIndex, Long stationId) {
        if (isNotLast(targetIndex)) {
            int shouldUpdateIndex = targetIndex + NEXT_STATION_INDEX;
            stations.get(shouldUpdateIndex).updatePreLineStation(stationId);
        }
    }

    private boolean isNotLast(int index) {
        return stations.size() > index + 1;
    }

    void remove(Long stationId) {
        int targetIndex = findIndex(stationId);

        LineStation station = stations.get(targetIndex);
        updateIfNotLast(targetIndex, station.getStationId());
        stations.remove(targetIndex);
    }

    private int findIndex(Long stationId) {
        return IntStream.range(0, stations.size())
                .filter(index -> stations.get(index).isSame(stationId))
                .findFirst()
                .orElseThrow(() -> new LineNotFoundException(stationId));
    }

    List<Long> getLineStationsId() {
        return stations.stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());
    }

    public List<Station> findMatchingStations(List<Station> stations) {
        List<Long> stationIds = getLineStationsId();

        return stations.stream()
                .filter(station -> stationIds.contains(station.getId()))
                .collect(Collectors.toList());
    }

    List<LineStation> getStations() {
        return stations;
    }
}
