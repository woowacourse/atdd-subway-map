package wooteco.subway.section.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import wooteco.subway.exception.DuplicatedSectionException;
import wooteco.subway.exception.NotAddSectionException;
import wooteco.subway.exception.NotContainStationsException;
import wooteco.subway.exception.NotExistSectionException;
import wooteco.subway.exception.NotFoundTerminalStationException;

public class Sections {

    private final List<Section> sections;

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

    public void validateNewSection(Section newSection) {
        validateDuplicatedSection(newSection);
        validateContainStation(newSection);
    }

    public void validateDuplicatedSection(Section newSection) {
        if (sections.stream()
            .anyMatch((section -> section.isSameStations(newSection)))) {
            throw new DuplicatedSectionException();
        }
    }

    public void validateContainStation(Section newSection) {
        Set<Long> stationIds = stationIds();

        if (!stationIds.contains(newSection.getUpStationId())
            && !stationIds.contains(newSection.getDownStationId())) {
            throw new NotContainStationsException();
        }
    }

    private Set<Long> stationIds() {
        Set<Long> stationIds = new HashSet<>();

        for (Section section : sections) {
            stationIds.add(section.getDownStationId());
            stationIds.add(section.getUpStationId());
        }
        return stationIds;
    }

    public boolean isTerminalSection(Section section) {
        List<Long> stationIds = sortedStationIds();

        return stationIds.get(0).equals(section.getDownStationId()) ||
            stationIds.get(stationIds.size() - 1).equals(section.getUpStationId());
    }

    public Section findOriginSection(Section section) {
        return sections.stream()
            .filter(it -> it.getUpStationId().equals(section.getUpStationId())
                || it.getDownStationId().equals(section.getDownStationId()))
            .findAny()
            .orElseThrow(NotAddSectionException::new);
    }

    public Section findNewlyCreatedSection(Section section) {
        List<Long> stationIds = sortedStationIds();
        Long lineId = section.getLineId();

        for (int i = 0; i < stationIds.size(); i++) {
            Long currentStationId = stationIds.get(i);
            if (currentStationId.equals(section.getUpStationId())) {
                return new Section(lineId, section.getDownStationId(),
                    stationIds.get(i + 1),
                    sectionDistance(currentStationId) - section.getDistance());
            }

            if (currentStationId.equals(section.getDownStationId())) {
                return new Section(lineId, stationIds.get(i - 1), section.getUpStationId(),
                    sectionDistance(stationIds.get(i - 1)) - section.getDistance());
            }
        }

        throw new NotAddSectionException();
    }

    private int sectionDistance(Long sectionId) {
        return sections.stream()
            .filter((section -> section.getUpStationId().equals(sectionId)))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new)
            .getDistance();
    }

}
