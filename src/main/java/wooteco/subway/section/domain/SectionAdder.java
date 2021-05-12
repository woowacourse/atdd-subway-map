package wooteco.subway.section.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import wooteco.subway.exception.DuplicatedSectionException;
import wooteco.subway.exception.NotAddSectionException;
import wooteco.subway.exception.NotContainStationsException;

public class SectionAdder extends Sections {

    public SectionAdder(List<Section> sections) {
        super(sections);
    }

    public void validateSectionAddable(Section newSection) {
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

    public Set<Long> stationIds() {
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

    public Section originSection(Section section) {
        return sections.stream()
            .filter(it -> it.getUpStationId().equals(section.getUpStationId())
                || it.getDownStationId().equals(section.getDownStationId()))
            .findAny()
            .orElseThrow(NotAddSectionException::new);
    }

    public Section createdSection(Section section) {
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
