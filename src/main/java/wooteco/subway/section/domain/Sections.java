package wooteco.subway.section.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import wooteco.subway.exception.NotExistSectionException;
import wooteco.subway.exception.NotFoundTerminalStationException;

public class Sections {

    protected final List<Section> sections;

    public Sections(List<Section> sections) {
        validateSections(sections);
        this.sections = sections;
    }

    private void validateSections(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new NotExistSectionException();
        }
    }

    public List<Long> sortedStationIds() {
        List<Long> stationIds = new ArrayList<>();
        Map<Long, Long> sectionInformation = new HashMap<>();

        for (Section section : sections) {
            sectionInformation.put(section.getUpStationId(), section.getDownStationId());
        }

        Long upStation = upwardTerminalStationId();
        while (!sectionInformation.isEmpty() && sectionInformation.containsKey(upStation)) {
            stationIds.add(upStation);
            upStation = sectionInformation.get(upStation);
        }
        stationIds.add(upStation);
        return stationIds;
    }

    private Long upwardTerminalStationId() {
        Set<Long> upStationIds = new HashSet<>();
        Set<Long> downStationIds = new HashSet<>();

        sections.forEach(section -> {
            upStationIds.add(section.getUpStationId());
            downStationIds.add(section.getDownStationId());
        });

        return upStationIds.stream()
            .filter(upStationId -> !downStationIds.contains(upStationId))
            .findFirst()
            .orElseThrow(NotFoundTerminalStationException::new);
    }

}
