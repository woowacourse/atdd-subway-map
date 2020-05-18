package wooteco.subway.admin.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.relational.core.mapping.MappedCollection;

import wooteco.subway.admin.exception.InvalidLineStationException;

public class LineStations {
    @MappedCollection(idColumn = "line_id", keyColumn = "index")
    private final List<LineStation> lineStations;

    public LineStations(List<LineStation> lineStations) {
        this.lineStations = new LinkedList<>(lineStations);
    }

    public int size() {
        return lineStations.size();
    }

    public void add(LineStation lineStation) {
        validateHavingSame(lineStation);
        int stationsSize = lineStations.size();
        int insertIndex = IntStream.range(0, stationsSize)
            .filter(index -> isPreStation(index, lineStation.getPreStationId()))
            .findAny()
            .orElse(stationsSize);
        lineStations.add(insertIndex, lineStation);
        updatePreLineStation(insertIndex, lineStation.getStationId());
    }

    private void validateHavingSame(LineStation lineStation) {
        if (isExistLineStation(lineStation)) {
            throw new InvalidLineStationException("이미 등록된 구간입니다");
        }
    }

    private boolean isPreStation(int index, Long preStationId) {
        return lineStations.get(index).isPreStation(preStationId);
    }

    private boolean isExistLineStation(LineStation lineStation) {
        return lineStations.stream()
            .anyMatch(station -> station.isSameStation(lineStation));
    }

    private void updatePreLineStation(int index, Long stationId) {
        if (lineStations.size() - 1 == index) {
            return;
        }
        LineStation lineStation = lineStations.get(index + 1);
        lineStation.updatePreLineStation(stationId);
    }

    public void removeLineStationById(Long stationId) {
        int removeIndex = IntStream.range(0, lineStations.size())
            .filter(index -> isBaseStation(index, stationId))
            .findAny()
            .orElseThrow(() -> new InvalidLineStationException("id를 찾을 수 없습니다."));
        if (lineStations.size() - 1 != removeIndex) {
            LineStation lineStation = lineStations.get(removeIndex);
            updatePreLineStation(removeIndex + 1, lineStation.getPreStationId());
        }
        lineStations.remove(removeIndex);
    }

    private boolean isBaseStation(int index, Long baseStationId) {
        return lineStations.get(index).isBaseStation(baseStationId);
    }

    public List<Long> makeLineStationsIds() {
        return lineStations.stream()
            .map(LineStation::getStationId)
            .collect(Collectors.toList());
    }
}
