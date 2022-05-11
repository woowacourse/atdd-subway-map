package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private static final int POSSIBLE_DELETION_LENGTH = 2;
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Long> getStationIds() {
        List<Long> stationIds = new ArrayList<>();

        for (Section section : sections) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }

        return stationIds.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toUnmodifiableList());
    }

    public void validateLengthToDeletion() {
        if (sections.size() != POSSIBLE_DELETION_LENGTH) {
            throw new IllegalArgumentException("구간을 삭제할 수 없습니다.");
        }
    }

    public Section getSectionStationIdEqualsUpStationId(Long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 입력입니다."));
    }


    public Section getSectionStationIdEqualsDownStationId(Long stationId) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(stationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 입력입니다."));
    }

    public Section getSectionForCombine(Long upStationId, Long downStationId) {
        for (Section section : sections) {
            if ((section.isSameAsUpStation(upStationId)) || (section.isSameAsDownStation(downStationId))) {
                return section;
            }
        }

        return sections.get(0);
    }

    public List<Section> getSections() {
        return sections;
    }
}
