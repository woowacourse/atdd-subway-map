package wooteco.subway.domain.section;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wooteco.subway.exceptions.SectionNotFoundException;

public class Sections {

    private List<Section> sections;

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public Deque<Long> getSortedId() {
        Map<Long, Long> upLineInfo = getUpLineInfo();
        Map<Long, Long> downLineInfo = getDownLineInfo();
        Deque<Long> stationIds = new ArrayDeque<>();

        Long initValue = upLineInfo.entrySet().iterator().next().getKey();
        stationIds.add(initValue);

        while (stationIds.size() < getSections().size() + 1) {
            if (upLineInfo.get(stationIds.getLast()) != null) {
                stationIds.addLast(upLineInfo.get(stationIds.getLast()));
            }
            if (downLineInfo.get(stationIds.getFirst()) != null) {
                stationIds.addFirst(downLineInfo.get(stationIds.getFirst()));
            }
        }
        return new ArrayDeque<>(stationIds);
    }

    private Map<Long, Long> getUpLineInfo() {
        Map<Long, Long> stationInfo = new HashMap<>();
        sections.forEach(section -> {
            stationInfo.put(section.getUpStationId(), section.getDownStationId());
        });
        return new HashMap<>(stationInfo);
    }

    private Map<Long, Long> getDownLineInfo() {
        Map<Long, Long> stationInfo = new HashMap<>();
        sections.forEach(section -> {
            stationInfo.put(section.getDownStationId(), section.getUpStationId());
        });
        return new HashMap<>(stationInfo);
    }

    public Section getSectionById(Long id) {
        return sections.stream()
            .filter(section -> section.getId().equals(id))
            .findFirst()
            .orElseThrow(SectionNotFoundException::new);
    }
}
