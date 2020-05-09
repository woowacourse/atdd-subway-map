package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.MappedCollection;

import java.util.ArrayList;
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
        return new LineStations(new ArrayList<>());
    }

    void add(LineStation lineStation) {
        int addIndex = findAddIndex(lineStation);
        update(lineStation, addIndex);
    }

    private int findAddIndex(LineStation lineStation) {
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

        if (isNotLast(targetIndex)) {
            int shouldUpdateIndex = targetIndex + NEXT_STATION_INDEX;
            stations.get(shouldUpdateIndex).updatePreLineStation(lineStation.getStationId());
        }
    }

    private boolean isNotLast(int index) {
        return stations.size() > index + 1;
    }

    void remove(Long stationId) {
        int targetIndex = findRemoveIndex(stationId);

        LineStation station = stations.get(targetIndex);
        if (isNotLast(targetIndex)) {
            int shouldUpdateIndex = targetIndex + NEXT_STATION_INDEX;
            stations.get(shouldUpdateIndex).updatePreLineStation(station.getPreStationId());
        }
        stations.remove(targetIndex);
    }

    private int findRemoveIndex(Long stationId) {
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
