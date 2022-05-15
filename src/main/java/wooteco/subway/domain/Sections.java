package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Sections {

    private final static int FIRST_INDEX = 0;
    private static final int BLANK_LENGTH = 0;
    private static final int POSSIBLE_DELETION_LENGTH = 2;
    private static final long NOTING = -1L;
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Long> getStationIds() {
        List<Long> stationIds = new ArrayList<>();
        Section firstSection = getFirstSection();
        stationIds.add(firstSection.getUpStationId());
        Long nextStation = firstSection.getDownStationId();
        while (nextStation != NOTING) {
            stationIds.add(nextStation);
            nextStation = getNextStation(nextStation);
        }
        return stationIds;
    }

    private Section getFirstSection() {
        return sections.stream()
                .filter(section -> getStationCountForFirstStation(section.getUpStationId()) == 0)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("구간 등록이 잘못됐으니 프로그램을 리팩토링하세요."));
    }

    private int getStationCountForFirstStation(Long stationId) {
        return (int) sections.stream()
                .filter(section -> section.isSameAsDownStation(stationId))
                .count();
    }

    private Long getNextStation(Long downStationId) {
        return sections.stream()
                .filter(section -> section.isSameAsUpStation(downStationId))
                .map(Section::getDownStationId)
                .findFirst()
                .orElse(NOTING);
    }

    public void validateLengthToDeletion() {
        if (sections.size() != POSSIBLE_DELETION_LENGTH) {
            throw new IllegalArgumentException("구간을 삭제할 수 없습니다.");
        }
    }

    public Section getSectionStationIdEqualsUpStationId(Long stationId) {
        return sections.stream()
                .filter(section -> section.isSameAsUpStation(stationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 입력입니다."));
    }


    public Section getSectionStationIdEqualsDownStationId(Long stationId) {
        return sections.stream()
                .filter(section -> section.isSameAsDownStation(stationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 입력입니다."));
    }

    public Section getSectionForCombine(Long upStationId, Long downStationId) {
        for (Section section : sections) {
            if ((section.isSameAsUpStation(upStationId)) || (section.isSameAsDownStation(downStationId))) {
                return section;
            }
        }
        return sections.get(FIRST_INDEX);
    }

    public boolean isBlank() {
        return sections.size() == BLANK_LENGTH;
    }

    public boolean isContain(Section section) {
        return sections.contains(section);
    }

    public List<Section> getSections() {
        return sections;
    }
}
