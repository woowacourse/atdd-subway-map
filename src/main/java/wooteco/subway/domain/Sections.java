package wooteco.subway.domain;

import wooteco.subway.exception.BusinessException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sections {

    private static final int MIN_COUNT_OF_SECTION = 1;
    private static final String NOT_EXIST_STATION = "해당 노선에 존재하는 역이 아닙니다.";
    private static final String ALREADY_ADDED = "이미 등록되어있는 구간입니다.";
    private static final String NO_SECTION = "추가할 수 있는 구간이 없습니다.";
    private static final String TOO_LONG_DISTANCE = "해당 구간의 거리가 추가하려는 구간보다 더 짧습니다.";
    private static final String CAN_NOT_DELETE = "하나의 노선에는 최소 하나의 구간이 필요합니다.";

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public LinkedList<Long> getSortedStationIds() {
        LinkedList<Long> sortedIds = new LinkedList<>();

        Section target = sections.get(0);

        addUpStream(sortedIds, target.getUpStationId());
        sortedIds.add(target.getUpStationId());
        sortedIds.add(target.getDownStationId());
        addDownStream(sortedIds, target.getDownStationId());

        return sortedIds;
    }

    public void validateTarget(Section target) {
        LinkedList<Long> sortedStations = getSortedStationIds();

        if (sortedStations.contains(target.getUpStationId()) && sortedStations.contains(target.getDownStationId())) {
            throw new BusinessException(ALREADY_ADDED);
        }

        if (!sortedStations.contains(target.getUpStationId()) && !sortedStations.contains(target.getDownStationId())) {
            throw new BusinessException(NO_SECTION);
        }
    }

    public boolean isTerminus(Section section) {
        LinkedList<Long> sortedStations = getSortedStationIds();

        return (section.isSameDown(sortedStations.getFirst()) ||
                section.isSameUp(sortedStations.getLast())
        );
    }

    public Section findSource(Section target) {
        Section source = sections.stream()
                .filter(s -> s.isSource(target))
                .findAny()
                .get();

        if (source.isShorterDistance(target)) {
            throw new BusinessException(TOO_LONG_DISTANCE);
        }

        return source;
    }

    public void validateDelete() {
        if (sections.size() == MIN_COUNT_OF_SECTION) {
            throw new BusinessException(CAN_NOT_DELETE);
        }
    }

    public List<Section> findSectionByStationId(Long stationId) {
        List<Section> result = sections.stream()
                .filter(s -> s.isSameUp(stationId) || s.isSameDown(stationId))
                .collect(Collectors.toList());

        if (result.size() == 0) {
            throw new BusinessException(NOT_EXIST_STATION);
        }

        return result;
    }

    private void addUpStream(LinkedList<Long> result, Long key) {
        Map<Long, Long> ids = sections.stream()
                .collect(Collectors.toMap(
                        Section::getDownStationId,
                        Section::getUpStationId
                ));

        while (ids.containsKey(key)) {
            key = ids.get(key);
            result.addFirst(key);
        }
    }

    private void addDownStream(LinkedList<Long> result, Long key) {
        Map<Long, Long> ids = sections.stream()
                .collect(Collectors.toMap(
                        Section::getUpStationId,
                        Section::getDownStationId
                ));

        while (ids.containsKey(key)) {
            key = ids.get(key);
            result.addLast(key);
        }
    }


}
