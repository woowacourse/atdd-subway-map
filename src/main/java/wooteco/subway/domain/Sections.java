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
        Long upStationId = findUpTerminalStationId();
        Long downStationId = findDownTerminalStationId();
        return isMiddlePoint(section, upStationId, downStationId);
    }

    private boolean isMiddlePoint(Section section, Long upStationId, Long downStationId) {
        return !(section.matchDownStationId(upStationId) || section.mathUpStationId(downStationId));
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
            .filter(i -> i.mathUpStationId(id))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("구간 중 해당 upStationId이 존재하지 않습니다."));
    }

    public Section findSectionByDownStationId(Long id) {
        return sections.stream()
            .filter(i -> i.matchDownStationId(id))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("구간 중 해당 downStationId 존재하지 않습니다."));
    }

    private Map<Long, Long> getSectionIds() {
        Map<Long, Long> sectionIds = new HashMap<>();
        for (Section section : sections) {
            sectionIds.put(section.getUpStationId(), section.getDownStationId());
        }
        return sectionIds;
    }

    private Long findDownTerminalStationId() {
        Map<Long, Long> sectionIds = getSectionIds();
        return sectionIds.values().stream()
            .filter(i -> !sectionIds.containsKey(i))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("하행점을 찾을 수 없습니다."));
    }

    private Long findUpTerminalStationId() {
        Map<Long, Long> sectionIds = getSectionIds();
        return sectionIds.keySet().stream()
            .filter(i -> !sectionIds.containsValue(i))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("상행점을 찾을 수 없습니다."));
    }

    public List<Long> sortedStationId() {
        Long upStationId = findUpTerminalStationId();
        List<Long> sectionIds = new ArrayList<>(List.of(upStationId));
        Map<Long, Long> sectionId = getSectionIds();

        for (int i = 0; i < sectionId.size(); i++) {
            upStationId = sectionId.get(upStationId);
            sectionIds.add(upStationId);
        }

        return sectionIds;
    }

    private boolean isSingleSection() {
        return sections.size() == MINIMUM_SIZE;
    }

    public boolean canRemoveSection() {
        return !(isSingleSection() || sections.isEmpty());
    }
}
