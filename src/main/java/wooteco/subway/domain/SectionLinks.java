package wooteco.subway.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SectionLinks {

    private final Map<Long, Long> sections;

    private SectionLinks(Map<Long, Long> sections) {
        this.sections = new HashMap<>(sections);
    }

    public static SectionLinks from(List<Section> sections) {
        Map<Long, Long> stationIds = new HashMap<>();
        for (Section existingSection : sections) {
            stationIds.put(existingSection.getUpStationId(), existingSection.getDownStationId());
        }
        return new SectionLinks(stationIds);
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

    public boolean isNotExistMatchedStation(Section section) {
        return isNotExistStation(section.getUpStationId()) && isNotExistStation(section.getDownStationId());
    }

    public boolean isAllMatchedStation(Section section) {
        return !isNotExistStation(section.getUpStationId()) && !isNotExistStation(section.getDownStationId());
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
