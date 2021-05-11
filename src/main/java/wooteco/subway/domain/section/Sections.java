package wooteco.subway.domain.section;

import java.util.*;

public class Sections {
    private final Map<Long, Long> ids = new HashMap<>();
    private final Map<Map<Long, Long>, Integer> sections = new HashMap<>();

    public Sections(List<Section> sections) {
        List<Section> copiedSections = new ArrayList<>(sections);
        for (Section section : copiedSections) {
            ids.put(section.getUpStationId(), section.getDownStationId());
            this.sections.put(ids, section.getDistance());
        }
    }

    public List<Long> getStationIds(long upwardTerminalId, long downwardTerminalId) {
        List<Long> stationIds = new ArrayList<>();
        long upwardId = upwardTerminalId;

        while (ids.containsKey(upwardId)) {
            stationIds.add(upwardId);
            upwardId = ids.get(upwardId);
        }

        stationIds.add(downwardTerminalId);
        return Collections.unmodifiableList(stationIds);
    }
}
