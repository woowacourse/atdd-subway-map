package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import wooteco.subway.exception.BothUpAndDownStationAlreadyExistException;
import wooteco.subway.exception.BothUpAndDownStationDoNotExistException;
import wooteco.subway.exception.CanNotInsertSectionException;
import wooteco.subway.exception.OnlyOneSectionException;

public class Sections {

    private final List<Section> value;

    public Sections(Long upStationId, Long downStationId, Distance distance) {
        Section initialSection = new Section(upStationId, downStationId, distance);
        this.value = new ArrayList<>(List.of(initialSection));
    }

    public Sections(Section section) {
        this.value = new ArrayList<>(List.of(section));
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
        validateHasSameStation(newSection);
        validateBothUpAndDownStationAlreadyExisting(newSection);

        if (shouldInsert(newSection)) {
            insertSection(newSection);
            return;
        }

        value.add(newSection);
    }

    private void validateBothUpAndDownStationAlreadyExisting(Section newSection) {
        boolean isUpStationExisting = value.stream().anyMatch(section -> section.isSameUpStation(newSection));
        boolean isDownStationExisting = value.stream().anyMatch(section -> section.isSameDownStation(newSection));

        if (isUpStationExisting && isDownStationExisting) {
            throw new BothUpAndDownStationAlreadyExistException();
        }
    }

    private void validateHasSameStation(Section newSection) {
        boolean hasSameStation = value.stream().anyMatch(section -> section.hasSameStation(newSection));

        if (!hasSameStation) {
            throw new BothUpAndDownStationDoNotExistException();
        }
    }

    private boolean shouldInsert(Section newSection) {
        return value.stream()
                .anyMatch(section -> section.isSameEitherUpOrDownStation(newSection));
    }

    private void insertSection(Section insertedSection) {
        Section insertTargetSection = getInsertTargetSection(insertedSection);
        validateDistanceOnInserting(insertedSection, insertTargetSection);

        Section shortenedTargetSection = shortenInsertTargetSection(insertTargetSection, insertedSection);

        value.remove(insertTargetSection);
        value.add(shortenedTargetSection);
        value.add(insertedSection);
    }

    private void validateDistanceOnInserting(Section insertedSection, Section insertTargetSection) {
        if (insertTargetSection.isDistanceLessThanOrEqualTo(insertedSection)) {
            throw new CanNotInsertSectionException();
        }
    }

    private Section getInsertTargetSection(Section insertedSection) {
        return value.stream()
                .filter(section -> section.isSameEitherUpOrDownStation(insertedSection))
                .findAny()
                .orElseThrow(CanNotInsertSectionException::new);
    }

    private Section shortenInsertTargetSection(Section insertTargetSection, Section insertedSection) {
        Long upStationId = getUpStationIdOfShortenedTargetSection(insertTargetSection, insertedSection);
        Long downStationId = getDownStationIdOfShortenedTargetSection(insertTargetSection, insertedSection);

        return new Section(upStationId, downStationId, insertTargetSection.subtractDistance(insertedSection));
    }

    private Long getUpStationIdOfShortenedTargetSection(Section insertTargetSection, Section insertedSection) {
        if (insertedSection.isSameUpStation(insertTargetSection)) {
            return insertedSection.getDownStationId();
        }

        return insertTargetSection.getUpStationId();
    }

    private Long getDownStationIdOfShortenedTargetSection(Section insertTargetSection, Section insertedSection) {
        if (insertedSection.isSameUpStation(insertTargetSection)) {
            return insertTargetSection.getDownStationId();
        }

        return insertedSection.getUpStationId();
    }

    // TODO: 리팩토링
    public void deleteStation(Long stationId) {
        validateOnlyOneSection();

        boolean isExistingSameUpStation = value.stream()
                .anyMatch(section -> section.getUpStationId().equals(stationId));
        boolean isExistingSameDownStation = value.stream()
                .anyMatch(section -> section.getDownStationId().equals(stationId));

        // 구간 목록의 상행역을 제거
        if (isExistingSameUpStation && !isExistingSameDownStation) {
            value.remove(value.stream().filter(section -> section.getUpStationId().equals(stationId)).findAny().get());
        }

        // 구간 목록의 하행역을 제거
        if (!isExistingSameUpStation && isExistingSameDownStation) {
            value.remove(
                    value.stream().filter(section -> section.getDownStationId().equals(stationId)).findAny().get());
        }

        // 구간 목록의 중간역을 제거
        if (isExistingSameUpStation && isExistingSameDownStation) {
            Section leftSection = value.stream().filter(section -> section.getDownStationId().equals(stationId))
                    .findAny().get();
            Section rightSection = value.stream().filter(section -> section.getUpStationId().equals(stationId))
                    .findAny().get();

            Section newSection = new Section(leftSection.getUpStationId(), rightSection.getDownStationId(),
                    leftSection.addDistance(rightSection));
            value.remove(leftSection);
            value.remove(rightSection);
            value.add(newSection);
        }
    }

    private void validateOnlyOneSection() {
        if (value.size() == 1) {
            throw new OnlyOneSectionException();
        }
    }

    public List<Section> getValue() {
        return List.copyOf(value);
    }

    @Override
    public String toString() {
        return "Sections{" +
                "value=" + value +
                '}';
    }
}
