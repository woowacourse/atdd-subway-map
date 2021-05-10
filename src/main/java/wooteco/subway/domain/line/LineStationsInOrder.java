package wooteco.subway.domain.line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.springframework.http.HttpStatus;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.HttpException;

public class LineStationsInOrder {
    private final List<Section> sectionsOfLine;

    public LineStationsInOrder(List<Section> sectionsOfLine) {
        this.sectionsOfLine = new ArrayList<>(sectionsOfLine);
    }

    public List<Long> getStationIdsInOrder() {
        Section randomSection = sectionsOfLine.get(0);
        List<Long> stationIdsInOrder = new LinkedList<>(Arrays.asList(randomSection.getUpStationId(), randomSection.getDownStationId()));
        setStationIdsInOrder(sectionsOfLine, stationIdsInOrder);
        return stationIdsInOrder;
    }

    private void setStationIdsInOrder(List<Section> sectionsOfLine, List<Long> stationIdsInOrder) {
        int numberOfStationsInLine = sectionsOfLine.size() + 1;
        while (stationIdsInOrder.size() < numberOfStationsInLine) {
            checkStationsOfLineAndSetIdsInOrder(sectionsOfLine, stationIdsInOrder);
        }
    }

    private void checkStationsOfLineAndSetIdsInOrder(List<Section> sectionsOfLine, List<Long> stationIdsInOrder) {
        Long firstId = stationIdsInOrder.get(0);
        Long lastId = stationIdsInOrder.get(stationIdsInOrder.size() - 1);
        for (Section section : sectionsOfLine) {
            firstId = addStationIdToFirst(stationIdsInOrder, firstId, section);
            lastId = addStationIdToLast(stationIdsInOrder, lastId, section);
        }
    }

    private Long addStationIdToFirst(List<Long> stationIdsInOrder, Long firstId, Section section) {
        if (section.getDownStationId().equals(firstId)) {
            stationIdsInOrder.add(0, section.getUpStationId());
            firstId = section.getUpStationId();
        }
        return firstId;
    }

    private Long addStationIdToLast(List<Long> stationIdsInOrder, Long lastId, Section section) {
        if (section.getUpStationId().equals(lastId)) {
            stationIdsInOrder.add(section.getDownStationId());
            lastId = section.getDownStationId();
        }
        return lastId;
    }

    public List<Station> getOrderedStationsByOrderedIds(List<Station> stationsInAnyOrder, List<Long> stationIdsInOrder) {
        List<Station> stationsInOrder = new ArrayList<>();
        for (Long stationId : stationIdsInOrder) {
            Station station = getStationById(stationsInAnyOrder, stationId);
            stationsInOrder.add(station);
        }
        return stationsInOrder;
    }

    private Station getStationById(List<Station> stationsInAnyOrder, Long stationId) {
        return stationsInAnyOrder.stream()
            .filter(station -> station.getId().equals(stationId))
            .findFirst()
            .orElseThrow(() -> new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "정렬된 id로 Station 정렬 에러"));
    }
}
