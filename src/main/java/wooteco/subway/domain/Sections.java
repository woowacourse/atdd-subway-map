package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public void checkHasStation(Long upStationId, Long downStationId) {
        List<Long> allStationId = getAllStationId();
        if (!allStationId.contains(upStationId) && !getAllStationId().contains(downStationId)) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음");
        }
    }

    private List<Long> getAllStationId() {
        Set<Long> ids = new HashSet<>();
        sections.forEach(section -> {
            ids.add(section.getUpStationId());
            ids.add(section.getDownStationId());
        });
        return new ArrayList<>(ids);
    }
}
