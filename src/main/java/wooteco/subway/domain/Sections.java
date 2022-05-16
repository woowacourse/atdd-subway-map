package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;

public class Sections {

    private static final int UP_STATION_SIZE = 1;
    private static final int UP_STATION_INDEX = 0;
    private static final int MINIMUM_SECTION_SIZE = 1;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public List<Long> getStationIds() {
        List<Long> stationIds = new ArrayList<>();

        Long upStationId = getUpStationId(sections);
        stationIds.add(upStationId);

        while (stationIds.size() != sections.size() + UP_STATION_SIZE) {
            Long downStationId = getDownStationId(sections, upStationId);
            stationIds.add(downStationId);
            upStationId = downStationId;
        }

        return stationIds;
    }

    private Long getUpStationId(List<Section> sections) {
        List<Long> upStationIds = sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());

        List<Long> downStationIds = sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());

        upStationIds.removeAll(downStationIds);
        return upStationIds.get(UP_STATION_INDEX);
    }

    private Long getDownStationId(List<Section> sections, Long upStationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(upStationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .getDownStationId();
    }

    public Sections getSectionsToDelete(Long stationId) {
        validateSectionSize(sections);
        List<Section> sectionsToDelete = sections.stream()
                .filter(section -> section.contains(stationId))
                .collect(Collectors.toList());
        validateDeletion(sectionsToDelete);
        return new Sections(sectionsToDelete);
    }

    private void validateSectionSize(List<Section> sections) {
        if (sections.size() == MINIMUM_SECTION_SIZE) {
            throw new IllegalArgumentException("구간을 삭제할 수 없습니다.");
        }
    }

    private void validateDeletion(List<Section> sectionsToDelete) {
        if (sectionsToDelete.isEmpty()) {
            throw new EmptyResultDataAccessException(0);
        }
    }

    public List<Long> getSectionIds() {
        return sections.stream()
                .map(Section::getId)
                .collect(Collectors.toList());
    }

    public Section merge() {
        return sections.get(0).merge(sections.get(1));
    }

    public int size() {
        return sections.size();
    }
}
