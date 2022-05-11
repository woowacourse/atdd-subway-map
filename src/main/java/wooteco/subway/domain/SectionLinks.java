package wooteco.subway.domain;

import java.util.HashMap;
import java.util.Map;

public class SectionLinks {

    private static final String ALREADY_EXIST_SECTION_STATIONS = "상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.";
    private static final String ALL_SECTION_STATIONS_NOT_EXIST = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.";

    private final Map<Long, Long> sections;

    public SectionLinks(Map<Long, Long> sections) {
        this.sections = new HashMap<>(sections);
    }

    public boolean isExistUpStation(Long id) {
        return sections.containsKey(id);
    }

    public boolean isExistDownStation(Long id) {
        return sections.containsValue(id);
    }

    public boolean isNotExistStation(Long id) {
        return !isExistUpStation(id) && !isExistDownStation(id);
    }

    public boolean isDuplicatedSection(Section section) {
        return !isNotExistStation(section.getUpStationId()) && !isNotExistStation(section.getDownStationId());
    }

    public boolean isNotAddableSection(Section section) {
        return isNotExistStation(section.getUpStationId()) && isNotExistStation(section.getDownStationId());
    }

    public void validateAddableSection(Section section) {
        validateSectionStationsExist(section);
        validateAllSectionStationsExist(section);
    }

    private void validateAllSectionStationsExist(Section section) {
        if (isDuplicatedSection(section)) {
            throw new IllegalStateException(ALREADY_EXIST_SECTION_STATIONS);
        }
    }

    private void validateSectionStationsExist(Section section) {
        if (isNotAddableSection(section)) {
            throw new IllegalStateException(ALL_SECTION_STATIONS_NOT_EXIST);
        }
    }

    public boolean isEndSection(Section section) {
        return (isExistUpStation(section.getDownStationId()) && !isExistDownStation(section.getDownStationId()))
            || (isExistDownStation(section.getUpStationId()) && !isExistUpStation(section.getUpStationId()));
    }

    public boolean isEndStation(Long stationId) {
        return (isExistDownStation(stationId) && !isExistUpStation(stationId))
            || (!isExistDownStation(stationId) && isExistUpStation(stationId));
    }
}
