package wooteco.subway.section.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import wooteco.subway.exception.DeleteSectionException;
import wooteco.subway.exception.NotExistStationException;

public class SectionRemover extends Sections {

    public SectionRemover(List<Section> sections) {
        super(sections);
    }

    public void validateSectionRemovable(Long stationId) {
        if (sections.size() == 1) {
            throw new DeleteSectionException();
        }
        Set<Long> stationIds = stationIds();
        if (!stationIds.contains(stationId)) {
            throw new NotExistStationException();
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

    public boolean isTerminalSection(Long stationId) {
        List<Long> stationIds = sortedStationIds();
        return stationIds.get(0).equals(stationId)
            || stationIds.get(stationIds.size() - 1).equals(stationId);
    }

    public Section deletedTerminalSection(Long lineId, Long stationId) {
        List<Long> stationIds = sortedStationIds();
        if (stationIds.get(0).equals(stationId)) {
            return new Section(lineId, stationIds.get(0), stationIds.get(1));
        }
        if (stationIds.get(stationIds.size() - 1).equals(stationId)) {
            return new Section(lineId, stationIds.get(stationIds.size() - 2),
                stationIds.get(stationIds.size() - 1));
        }
        throw new DeleteSectionException();
    }

    public List<Section> deletedSections(Long lineId, Long stationId) {
        List<Section> deletedSections = new ArrayList<>();
        List<Long> stationIds = sortedStationIds();

        for (int i = 0; i < stationIds.size(); i++) {
            if (stationIds.get(i).equals(stationId)) {
                deletedSections.add(new Section(lineId, stationIds.get(i - 1), stationId));
                deletedSections.add(new Section(lineId, stationId, stationIds.get(i + 1)));
            }
        }

        return deletedSections;
    }

    private int sectionDistance(Long upStationId, Long downStationId) {
        int distance = 0;

        for (Section section : sections) {
            if (section.getUpStationId().equals(upStationId)) {
                distance += section.getDistance();
            }
            if (section.getDownStationId().equals(downStationId)) {
                distance += section.getDistance();
            }
        }

        return distance;
    }

    public Section createdSection(Long lineId, Long stationId) {
        List<Long> stationIds = sortedStationIds();
        for (int i = 0; i < stationIds.size(); i++) {
            if (stationIds.get(i).equals(stationId)) {
                Long upStationId = stationIds.get(i - 1);
                Long downStationId = stationIds.get(i + 1);
                return new Section(lineId, upStationId, downStationId,
                    sectionDistance(upStationId, downStationId));
            }
        }
        throw new DeleteSectionException();
    }

}