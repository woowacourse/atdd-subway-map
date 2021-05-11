package wooteco.subway.section;

import wooteco.subway.exception.SubWayException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {
    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sort(sections);
    }

    private List<Section> sort(List<Section> sections) {
        Queue<Section> waiting = new LinkedList<>(sections);
        Deque<Section> result = new ArrayDeque<>();

        result.addLast(waiting.remove());
        sortUpToDown(waiting, result);

        return new ArrayList<>(result);
    }

    private void sortUpToDown(Queue<Section> waiting, Deque<Section> result) {
        while (!waiting.isEmpty()) {
            Section section = waiting.remove();
            Section frontBase = result.peek();
            Section lastBase = result.peekLast();
            if (section.isSameUp(lastBase.getDownStationId())) {
                result.addLast(section);
                continue;
            }
            if (section.isSameDown(frontBase.getUpStationId())) {
                result.addFirst(section);
                continue;
            }
            waiting.add(section);
        }
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
        return containUpIdInUpIds(newSection) &&
                notContainDownIdInDownIds(newSection);
    }

    public boolean appendBeforeDown(Section newSection) {
        return containDownIdInDownIds(newSection) &&
                notContainUpIdInUpIds(newSection);
    }

    private boolean containUpIdInUpIds(Section newSection) {
        return upStationIds(sections).stream()
                .anyMatch(upId -> upId.equals(newSection.getUpStationId()));
    }

    private boolean notContainDownIdInDownIds(Section newSection) {
        return downStationIds(sections).stream()
                .noneMatch(downId -> downId.equals(newSection.getDownStationId()));
    }

    private boolean containDownIdInDownIds(Section newSection) {
        return downStationIds(sections).stream()
                .anyMatch(downId -> downId.equals(newSection.getDownStationId()));
    }

    private boolean notContainUpIdInUpIds(Section newSection) {
        return upStationIds(sections).stream()
                .noneMatch(upId -> upId.equals(newSection.getDownStationId()));
    }

    public List<Long> toSortedStationIds() {
        Long lastStationId = sections.get(sections.size()-1).getDownStationId();
        List<Long> stationIds = sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
        stationIds.add(lastStationId);
        return stationIds;
    }
}
