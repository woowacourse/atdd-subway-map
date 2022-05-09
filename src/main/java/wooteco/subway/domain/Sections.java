package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import wooteco.subway.exception.NotFoundException;

public class Sections {

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = new ArrayList<>(sections);
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

    public void add(final Section section) {
        if (hasSameSection(section)) {
            throw new IllegalArgumentException("이미 있는 구간은 추가할 수 없습니다.");
        }
        if (hasSameUpStationIdSection(section)) {
            addSectionWhenHasSameUpStationId(section);
            return;
        }
        if (hasSameDownStationIdSection(section)) {
            addSectionWhenHasSameDownStationId(section);
            return;
        }
        sections.add(section);
    }

    private boolean hasSameSection(final Section section) {
        return sections.stream()
                .anyMatch(section::equals);
    }

    private boolean hasSameUpStationIdSection(final Section section) {
        return sections.stream()
                .anyMatch(s -> s.hasSameUpStationId(section));
    }

    private void addSectionWhenHasSameUpStationId(final Section section) {
        final Section originSection = findSameUpStationIdSection(section);
        validateDistance(section, originSection);

        final Section newSection = new Section(section.getLineId(), section.getDownStationId(),
                originSection.getDownStationId(), originSection.getDistance() - section.getDistance());
        sections.remove(originSection);
        sections.add(section);
        sections.add(newSection);
    }

    private void validateDistance(final Section section, final Section originSection) {
        if (section.isSameOrLongerThan(originSection)) {
            throw new IllegalArgumentException("구간의 길이가 너무 길어 추가할 수 없습니다.");
        }
    }

    private Section findSameUpStationIdSection(final Section section) {
        return sections.stream()
                .filter(s -> s.hasSameUpStationId(section))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("구간을 찾을 수 없습니다."));
    }

    private boolean hasSameDownStationIdSection(final Section section) {
        return sections.stream()
                .anyMatch(s -> s.hasSameDownStationId(section));
    }

    private void addSectionWhenHasSameDownStationId(final Section section) {
        final Section originSection = findSameDownStationIdSection(section);
        validateDistance(section, originSection);

        final Section newSection = new Section(section.getLineId(), originSection.getUpStationId(),
                section.getUpStationId(), originSection.getDistance() - section.getDistance());
        sections.remove(originSection);
        sections.add(section);
        sections.add(newSection);
    }

    private Section findSameDownStationIdSection(final Section section) {
        return sections.stream()
                .filter(s -> s.hasSameDownStationId(section))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("구간을 찾을 수 없습니다."));
    }

    public List<Section> getSections() {
        return sections;
    }
}
