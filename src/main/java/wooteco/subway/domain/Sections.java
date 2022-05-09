package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import wooteco.subway.exception.NotFoundException;

public class Sections {

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public List<Long> toSortedStationIds() {
        Section section = findFirstSection(findAnySection());

        final List<Long> stationIds = new ArrayList<>();
        stationIds.add(section.getUpStationId());

        while (hasLowerSection(section)) {
            section = findLowerSection(section);
            stationIds.add(section.getUpStationId());
        }

        stationIds.add(section.getDownStationId());
        return stationIds;
    }

    private Section findAnySection() {
        return sections.stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("구간을 찾을 수 없습니다."));
    }

    private Section findFirstSection(final Section section) {
        if (hasUpperSection(section)) {
            return findFirstSection(findUpperSection(section));
        }
        return section;
    }

    private boolean hasUpperSection(final Section section) {
        return sections.stream()
                .anyMatch(section::isLowerThan);
    }

    private Section findUpperSection(final Section section) {
        return sections.stream()
                .filter(section::isLowerThan)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("구간을 찾을 수 없습니다."));
    }

    private boolean hasLowerSection(final Section section) {
        return sections.stream()
                .anyMatch(section::isUpperThan);
    }

    private Section findLowerSection(final Section section) {
        return sections.stream()
                .filter(section::isUpperThan)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("구간을 찾을 수 없습니다."));
    }

    public List<Section> getSections() {
        return sections;
    }
}
