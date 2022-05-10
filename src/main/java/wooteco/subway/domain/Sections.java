package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    // 1-3, 3-2, 2-4 하나의 section을 기준으로 up-id와 down-id가 같이 않은 하나의 섹션을 찾으면 됨
// => 만약 순환일 땐 어떻게 하지 1-3, 3-2, 2-1일틴데
    public List<Long> findStationIdsInOrder() {
        List<Long> stationIds = new ArrayList<>();
        Long downStationId = 0L;
        for (Section section : sections) {
            if (countFinalUpStation(section) == 0) {
                stationIds.add(section.getUpStationId());
                downStationId = section.getDownStationId();
                break;
            }
        }
        return findStationInOrder(downStationId, stationIds);
    }

    private long countFinalUpStation(Section section) {
        return sections.stream()
                .filter(s -> section.getUpStationId().equals(s.getDownStationId()))
                .count();
    }

    private List<Long> findStationInOrder(Long downStationId, List<Long> stationIds) {
        stationIds.add(downStationId);
        Optional<Section> optionalSection = sections.stream()
                .filter(section -> downStationId.equals(section.getUpStationId()))
                .findFirst();
        optionalSection.ifPresent(section -> findStationInOrder(section.getDownStationId(), stationIds));
        return stationIds;
    }

}
