package wooteco.subway.domain.line;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wooteco.subway.domain.section.Section;

public class SortedStationIds {

    Map<Long, Long> up = new HashMap<>();
    Map<Long, Long> down = new HashMap<>();
    ArrayDeque<Long> result = new ArrayDeque<>();

    private final List<Section> sections;

    public SortedStationIds(List<Section> sections) {
        this.sections = sections;
    }

    public List<Long> get() {
        init(sections);
        sort();
        return new ArrayList<>(result);
    }

    private void init(List<Section> sections) {
        for (Section section : sections) {
            up.put(section.getDownStationId(), section.getUpStationId());
            down.put(section.getUpStationId(), section.getDownStationId());
        }

        result.addFirst(sections.get(0).getUpStationId());
    }

    private void sort() {
        while (up.containsKey(result.peekFirst())) {
            result.addFirst(up.get(result.peekFirst()));
        }
        while (down.containsKey(result.peekLast())) {
            result.addLast(down.get(result.peekLast()));
        }
    }
}
