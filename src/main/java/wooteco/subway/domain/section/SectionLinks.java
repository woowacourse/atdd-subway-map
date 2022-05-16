package wooteco.subway.domain.section;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SectionLinks {

    private static final int FIRST_UP_STATION_INDEX = 0;

    private final Map<Long, Long> sections;

    private SectionLinks(Map<Long, Long> sections) {
        this.sections = new LinkedHashMap<>(sections);
    }

    static SectionLinks from(List<Section> sections) {
        Map<Long, Long> stationIds = new LinkedHashMap<>();
        for (Section existingSection : sections) {
            stationIds.put(existingSection.getUpStationId(), existingSection.getDownStationId());
        }
        return new SectionLinks(stationIds);
    }

    public List<Long> getAllStationId() {
        List<Long> ids = new ArrayList<>(sections.keySet());
        ids.removeAll(sections.values());
        Long upStationId = ids.get(FIRST_UP_STATION_INDEX);
        while (sections.containsKey(upStationId)) {
            Long downStationId = sections.get(upStationId);
            ids.add(downStationId);
            upStationId = downStationId;
        }
        return ids;
    }

    boolean isNotExistMatchedStation(Section section) {
        return isNotExistStation(section.getUpStationId()) && isNotExistStation(section.getDownStationId());
    }

    boolean isAllMatchedStation(Section section) {
        return !isNotExistStation(section.getUpStationId()) && !isNotExistStation(section.getDownStationId());
    }

    boolean isEndSection(Section section) {
        return (isExistUpStation(section.getDownStationId()) && !isExistDownStation(section.getDownStationId()))
            || (isExistDownStation(section.getUpStationId()) && !isExistUpStation(section.getUpStationId()));
    }

    boolean isEndStation(Long stationId) {
        return (isExistDownStation(stationId) && !isExistUpStation(stationId))
            || (!isExistDownStation(stationId) && isExistUpStation(stationId));
    }

    boolean isNotExistStation(Long id) {
        return !isExistUpStation(id) && !isExistDownStation(id);
    }

    boolean isExistUpStation(Long id) {
        return sections.containsKey(id);
    }

    private boolean isExistDownStation(Long id) {
        return sections.containsValue(id);
    }
}
