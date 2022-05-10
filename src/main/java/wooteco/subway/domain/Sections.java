package wooteco.subway.domain;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {
    private static final int MERGE_SECTION_SIZE = 2;
    private static final int LAST_STATION_SIZE = 1;
    public static final int ONE_SECTION = 1;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Long> getDistinctStationIds() {
        return sections.stream()
                .map(section -> Arrays.asList(section.getUpStationId(), section.getDownStationId()))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Long> getLastStationIds() {
        return sections.stream()
                .map(section -> Arrays.asList(section.getUpStationId(), section.getDownStationId()))
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(it -> it))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() == LAST_STATION_SIZE)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public Optional<Section> getExistedUpStationSection(Long upStationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(upStationId))
                .findFirst();
    }

    public Optional<Section> getExistedDownStationSection(Long downStationId) {
        return sections.stream()
                .filter(section -> isEqualDownStationId(downStationId, section))
                .findFirst();
    }

    private boolean isEqualDownStationId(Long downStationId, Section section) {
        return section.getDownStationId().equals(downStationId);
    }

    public boolean isLastStation(Long stationId) {
        return getLastStationIds().contains(stationId);
    }

    public Sections getByStationId(Long stationId) {
        return new Sections(sections.stream()
                .filter(section -> section.isEqualDownStationId(stationId) || section.isEqualUpStationId(stationId))
                .collect(Collectors.toList()));
    }

    public List<Section> getSections() {
        return sections;
    }

    public boolean isIntermediateStation() {
        return sections.size() == MERGE_SECTION_SIZE;
    }

    public Section mergeSections() {
        if (sections.size() != MERGE_SECTION_SIZE) {
            throw new IllegalArgumentException("두 구간만 합칠 수 있습니다.");
        }

        int distance = calculateDistance();
        List<Long> mergedIds = getMergedStationIds();

        return new Section(distance, sections.get(0).getLineId(), mergedIds.get(0), mergedIds.get(1));
    }

    private List<Long> getMergedStationIds() {
        Section section1 = sections.get(0);
        Section section2 = sections.get(1);

        if (section1.isEqualUpStationId(section2.getDownStationId())) {
            return List.of(section2.getUpStationId(), section1.getDownStationId());
        }
        if (section2.isEqualUpStationId(section1.getDownStationId())) {
            return List.of(section1.getUpStationId(), section2.getDownStationId());
        }
        throw new IllegalArgumentException("합칠 수 없는 구간입니다.");
    }

    private int calculateDistance() {
        return sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }

    public void validateSize() {
        if (sections.size() == ONE_SECTION) {
            throw new IllegalArgumentException("구간이 하나인 경우에는 삭제할 수 없습니다.");
        }
    }
}
