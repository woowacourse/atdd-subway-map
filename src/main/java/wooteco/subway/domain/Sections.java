package wooteco.subway.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections {
    private static final String ALREADY_CONTAINS_UP_AND_DOWN_STATIONS = "상행역과 하행역이 이미 모두 노선에 등록되어 있습니다.";
    private static final String NOT_CONTAINS_UP_AND_DOWN_STATIONS = "상행역과 하행역이 모두 노선에 등록되어 있지 않습니다.";
    private static final String CAN_NOT_DELETE_MORE = "해당 노선은 더 삭제할 수 없습니다.";
    private static final String COMBINE_ONLY_VALUE_LENGTH_IS_TWO = "두개의 노선이 존재할 때에만 병합할 수 있습니다";

    private static final long DEFAULT = -1L;

    private final List<Section> value;

    public Sections(List<Section> value) {
        this.value = value;
    }

    public void checkSection(Section inputSection) {
        List<Long> stationIds = convertToStationIds();

        Long inputUpStationId = inputSection.getUpStationId();
        Long inputDownStationId = inputSection.getDownStationId();

        checkContainsAll(stationIds, List.of(inputUpStationId, inputDownStationId));
        checkNotContains(stationIds, inputUpStationId, inputDownStationId);
    }

    private void checkContainsAll(List<Long> stationIds, List<Long> inputIds) {
        if (stationIds.containsAll(inputIds)) {
            throw new IllegalArgumentException(ALREADY_CONTAINS_UP_AND_DOWN_STATIONS);
        }
    }

    private void checkNotContains(List<Long> stationIds, Long inputUpStationId, Long inputDownStationId) {
        if (!stationIds.contains(inputDownStationId) && !stationIds.contains(inputUpStationId)) {
            throw new IllegalArgumentException(NOT_CONTAINS_UP_AND_DOWN_STATIONS);
        }
    }

    public Optional<Section> getTargetSectionBySection(Section inputSection) {
        return value.stream()
                .filter(section -> section.isSameUpStationId(inputSection) || section.isSameDownStationId(inputSection))
                .findAny();
    }

    public List<Long> convertToStationIds() {
        Long id = value.get(0).getUpStationId();
        LinkedList<Long> result = new LinkedList<>();
        result.add(id);

        checkUpperStations(result, id);
        checkLowerStations(result, id);

        return result.stream()
                .distinct()
                .collect(Collectors.toUnmodifiableList());
    }

    private void checkLowerStations(LinkedList<Long> result, Long id) {
        Map<Long, Long> stationIds = value.stream()
                .collect(Collectors.toMap(Section::getDownStationId, Section::getUpStationId));

        while (stationIds.getOrDefault(id, DEFAULT) != DEFAULT) {
            id = stationIds.get(id);
            result.addFirst(id);
        }
    }

    private void checkUpperStations(LinkedList<Long> result, Long id) {
        Map<Long, Long> stationIds = value.stream()
                .collect(Collectors.toMap(Section::getUpStationId, Section::getDownStationId));

        while (stationIds.getOrDefault(id, DEFAULT) != DEFAULT) {
            id = stationIds.get(id);
            result.addLast(id);
        }
    }

    public Section getTargetSectionByStationId(long stationId) {
        List<Section> sections = value.stream()
                .filter(section -> section.getUpStationId() == stationId || section.getDownStationId() == stationId)
                .collect(Collectors.toUnmodifiableList());

        Sections targetSections = new Sections(sections);
        targetSections.checkSectionsSize();
        return targetSections.mergeSections();
    }

    public void checkCanDelete() {
        if (value.size() == 1) {
            throw new IllegalArgumentException(CAN_NOT_DELETE_MORE);
        }
    }

    private void checkSectionsSize() {
        if (value.size() != 2) {
            throw new IllegalArgumentException(COMBINE_ONLY_VALUE_LENGTH_IS_TWO);
        }
    }

    private Section mergeSections() {
        int newDistance = value.stream()
                .mapToInt(Section::getDistance)
                .sum();

        Section firstSection = value.get(0);
        Section secondSection = value.get(1);

        if (firstSection.isSameDownStationId(secondSection.getUpStationId())) {
            return Section.of(firstSection.getUpStationId(), secondSection.getDownStationId(), newDistance);
        }
        return Section.of(secondSection.getUpStationId(), firstSection.getDownStationId(), newDistance);
    }
}
