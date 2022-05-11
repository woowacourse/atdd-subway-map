package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import wooteco.subway.exception.notfound.NotFoundSectionException;

public class Sections {

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public List<Long> toSortedStationIds() {
        final LinkedList<Long> stationsIds = new LinkedList<>();
        final Section section = findAnySection();
        addUpperStationIds(stationsIds, section);
        addLowerStationIds(stationsIds, section);
        return stationsIds;
    }

    private Section findAnySection() {
        return sections.stream()
                .findAny()
                .orElseThrow(NotFoundSectionException::new);
    }

    private void addUpperStationIds(final LinkedList<Long> stationsIds, final Section section) {
        stationsIds.addFirst(section.getUpStationId());
        findUpperSection(section).ifPresent(value -> addUpperStationIds(stationsIds, value));
    }

    private Optional<Section> findUpperSection(final Section section) {
        return sections.stream()
                .filter(section::isLowerThan)
                .findFirst();
    }

    private void addLowerStationIds(final LinkedList<Long> stationsIds, final Section section) {
        stationsIds.addLast(section.getDownStationId());
        findLowerSection(section).ifPresent(value -> addLowerStationIds(stationsIds, value));
    }

    private Optional<Section> findLowerSection(final Section section) {
        return sections.stream()
                .filter(section::isUpperThan)
                .findFirst();
    }

    public void add(final Section section) {
        validateDuplicateSection(section);
        final Optional<Section> foundSection = findSameUpOrDownStationIdSection(section);
        if (foundSection.isPresent()) {
            addAtMiddle(section, foundSection.get());
            return;
        }
        sections.add(section);
    }

    private void validateDuplicateSection(final Section section) {
        sections.stream()
                .filter(section::equals)
                .findAny()
                .ifPresent(value -> {
                    throw new IllegalArgumentException("이미 있는 구간은 추가할 수 없습니다.");
                });
    }

    private Optional<Section> findSameUpOrDownStationIdSection(final Section section) {
        return sections.stream()
                .filter(s -> s.hasSameUpStationId(section) || s.hasSameDownStationId(section))
                .findFirst();
    }

    private void addAtMiddle(final Section section, final Section originSection) {
        validateDistance(section, originSection);
        sections.remove(originSection);
        sections.add(section);
        sections.add(createNewSection(section, originSection));
    }

    private void validateDistance(final Section section, final Section originSection) {
        if (section.isSameOrLongerThan(originSection)) {
            throw new IllegalArgumentException("구간의 길이가 너무 길어 추가할 수 없습니다.");
        }
    }

    private Section createNewSection(final Section section, final Section originSection) {
        if (section.hasSameUpStationId(originSection)) {
            return new Section(section.getLineId(), section.getDownStationId(),
                    originSection.getDownStationId(), originSection.minusDistance(section));
        }
        return new Section(section.getLineId(), originSection.getUpStationId(),
                section.getUpStationId(), originSection.minusDistance(section));
    }

    public void remove(final Long stationId) {
        final Optional<Section> upperSection = findSameDownStationIdSection(stationId);
        final Optional<Section> lowerSection = findSameUpStationIdSection(stationId);
        if (upperSection.isPresent() && lowerSection.isPresent()) {
            removeAtMiddle(upperSection.get(), lowerSection.get());
            return;
        }
        removeAtSide(upperSection, lowerSection);
    }

    private Optional<Section> findSameUpStationIdSection(final Long stationId) {
        return sections.stream()
                .filter(s -> s.getUpStationId().equals(stationId))
                .findFirst();
    }

    private Optional<Section> findSameDownStationIdSection(final Long stationId) {
        return sections.stream()
                .filter(s -> s.getDownStationId().equals(stationId))
                .findFirst();
    }

    private void removeAtMiddle(final Section upperSection, final Section lowerSection) {
        final Section newSection = new Section(upperSection.getLineId(), upperSection.getUpStationId(),
                lowerSection.getDownStationId(), upperSection.plusDistance(lowerSection));
        sections.remove(upperSection);
        sections.remove(lowerSection);
        sections.add(newSection);
    }

    private void removeAtSide(final Optional<Section> upperSection, final Optional<Section> lowerSection) {
        validateSizeWhenFirstOrLastSection();
        upperSection.ifPresent(sections::remove);
        lowerSection.ifPresent(sections::remove);
    }

    private void validateSizeWhenFirstOrLastSection() {
        if (sections.size() < 2) {
            throw new IllegalArgumentException("노선에 구간은 1개 이상이어야 합니다.");
        }
    }

    public List<Section> findDifferentSections(final Sections sections) {
        return this.sections.stream()
                .filter(s -> !sections.getSections().contains(s))
                .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return List.copyOf(sections);
    }
}
