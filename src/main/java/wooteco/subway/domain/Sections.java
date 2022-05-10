package wooteco.subway.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public boolean isForkedRoad(Section section) {
        Map<Long, Long> sectionId = getSectionId();

        Long upStationId = findUpStationId(sectionId);
        Long downStationId = findDownStationId(sectionId);

        validateForkedLoad(section, upStationId, downStationId);
        return isMiddlePoint(section, upStationId, downStationId);
    }

    private boolean isMiddlePoint(Section section, Long upStationId, Long downStationId) {
        return !(section.getDownStationId().equals(upStationId) || section.getUpStationId()
            .equals(downStationId));
    }

    private Map<Long, Long> getSectionId() {
        Map<Long, Long> sectionId = new HashMap<>();
        for (Section sectionOfSections : sections) {
            sectionId.put(sectionOfSections.getUpStationId(), sectionOfSections.getDownStationId());
        }
        return sectionId;
    }

    private void validateForkedLoad(Section section,  Long upStationId, Long downStationId) {
        if (section.getUpStationId().equals(upStationId)) {
            throw new IllegalArgumentException("갈래길은 생성할 수 없습니다.");
        }
        if (section.getDownStationId().equals(downStationId)) {
            throw new IllegalArgumentException("갈래길은 생성할 수 없습니다.");
        }
    }

    private Long findDownStationId(Map<Long, Long> sectionId) {
        return sectionId.values().stream()
            .filter(i -> !(sectionId.containsKey(i)))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("하행점을 찾을 수 없습니다."));
    }

    private Long findUpStationId(Map<Long, Long> sectionId) {
        return sectionId.keySet().stream()
            .filter(i -> !(sectionId.containsValue(i)))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("상행점을 찾을 수 없습니다."));
    }
}
