package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public boolean isMiddleSection(Section section) {
        Long upStationId = findUpStationId();
        Long downStationId = findDownStationId();
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

    public void validateForkedLoad(Section section) {
        Long upStationId = findUpStationId();
        Long downStationId = findDownStationId();
        if (section.getUpStationId().equals(upStationId) || section.getDownStationId().equals(downStationId)) {
            throw new IllegalArgumentException("갈래길은 생성할 수 없습니다.");
        }
    }

    private Long findDownStationId() {
        Map<Long, Long> sectionId = getSectionId();
        return sectionId.values().stream()
            .filter(i -> !(sectionId.containsKey(i)))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("하행점을 찾을 수 없습니다."));
    }

    private Long findUpStationId() {
        Map<Long, Long> sectionId = getSectionId();
        return sectionId.keySet().stream()
            .filter(i -> !(sectionId.containsValue(i)))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("상행점을 찾을 수 없습니다."));
    }

    public List<Long> sortedStationId() {
        List<Long> stationIds = new ArrayList<>();
        Map<Long, Long> sectionIds = getSectionId();
        Long upStationId = findUpStationId();

        for (int i = 0; i < sectionIds.size(); i++) {
            stationIds.add(upStationId);
            upStationId = sectionIds.get(upStationId);
        }

        return stationIds;
    }
}
