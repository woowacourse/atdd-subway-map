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

    public List<Long> getSortedStationId() {
        Map<Long, Long> stationsBySection = new HashMap<>();
        sections.forEach(section -> stationsBySection.put(section.getUpStationId(), section.getDownStationId()));

        Long upStationId = getUpStationId(stationsBySection);

        List<Long> stations = new ArrayList<>();
        stations.add(upStationId);
        while (stationsBySection.containsKey(upStationId)) {
            Long downStationId = stationsBySection.get(upStationId);
            stations.add(downStationId);
            upStationId = downStationId;
        }
        return stations;
    }

    private Long getUpStationId(Map<Long, Long> stationsBySection) {
        return stationsBySection.keySet().stream()
                .filter(key -> !stationsBySection.containsValue(key))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("구간 등록이 잘못되었습니다."));
    }
}
