package wooteco.subway.domain.section;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import wooteco.subway.exception.illegal.BothStationInLineException;
import wooteco.subway.exception.nosuch.BothStationNotInLineException;

public class Sections {
    private final List<Section> sections;
    private final List<Long> stationIds;

    public Sections(List<Section> sections, List<Long> orderedStationId) {
        this.sections = sections;
        this.stationIds = orderedStationId;
    }

    public static Sections of(List<Section> sections) {
        Long startStationId = makeStartStationId(sections);

        List<Long> orderedStationId = new ArrayList<>();
        List<Section> orderedSections = new ArrayList<>();

        orderedStationId.add(startStationId);

        while (orderedStationId.size() != sections.size() + 1) {
            long finalStartStationId = startStationId;
            startStationId = addDownStationId(sections, orderedStationId, orderedSections, finalStartStationId);
        }

        return new Sections(orderedSections, orderedStationId);
    }

    private static Long addDownStationId(List<Section> sections, List<Long> orderedStationId,
        List<Section> orderedSections, long finalStartStationId) {
        Long downStationId = sections.stream()
            .filter(section -> section.getUpStationId() == finalStartStationId)
            .peek(orderedSections::add)
            .map(Section::getDownStationId)
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
        orderedStationId.add(downStationId);
        return downStationId;
    }

    private static Long makeStartStationId(List<Section> sections) {
        List<Long> upStationIds = sections.stream()
            .map(Section::getUpStationId)
            .collect(Collectors.toList());

        List<Long> downStationIds = sections.stream()
            .map(Section::getDownStationId)
            .collect(Collectors.toList());

        return upStationIds.stream()
            .filter(id -> !downStationIds.contains(id))
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
    }

    public void validStations(long upStationId, long downStationId) {
        if (stationIds.containsAll(Arrays.asList(upStationId, downStationId))) {
            throw new BothStationInLineException();
        }

        if (!stationIds.contains(upStationId) && !stationIds.contains(downStationId)) {
            throw new BothStationNotInLineException();
        }
    }

    public boolean isEndStations(long upStationId, long downStationId) {
        return stationIds.get(0) == downStationId || stationIds.get(stationIds.size() - 1) == upStationId;
    }

    public boolean contains(long stationId) {
        return stationIds.contains(stationId);
    }

    public boolean canNotDelete() {
        return sections.size() == 1;
    }

    public Section getUpSection(long stationId) {
        return sections.stream()
            .filter(section -> section.getDownStationId() == stationId)
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
    }

    public Section getDownSection(long stationId) {
        return sections.stream()
            .filter(section -> section.getUpStationId() == stationId)
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
    }

    public List<Section> getSectionsWithStationId(long stationId) {
        return sections.stream()
            .filter(section -> section.contains(stationId))
            .distinct()
            .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Long> getStationIds() {
        return stationIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Sections that = (Sections)o;
        return Objects.equals(sections, that.sections) && Objects.equals(stationIds, that.stationIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections, stationIds);
    }
}
