package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import wooteco.subway.exception.notfound.NotFoundSectionException;

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
                .orElseThrow(NotFoundSectionException::new);
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
                .orElseThrow(NotFoundSectionException::new);
    }

    private boolean hasLowerSection(final Section section) {
        return sections.stream()
                .anyMatch(section::isUpperThan);
    }

    private Section findLowerSection(final Section section) {
        return sections.stream()
                .filter(section::isUpperThan)
                .findFirst()
                .orElseThrow(NotFoundSectionException::new);
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
                .orElseThrow(NotFoundSectionException::new);
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
                .orElseThrow(NotFoundSectionException::new);
    }

    public void remove(final Long stationId) {
        if (isFirstStationId(stationId)) {
            removeFirstSection(stationId);
            return;
        }
        if (isLastStationId(stationId)) {
            removeLastSection(stationId);
            return;
        }
        removeBothSideSections(stationId);
    }

    private void removeBothSideSections(Long stationId) {
        final Section upperSection = findSameDownStationIdSection(stationId);
        final Section lowerSection = findSameUpStationIdSection(stationId);
        final Section newSection = new Section(upperSection.getLineId(), upperSection.getUpStationId(),
                lowerSection.getDownStationId(), upperSection.getDistance() + lowerSection.getDistance());
        sections.remove(upperSection);
        sections.remove(lowerSection);
        sections.add(newSection);
    }

    private void removeLastSection(final Long stationId) {
        validateSizeWhenFirstOrLastSection();
        final Section section = findSameDownStationIdSection(stationId);
        sections.remove(section);
    }

    private void removeFirstSection(final Long stationId) {
        validateSizeWhenFirstOrLastSection();
        final Section section = findSameUpStationIdSection(stationId);
        sections.remove(section);
    }


    private void validateSizeWhenFirstOrLastSection() {
        if (sections.size() < 2) {
            throw new IllegalArgumentException("노선에 구간은 1개 이상이어야 합니다.");
        }
    }

    private boolean isFirstStationId(final Long stationId) {
        return findFirstSection(findAnySection()).getUpStationId().equals(stationId);
    }

    private boolean isLastStationId(final Long stationId) {
        return findLastSection(findAnySection()).getDownStationId().equals(stationId);
    }

    private Section findLastSection(final Section section) {
        if (hasLowerSection(section)) {
            return findLastSection(findLowerSection(section));
        }
        return section;
    }

    private Section findSameUpStationIdSection(final Long stationId) {
        return sections.stream()
                .filter(s -> s.getUpStationId().equals(stationId))
                .findFirst()
                .orElseThrow(NotFoundSectionException::new);
    }

    private Section findSameDownStationIdSection(final Long stationId) {
        return sections.stream()
                .filter(s -> s.getDownStationId().equals(stationId))
                .findFirst()
                .orElseThrow(NotFoundSectionException::new);
    }

    public List<Section> getSections() {
        return sections;
    }
}
