package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import wooteco.subway.exception.BothUpAndDownStationDoNotExistException;
import wooteco.subway.exception.BothUpAndDownStationExistException;
import wooteco.subway.exception.CanNotInsertSectionException;
import wooteco.subway.exception.OnlyOneSectionException;

public class Sections {

    private final List<Section> sections;

    public Sections(Long upStationId, Long downStationId, Distance distance) {
        Section initialSection = new Section(upStationId, downStationId, distance);
        this.sections = new ArrayList<>(List.of(initialSection));
    }

    public Sections(Section section) {
        this.sections = new ArrayList<>(List.of(section));
    }

    // TODO: 리팩토링
    public static Sections from(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("비어있는 구간 목록은 생성할 수 없습니다.");
        }

        Section firstSection = sections.get(0);
        Sections newSections = new Sections(firstSection);
        sections.remove(0);

        for (Section section : sections) {
            newSections.addSection(section);
        }

        return newSections;
    }

    // TODO: 구간 삽입 로직 리팩토링
    public void addSection(Section newSection) {
        validateBothStationsExisting(newSection);
        validateBothStationsNotExisting(newSection);

        if (isNeededToInsert(newSection)) {
            insertSection(newSection);
        }

        sections.add(newSection);
    }

    private void validateBothStationsExisting(Section newSection) {
        boolean isBothStationsExisting = sections.stream()
                .filter(section -> section.isUpStationSame(newSection))
                .anyMatch(section -> section.isDownStationSame(newSection));

        if (isBothStationsExisting) {
            throw new BothUpAndDownStationExistException();
        }
    }

    private void validateBothStationsNotExisting(Section newSection) {
        boolean hasNotSameStation = sections.stream()
                .noneMatch(section -> section.hasSameStation(newSection));

        if (hasNotSameStation) {
            throw new BothUpAndDownStationDoNotExistException();
        }
    }

    private boolean isNeededToInsert(Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.isEitherUpStationOrDownStationSame(newSection));
    }

    private void insertSection(Section newSection) {
        Section baseSection = findBaseSection(newSection);
        validateDistanceOnInserting(baseSection, newSection);

        Section shortenedBaseSection = shortenBaseSection(baseSection, newSection);

        sections.remove(baseSection);
        sections.add(shortenedBaseSection);
    }

    private Section findBaseSection(Section newSection) {
        return sections.stream()
                .filter(section -> section.isEitherUpStationOrDownStationSame(newSection))
                .findAny()
                .orElseThrow(CanNotInsertSectionException::new);
    }

    private void validateDistanceOnInserting(Section baseSection, Section newSection) {
        if (baseSection.isDistanceLessThanOrEqualTo(newSection)) {
            throw new CanNotInsertSectionException();
        }
    }

    private Section shortenBaseSection(Section baseSection, Section newSection) {
        Long baseUp = baseSection.getUpStationId();
        Long newUp = newSection.getUpStationId();
        Long newDown = newSection.getDownStationId();
        Long baseDown = baseSection.getDownStationId();
        Distance shortenedDistance = baseSection.subtractDistance(newSection);

        if (newSection.isUpStationSame(baseSection)) {
            return new Section(newDown, baseDown, shortenedDistance);
        }

        return new Section(baseUp, newUp, shortenedDistance);
    }

    // TODO: 리팩토링
    public void deleteStation(Long stationId) {
        validateHasSingleSection();

        boolean isExistingSameUpStation = sections.stream()
                .anyMatch(section -> section.getUpStationId().equals(stationId));
        boolean isExistingSameDownStation = sections.stream()
                .anyMatch(section -> section.getDownStationId().equals(stationId));

        // 구간 목록의 상행역을 제거
        if (isExistingSameUpStation && !isExistingSameDownStation) {
            sections.remove(
                    sections.stream().filter(section -> section.getUpStationId().equals(stationId)).findAny().get());
        }

        // 구간 목록의 하행역을 제거
        if (!isExistingSameUpStation && isExistingSameDownStation) {
            sections.remove(
                    sections.stream().filter(section -> section.getDownStationId().equals(stationId)).findAny().get());
        }

        // 구간 목록의 중간역을 제거
        if (isExistingSameUpStation && isExistingSameDownStation) {
            Section leftSection = sections.stream().filter(section -> section.getDownStationId().equals(stationId))
                    .findAny().get();
            Section rightSection = sections.stream().filter(section -> section.getUpStationId().equals(stationId))
                    .findAny().get();

            Section newSection = new Section(leftSection.getUpStationId(), rightSection.getDownStationId(),
                    leftSection.addDistance(rightSection));
            sections.remove(leftSection);
            sections.remove(rightSection);
            sections.add(newSection);
        }
    }

    private void validateHasSingleSection() {
        if (sections.size() == 1) {
            throw new OnlyOneSectionException();
        }
    }

    public List<Section> getValue() {
        return List.copyOf(sections);
    }

    @Override
    public String toString() {
        return "Sections{" +
                "value=" + sections +
                '}';
    }
}
