package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import wooteco.subway.exception.section.DuplicatedSectionException;
import wooteco.subway.exception.section.NonexistentSectionStationException;
import wooteco.subway.exception.section.SectionLengthExcessException;

public class Sections {

    private static final int NONE = 0;
    private static final int TWO = 2;

    private final List<Section> sections;
    private final Long lineId;

    public Sections(List<Section> sections, Long lineId) {
        this.sections = sections;
        this.lineId = lineId;
    }

    public void validateAddable(Section section) {
        if (sections.size() == NONE) {
            return;
        }
        checkDuplicateSection(section);
    }

    private void checkDuplicateSection(Section section) {
        List<Long> stationIds = findStationIds();
        long matchCount = stationIds.stream()
                .filter(it -> section.isSameUpStationId(it) || section.isSameDownStationId(it))
                .count();

        if (matchCount == TWO) {
            throw new DuplicatedSectionException();
        }
        if (matchCount == NONE) {
            throw new NonexistentSectionStationException();
        }
    }

    private List<Long> findStationIds() {
        Set<Long> upStationIds = sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toSet());

        Set<Long> downStationIds = sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toSet());
        upStationIds.addAll(downStationIds);
        return new ArrayList<>(upStationIds);
    }

    public boolean needToChange(Section section) {
        return sections.stream()
                .anyMatch(it -> section.hasSameUpStation(it)
                        || section.hasSameDownStation(it));
    }

    public Section findUpdatingSection(Section section) {
        boolean upStationMatched = sections.stream()
                .anyMatch(it -> section.hasSameUpStation(it)
                        && !section.hasSameDownStation(it));

        if (upStationMatched) {
            return findNewUpStationSection(section);
        }
        return findNewDownStationSection(section);
    }

    private Section findNewUpStationSection(Section section) {
        Section oldSection = sections.stream()
                .filter(it -> section.hasSameUpStation(it)
                        && !section.hasSameDownStation(it))
                .findAny()
                .orElseThrow(NonexistentSectionStationException::new);

        if (section.isGreaterOrEqualDistanceThan(oldSection)) {
            throw new SectionLengthExcessException();
        }

        return new Section(oldSection.getId(),
                oldSection.getLineId(),
                section.getDownStationId(),
                oldSection.getDownStationId(),
                oldSection.calculateDistanceDifference(section));
    }

    private Section findNewDownStationSection(Section section) {
        Section oldSection = sections.stream()
                .filter(it -> !section.hasSameUpStation(it)
                        && section.hasSameDownStation(it))
                .findAny()
                .orElseThrow(NonexistentSectionStationException::new);

        if (section.isGreaterOrEqualDistanceThan(oldSection)) {
            throw new SectionLengthExcessException();
        }

        return new Section(oldSection.getId(),
                oldSection.getLineId(),
                oldSection.getUpStationId(),
                section.getUpStationId(),
                oldSection.calculateDistanceDifference(section));
    }

    public void validateRemovable(Long stationId) {
        if (sections.size() <= 1) {
            throw new IllegalArgumentException();
        }
        List<Long> stationIds = findStationIds();
        if (!stationIds.contains(stationId)) {
            throw new IllegalArgumentException();
        }
    }

    public boolean isEndStation(Long stationId) {
        long matchedCount = sections.stream()
                .filter(it -> it.isSameUpStationId(stationId) || it.isSameDownStationId(stationId))
                .count();
        return matchedCount == 1;
    }

    public Long findEndSectionIdToRemove(Long stationId) {
        return sections.stream()
                .filter(it -> it.isSameUpStationId(stationId)
                        || it.isSameDownStationId(stationId))
                .map(Section::getId)
                .findFirst()
                .orElseThrow();
    }

    public Section makeNewSection(Long stationId) {
        Section newUpStation = sections.stream().filter(it -> it.isSameDownStationId(stationId))
                .findFirst()
                .orElseThrow();

        Section newDownStation = sections.stream().filter(it -> it.isSameUpStationId(stationId))
                .findFirst()
                .orElseThrow();

        return new Section(newUpStation.getLineId(),
                newUpStation.getUpStationId(),
                newDownStation.getDownStationId(),
                newUpStation.getDistance() + newDownStation.getDistance());
    }

    public List<Long> findSectionIdsToRemove(Long stationId) {
        return sections.stream()
                .filter(it -> it.isSameUpStationId(stationId)
                        || it.isSameDownStationId(stationId))
                .map(Section::getId)
                .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return sections;
    }

    public Long getLineId() {
        return lineId;
    }

    @Override
    public String toString() {
        return "Sections{" +
                "sections=" + sections +
                ", lineId=" + lineId +
                '}';
    }
}
