package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import wooteco.subway.exception.section.SectionLengthExcessException;

public class Sections {

    private final List<Section> sections;
    private final Long lineId;

    public Sections(List<Section> sections, Long lineId) {
        this.sections = sections;
        this.lineId = lineId;
    }

    public List<Long> findEndStationIds() {
        Map<Long, Integer> stationCount = new HashMap<>();

        for (Section section : sections) {
            stationCount.put(section.getUpStationId(),
                    stationCount.getOrDefault(section.getUpStationId(), 0) + 1);

            stationCount.put(section.getDownStationId(),
                    stationCount.getOrDefault(section.getDownStationId(), 0) + 1);
        }

        List<Long> endStationIds = new ArrayList<>();
        for (Entry<Long, Integer> entrySet : stationCount.entrySet()) {
            if (entrySet.getValue() == 1) {
                endStationIds.add(entrySet.getKey());
            }
        }

        return new ArrayList<>(List.of(findUpStationId(endStationIds), findDownStationId(endStationIds)));
    }

    private Long findUpStationId(List<Long> endStationIds) {
        return sections.stream()
                .map(Section::getUpStationId)
                .filter(endStationIds::contains)
                .findFirst()
                .get();
    }

    private Long findDownStationId(List<Long> endStationIds) {
        return sections.stream()
                .map(Section::getDownStationId)
                .filter(endStationIds::contains)
                .findFirst()
                .get();
    }

    public void validateAddable(Section section) {
        if (sections.size() == 0) {
            return;
        }
        checkDuplicateSection(section);
        checkExistingStation(section);
    }

    private void checkDuplicateSection(Section section) {
        boolean matched = sections.stream()
                .anyMatch(it -> section.hasSameUpStation(it)
                        && section.hasSameDownStation(it));
        if (matched) {
            throw new IllegalArgumentException();
        }

        List<Long> endStationIds = findEndStationIds();
        if (section.getUpStationId().equals(endStationIds.get(0)) && section.getDownStationId()
                .equals(endStationIds.get(1))) {
            throw new IllegalArgumentException();
        }
    }

    private void checkExistingStation(Section section) {
        boolean matched = sections.stream()
                .anyMatch(section::hasAnySameStation);

        if (!matched) {
            throw new IllegalArgumentException();
        }
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
        Section targetSection = sections.stream()
                .filter(it -> section.hasSameUpStation(it)
                        && !section.hasSameDownStation(it))
                .findAny()
                .get();

        if (section.getDistance() >= targetSection.getDistance()) {
            throw new SectionLengthExcessException();
        }

        return new Section(targetSection.getId(),
                targetSection.getLineId(),
                section.getDownStationId(),
                targetSection.getDownStationId(),
                targetSection.getDistance() - section.getDistance());
    }

    private Section findNewDownStationSection(Section section) {
        Section targetSection = sections.stream()
                .filter(it -> !section.hasSameUpStation(it)
                        && section.hasSameDownStation(it))
                .findAny()
                .get();

        if (section.getDistance() >= targetSection.getDistance()) {
            throw new SectionLengthExcessException();
        }

        return new Section(targetSection.getId(),
                targetSection.getLineId(),
                targetSection.getUpStationId(),
                section.getUpStationId(),
                targetSection.getDistance() - section.getDistance());
    }

    public List<Section> getSections() {
        return sections;
    }

    public Long getLineId() {
        return lineId;
    }
}
