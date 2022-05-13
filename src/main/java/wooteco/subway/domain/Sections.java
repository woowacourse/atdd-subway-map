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
    private static final String INCORRECT_TARGET_SECTIONS_SIZE = "대상 Sections의 크기가 올바르지 않습니다.";

    private static final long DEFAULT = -1L;
    private static final int VALID_SECTIONS_TO_MERGE_SIZE = 2;
    private static final int DELETE_LIMIT = 1;

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

    public Optional<Section> getTargetSectionToInsert(Section inputSection) {
        return value.stream()
                .filter(section -> section.isSameUpStationId(inputSection.getUpStationId())
                        || section.isSameDownStationId(inputSection.getDownStationId()))
                .findAny();
    }

    public List<Long> convertToStationIds() {
        Long id = value.get(0).getUpStationId();
        LinkedList<Long> result = new LinkedList<>();
        result.add(id);

        addUpStations(result, id);
        addDownStations(result, id);

        return result.stream()
                .distinct()
                .collect(Collectors.toUnmodifiableList());
    }

    private void addUpStations(LinkedList<Long> result, Long id) {
        Map<Long, Long> stationIds = value.stream()
                .collect(Collectors.toMap(Section::getDownStationId, Section::getUpStationId));

        while (stationIds.getOrDefault(id, DEFAULT) != DEFAULT) {
            id = stationIds.get(id);
            result.addFirst(id);
        }
    }

    private void addDownStations(LinkedList<Long> result, Long id) {
        Map<Long, Long> stationIds = value.stream()
                .collect(Collectors.toMap(Section::getUpStationId, Section::getDownStationId));

        while (stationIds.getOrDefault(id, DEFAULT) != DEFAULT) {
            id = stationIds.get(id);
            result.addLast(id);
        }
    }

    public void checkCanDelete() {
        if (value.size() <= DELETE_LIMIT) {
            throw new IllegalArgumentException(CAN_NOT_DELETE_MORE);
        }
    }

    public Section getMergedTargetSectionToDelete(long stationId) {
        List<Section> sections = value.stream()
                .filter(section -> section.isSameUpStationId(stationId) || section.isSameDownStationId(stationId))
                .collect(Collectors.toUnmodifiableList());

        Sections targetSections = new Sections(sections);
        targetSections.checkSectionsSize();
        return targetSections.mergeSections();
    }

    private void checkSectionsSize() {
        if (value.size() != VALID_SECTIONS_TO_MERGE_SIZE) {
            throw new IllegalArgumentException(INCORRECT_TARGET_SECTIONS_SIZE);
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
