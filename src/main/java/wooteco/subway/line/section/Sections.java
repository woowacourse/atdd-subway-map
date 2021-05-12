package wooteco.subway.line.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import wooteco.subway.exception.IllegalUserInputException;
import wooteco.subway.exception.NotExistItemException;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public void sort() {
        Map<Long, Section> upStationToSection = getUpStationToSection();
        Long upStationId = getStationId(upStationToSection);
        sections.clear();
        while (upStationToSection.containsKey(upStationId)) {
            Section section = upStationToSection.get(upStationId);
            sections.add(section);
            upStationId = section.getDownStationId();
        }
    }

    private Map<Long, Section> getUpStationToSection() {
        Map<Long, Section> upStationToSection = new HashMap<>();
        for (Section section : sections) {
            upStationToSection.put(section.getUpStationId(), section);
        }
        return upStationToSection;
    }

    private Long getStationId(Map<Long, Section> upStationToSection) {
        Map<Long, Integer> stationIdCount = new HashMap<>();

        for (Section section : sections) {
            Long upStationId = section.getUpStationId();
            Long downStationId = section.getDownStationId();
            stationIdCount.put(upStationId, stationIdCount.getOrDefault(upStationId, 0) + 1);
            stationIdCount.put(downStationId, stationIdCount.getOrDefault(downStationId, 0) + 1);
        }

        return stationIdCount.keySet().stream()
            .filter(key -> stationIdCount.get(key) == 1)
            .filter(upStationToSection::containsKey)
            .findFirst()
            .orElseThrow(NotExistItemException::new);
    }

    public List<Section> getSections() {
        return new ArrayList<>(sections);
    }

    public Section findJoinSection(Section section) {
        validate(section);
        return null;
    }

    private Set<Long> getStationIds() {
        Set<Long> stationIds = new HashSet<>();
        for (Section section : sections) {
            stationIds.add(section.getDownStationId());
            stationIds.add(section.getUpStationId());
        }
        return stationIds;
    }

    private void validate(Section section) {
        Set<Long> stationIds = getStationIds();
        int stationCount = 0;
        if (stationIds.contains(section.getDownStationId())) {
            stationCount++;
        }
        if (stationIds.contains(section.getUpStationId())) {
            stationCount++;
        }
        if (stationCount != 2) {
            throw new IllegalUserInputException();
        }
    }
}
