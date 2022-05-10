package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Sections {

    private static final int FIRST_INDEX = 0;
    private static final int MINIMUM_SIZE = 1;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Long> getSortedStationId() {
        Map<Long, Long> stationsBySection = new HashMap<>();
        sections.forEach(section -> stationsBySection.put(section.getUpStationId(), section.getDownStationId()));

        Long upStationId = findUpStationId(stationsBySection);

        List<Long> stations = new ArrayList<>();
        stations.add(upStationId);
        while (stationsBySection.containsKey(upStationId)) {
            Long downStationId = stationsBySection.get(upStationId);
            stations.add(downStationId);
            upStationId = downStationId;
        }
        return stations;
    }

    private Long findUpStationId(Map<Long, Long> stationsBySection) {
        return stationsBySection.keySet().stream()
                .filter(key -> !stationsBySection.containsValue(key))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("구간 등록이 잘못되었습니다."));
    }

    public void checkHasStation(Long upStationId, Long downStationId) {
        List<Long> allStationId = getAllStationId();
        if (!allStationId.contains(upStationId) && !allStationId.contains(downStationId)) {
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

    public boolean isTerminal(Long stationId) {
        List<Long> sortedStationId = getSortedStationId();
        Long upStationId = getFirst(sortedStationId);
        Long downStationId = getLast(sortedStationId);

        return stationId.equals(upStationId) || stationId.equals(downStationId);
    }

    private Long getFirst(List<Long> ids) {
        return ids.get(FIRST_INDEX);
    }

    private Long getLast(List<Long> ids) {
        int lastIndex = ids.size() - 1;
        return ids.get(lastIndex);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public boolean isMinimumSize() {
        return sections.size() == MINIMUM_SIZE;
    }
}
