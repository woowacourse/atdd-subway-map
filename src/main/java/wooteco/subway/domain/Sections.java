package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import wooteco.subway.exception.NotFoundException;

public class Sections {
    private static final String ALREADY_CONTAINS_UP_AND_DOWN_STATIONS = "상행역과 하행역이 이미 모두 노선에 등록되어 있습니다.";
    private static final String NOT_CONTAINS_UP_AND_DOWN_STATIONS = "상행역과 하행역이 모두 노선에 등록되어 있지 않습니다.";
    private static final String STATION_NOT_FOUND_IN_SECTIONS = "해당 노선에서는 입력한 지하철 역을 찾을 수 없습니다.";
    private static final String CAN_NOT_DELETE_MORE = "해당 노선은 더 삭제할 수 없습니다.";

    private final List<Section> value;

    public Sections(List<Section> value) {
        this.value = value;
    }

    public void checkSection(Section inputSection) {
        List<Long> stationIds = convertToStationIds();

        Long inputUpStationId = inputSection.getUpStationId();
        Long inputDownStationId = inputSection.getDownStationId();

        if (stationIds.containsAll(List.of(inputUpStationId, inputDownStationId))) {
            throw new IllegalArgumentException(ALREADY_CONTAINS_UP_AND_DOWN_STATIONS);
        }
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
        List<Long> stationIds = new ArrayList<>();

        for (Section section : value) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }

        return stationIds.stream()
                .distinct()
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Section> findByStationId(long stationId) {
        List<Section> sections = value.stream()
                .filter(section -> section.getUpStationId() == stationId || section.getDownStationId() == stationId)
                .collect(Collectors.toUnmodifiableList());

        if (sections.size() == 0) {
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
