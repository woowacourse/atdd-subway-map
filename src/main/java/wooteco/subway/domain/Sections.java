package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import wooteco.subway.exception.section.DuplicatedSectionException;
import wooteco.subway.exception.section.NonexistentSectionStationException;
import wooteco.subway.exception.section.NonexistentStationSectionException;
import wooteco.subway.exception.section.OnlyOneSectionException;
import wooteco.subway.exception.section.SectionLengthExcessException;

public class Sections {

    private static final int NONE = 0;
    private static final int TWO = 2;

    private static final int ONE = 1;

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
        checkIsDuplicate(section);
    }

    private void checkIsDuplicate(Section section) {
        List<Long> stationIds = findStationIds();

        long matchCount = stationIds.stream()
                .filter(it -> section.isSameUpStationId(it)
                        || section.isSameDownStationId(it))
                .count();

        if (matchCount == TWO) {
            throw new DuplicatedSectionException();
        }
        if (matchCount == NONE) {
            throw new NonexistentSectionStationException();
        }
    }

    private List<Long> findStationIds() {
        return sections.stream()
                .flatMap(it -> it.getStationIds().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public boolean needToChangeExistingSection(Section section) {
        return sections.stream()
                .anyMatch(it -> section.hasSameUpStation(it)
                        || section.hasSameDownStation(it));
    }

    public Section findNeedUpdatingSection(Section section) {
        boolean upStationMatched = sections.stream()
                .anyMatch(it -> section.hasSameUpStation(it)
                        && !section.hasSameDownStation(it));

        if (upStationMatched) {
            return makeSectionWithNewUpStation(section);
        }
        return makeSectionWithNewDownStation(section);
    }

    private Section makeSectionWithNewUpStation(Section section) {
        Section oldSection = sections.stream()
                .filter(it -> section.hasSameUpStation(it)
                        && !section.hasSameDownStation(it))
                .findFirst()
                .orElseThrow(NonexistentSectionStationException::new);

        checkDistance(section, oldSection);

        return new Section(oldSection.getId(),
                oldSection.getLineId(),
                section.getDownStationId(),
                oldSection.getDownStationId(),
                oldSection.calculateDistanceDifference(section));
    }

    private void checkDistance(Section section, Section oldSection) {
        if (section.isGreaterOrEqualDistanceThan(oldSection)) {
            throw new SectionLengthExcessException();
        }
    }

    private Section makeSectionWithNewDownStation(Section section) {
        Section oldSection = sections.stream()
                .filter(it -> !section.hasSameUpStation(it)
                        && section.hasSameDownStation(it))
                .findAny()
                .orElseThrow(NonexistentSectionStationException::new);

        checkDistance(section, oldSection);

        return new Section(oldSection.getId(),
                oldSection.getLineId(),
                oldSection.getUpStationId(),
                section.getUpStationId(),
                oldSection.calculateDistanceDifference(section));
    }

    public void validateRemovable(Long stationId) {
        if (sections.size() <= ONE) {
            throw new OnlyOneSectionException();
        }
        List<Long> stationIds = findStationIds();
        if (!stationIds.contains(stationId)) {
            throw new NonexistentStationSectionException();
        }
    }

    public boolean isEndStation(Long stationId) {
        long matchedCount = sections.stream()
                .filter(it -> it.isSameUpStationId(stationId)
                        || it.isSameDownStationId(stationId))
                .count();
        return matchedCount == ONE;
    }

    public Long findEndSectionIdToRemove(Long stationId) {
        return sections.stream()
                .filter(it -> it.isSameUpStationId(stationId)
                        || it.isSameDownStationId(stationId))
                .map(Section::getId)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    public Section makeNewSection(Long stationId) {
        Section newUpStation = sections.stream()
                .filter(it -> it.isSameDownStationId(stationId))
                .findFirst()
                .orElseThrow(IllegalStateException::new);

        Section newDownStation = sections.stream()
                .filter(it -> it.isSameUpStationId(stationId))
                .findFirst()
                .orElseThrow(IllegalStateException::new);

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

    public List<Section> findEndSections() {
        Map<Long, Integer> stationCount = findStationCountMap();

        List<Long> endStationIds = stationCount.entrySet()
                .stream()
                .filter(it -> it.getValue() == ONE)
                .map(Entry::getKey)
                .collect(Collectors.toList());

        return List.of(findUpSection(endStationIds), findDownSection(endStationIds));
    }

    private Map<Long, Integer> findStationCountMap() {
        Map<Long, Integer> stationCount = new HashMap<>();

        for (Section section : sections) {
            stationCount.put(section.getUpStationId(),
                    stationCount.getOrDefault(section.getUpStationId(), 0) + 1);

            stationCount.put(section.getDownStationId(),
                    stationCount.getOrDefault(section.getDownStationId(), 0) + 1);
        }
        return stationCount;
    }

    private Section findUpSection(List<Long> endStationIds) {
        return sections.stream()
                .filter(it -> endStationIds.contains(it.getUpStationId()))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    private Section findDownSection(List<Long> endStationIds) {
        return sections.stream()
                .filter(it -> endStationIds.contains(it.getDownStationId()))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    public List<Long> findArrangedStationIds(Section endUpSection) {
        List<Long> stationIds = new ArrayList<>(
                List.of(endUpSection.getUpStationId(), endUpSection.getDownStationId()));

        Optional<Long> nextStationId = sections.stream()
                .filter(it -> it.isSameUpStationId(stationIds.get(stationIds.size() - 1)))
                .map(Section::getDownStationId)
                .findFirst();

        while (nextStationId.isPresent()) {
            nextStationId = sections.stream()
                    .filter(it -> it.isSameUpStationId(stationIds.get(stationIds.size() - 1)))
                    .map(Section::getDownStationId)
                    .findFirst();
            nextStationId.ifPresent(stationIds::add);
        }

        return stationIds;
    }

    public List<Section> getSections() {
        return sections;
    }
}
