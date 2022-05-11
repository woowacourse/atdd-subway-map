package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import wooteco.subway.exception.BothUpAndDownStationDoNotExistException;
import wooteco.subway.exception.BothUpAndDownStationExistException;
import wooteco.subway.exception.CanNotInsertSectionException;
import wooteco.subway.exception.OnlyOneSectionException;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        validateEmptySections(sections);
        this.sections = new ArrayList<>(sections);
    }

    private void validateEmptySections(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("비어있는 구간 목록은 생성할 수 없습니다.");
        }
    }

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

    public void deleteStation(Long stationId) {
        validateHasSingleSection();

        Optional<Section> upSection = getUpSection(stationId);
        Optional<Section> downSection = getDownSection(stationId);

        upSection.ifPresent(sections::remove);
        downSection.ifPresent(sections::remove);

        if (upSection.isPresent() && downSection.isPresent()) {
            addMergedSection(upSection.get(), downSection.get());
        }
    }

    private void validateHasSingleSection() {
        if (sections.size() == 1) {
            throw new OnlyOneSectionException();
        }
    }

    private Optional<Section> getUpSection(Long stationId) {
        return sections.stream()
                .filter(section -> section.hasStationIdAsDownStation(stationId))
                .findAny();
    }

    private Optional<Section> getDownSection(Long stationId) {
        return sections.stream()
                .filter(section -> section.hasStationIdAsUpStation(stationId))
                .findAny();
    }

    private void addMergedSection(Section upSection, Section downSection) {
        Section mergedSection = Section.merge(upSection, downSection);
        sections.add(mergedSection);
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
