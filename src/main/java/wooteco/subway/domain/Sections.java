package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Sections {

    private static final int MINIMUM_SIZE = 1;

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
        return !(section.getDownStationId().equals(upStationId) || section.getUpStationId().equals(downStationId));
    }

    public boolean hasStationId(Long id) {
        Set<Long> stationIds = new HashSet<>();
        for (Section section : sections) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }
        return stationIds.contains(id);
    }

    public Section findSectionByUpStationId(Long id) {
        return sections.stream()
            .filter(i -> i.getUpStationId().equals(id))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("구간 중 해당 upStationId이 존재하지 않습니다."));
    }

    public Section findSectionByDownStationId(Long id) {
        return sections.stream()
            .filter(i -> i.getDownStationId().equals(id))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("구간 중 해당 downStationId 존재하지 않습니다."));
    }

    private Map<Long, Long> getSectionId() {
        Map<Long, Long> sectionId = new HashMap<>();
        for (Section sectionOfSections : sections) {
            sectionId.put(sectionOfSections.getUpStationId(), sectionOfSections.getDownStationId());
        }
        return sectionId;
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
        Long upStationId = findUpStationId();
        List<Long> sectionIds = new ArrayList<>(List.of(upStationId));
        Map<Long, Long> sectionId = getSectionId();

        for (int i = 0; i < sectionId.size(); i++) {
            upStationId = sectionId.get(upStationId);
            sectionIds.add(upStationId);
        }

        return sectionIds;
    }

    public boolean isSingleSection() {
        return sections.size() == MINIMUM_SIZE;
    }
}
