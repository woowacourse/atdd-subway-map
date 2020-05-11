package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.MappedCollection;

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
        boolean isDuplicate = stations.stream()
                .filter(station -> station.isSameId(lineStation))
                .anyMatch(station -> station.isSamePreStationId(lineStation));
        if (isDuplicate) {
            throw new IllegalArgumentException("중복된 구간은 존재하면 안됩니다.");
        }
    }

    private int findNextIndex(LineStation lineStation) {
        if (lineStation.isFirstOnLine()) {
            return FIRST_INDEX;
        }

        int targetIndex = IntStream.range(0, stations.size())
            .filter(index -> stations.get(index).isSameWithPreStationId(lineStation))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("이전역이 존재하지 않습니다."));
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
            .filter(index -> stations.get(index).isSameId(stationId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));
    }

    List<Long> getLineStationsId() {
        return stations.stream()
            .map(LineStation::getStationId)
            .collect(Collectors.toList());
    }

    List<LineStation> getStations() {
        return stations;
    }
}
