package wooteco.subway.section;

import wooteco.subway.exception.SubWayException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {
    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void validateSavableSection(Section section) {
        long matchCount = Stream.concat(upStationIds(sections).stream(), downStationIds(sections).stream())
                .distinct()
                .filter(id -> id.equals(section.getUpStationId()) || id.equals(section.getDownStationId()))
                .count();

        if (matchCount == 2 || matchCount == 0) {
            throw new SubWayException("등록 불가능한 구간입니다.");
        }
    }

    public List<Section> toList() {
        return new ArrayList<>(sections);
    }

    public boolean isOnEdge(Section section) {
        return isOnUpEdge(section) || isOnDownEdge(section);
    }

    private boolean isOnDownEdge(Section section) {
        return section.getUpStationId().equals(getLastDownId());
    }

    private Long getFirstUpId() {
        return upStationIds(sections).stream()
                .filter(upId -> downStationIds(sections).stream().noneMatch(downId -> downId.equals(upId)))
                .findAny()
                .orElseThrow(() -> new SubWayException("상행역이 없습니다."));
    }

    private Long getLastDownId() {
        return downStationIds(sections).stream()
                .filter(downId -> upStationIds(sections).stream().noneMatch(upId -> upId.equals(downId)))
                .findAny()
                .orElseThrow(() -> new SubWayException("하행역이 없습니다."));
    }

    private boolean isOnUpEdge(Section section) {
        return section.getDownStationId().equals(getFirstUpId());
    }

    private List<Long> upStationIds(List<Section> sections) {
        return sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
    }

    private List<Long> downStationIds(List<Section> sections) {
        return sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());
    }

    public boolean appendToUp(Section newSection) {
        return upStationIds(sections).stream()
                .anyMatch(upId -> upId.equals(newSection.getUpStationId())) &&
                        downStationIds(sections).stream().noneMatch(downId -> downId.equals(newSection.getDownStationId()));
    }

    public boolean appendBeforeDown(Section newSection) {
        return downStationIds(sections).stream()
                .anyMatch(downId -> downId.equals(newSection.getDownStationId())) &&
                        upStationIds(sections).stream().noneMatch(upId -> upId.equals(newSection.getDownStationId()));
    }
}
