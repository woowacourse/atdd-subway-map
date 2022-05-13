package wooteco.subway.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import wooteco.subway.exception.NotFoundException;

public class Sections {
    private static final String ALREADY_CONTAINS_UP_AND_DOWN_STATIONS = "상행역과 하행역이 이미 모두 노선에 등록되어 있습니다.";
    private static final String NOT_CONTAINS_UP_AND_DOWN_STATIONS = "상행역과 하행역이 모두 노선에 등록되어 있지 않습니다.";
    private static final String STATION_NOT_FOUND_IN_SECTIONS = "해당 노선에서는 입력한 지하철 역을 찾을 수 없습니다.";
    private static final String CAN_NOT_DELETE_MORE = "해당 노선은 더 삭제할 수 없습니다.";

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

    public Optional<Section> getTargetSection(Section inputSection) {
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

    public List<Section> findByStationId(long stationId) {
        List<Section> sections = value.stream()
                .filter(section -> section.getUpStationId() == stationId || section.getDownStationId() == stationId)
                .collect(Collectors.toUnmodifiableList());

        if (sections.isEmpty()) {
            throw new NotFoundException(STATION_NOT_FOUND_IN_SECTIONS);
        }
        return sections;
    }

    public void checkCanDelete() {
        if (value.size() == 1) {
            throw new IllegalArgumentException(CAN_NOT_DELETE_MORE);
        }
    }

    public int size() {
        return value.size();
    }
}
