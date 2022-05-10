package wooteco.subway.domain;

import wooteco.subway.exception.BothUpAndDownStationAlreadyExistsException;
import wooteco.subway.exception.BothUpAndDownStationDoNotExistException;
import wooteco.subway.exception.CanNotInsertSectionException;

import java.util.ArrayList;
import java.util.List;

public class Sections {

    private final List<Section> value;

    public Sections(Long upStationId, Long downStationId, Distance distance) {
        Section initialSection = new Section(upStationId, downStationId, distance);
        this.value = new ArrayList<>(List.of(initialSection));
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
            throw new BothUpAndDownStationAlreadyExistsException();
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
        if (insertTargetSection.isDistanceLessThan(insertedSection)) {
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
