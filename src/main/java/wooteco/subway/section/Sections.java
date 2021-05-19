package wooteco.subway.section;

import wooteco.subway.exception.InvalidInsertException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.station.Station;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {
    private static final int ALL_SECTION_EXIST_COUNT = 2;
    private static final int NONE_EXIST_SECTION_COUNT = 0;
    private static final int DELETABLE_COUNT = 2;
    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sort(sections);
        validateDeletable();
    }

    public Sections(List<Section> sections, Section section) {
        this.sections = sort(sections);
        validateSavableSection(section);
    }

    private List<Section> sort(List<Section> sections) {
        Queue<Section> waiting = new LinkedList<>(sections);
        Deque<Section> result = new ArrayDeque<>();

        result.addLast(waiting.poll());
        sortUpToDown(waiting, result);

        return new ArrayList<>(result);
    }

    private void sortUpToDown(Queue<Section> waiting, Deque<Section> result) {
        while (!waiting.isEmpty()) {
            sortSectionSequence(waiting, result);
        }
    }

    private void sortSectionSequence(Queue<Section> waiting, Deque<Section> result) {
        Section current = waiting.poll();
        Section first = result.peekFirst();
        Section last = result.peekLast();
        if (current.isBefore(first)) {
            result.addFirst(current);
            return;
        }
        if (current.isAfter(last)) {
            result.addLast(current);
            return;
        }
        waiting.add(current);
    }

    public void validateSavableSection(Section section) {
        long matchCount = Stream.concat(upStationIds(sections).stream(), downStationIds(sections).stream())
                .distinct()
                .filter(id -> section.isSameAsUpId(id) || section.isSameAsDownId(id))
                .count();

        if (matchCount == ALL_SECTION_EXIST_COUNT || matchCount == NONE_EXIST_SECTION_COUNT) {
            throw new InvalidInsertException("해당 구간에는 등록할 수 없습니다.");
        }
    }

    public boolean isOnEdge(Section section) {
        Station downStation = section.getDownStation();
        Station upStation = section.getUpStation();
        return isOnUpEdge(downStation.getId())
                || isOnDownEdge(upStation.getId());
    }

    public boolean isOnUpEdge(Long downId) {
        return downId.equals(getFirstUpId());
    }

    public boolean isOnDownEdge(Long upId) {
        return upId.equals(getLastDownId());
    }

    private Long getFirstUpId() {
        return upStationIds(sections).stream()
                .filter(this::isNotMatchWithDownIds)
                .findAny()
                .orElseThrow(() -> new NotFoundException("상행역이 없습니다."));
    }

    private boolean isNotMatchWithDownIds(Long upId) {
        return downStationIds(sections).stream()
                .noneMatch(downId -> downId.equals(upId));
    }

    private Long getLastDownId() {
        return downStationIds(sections).stream()
                .filter(this::isNotMatchWithUpIds)
                .findAny()
                .orElseThrow(() -> new NotFoundException("하행역이 없습니다."));
    }

    private boolean isNotMatchWithUpIds(Long downId) {
        return upStationIds(sections).stream()
                .noneMatch(upId -> upId.equals(downId));
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

    public boolean appendToForward(Section newSection) {
        return containUpIdInUpIds(newSection) &&
                notContainDownIdInDownIds(newSection);
    }

    public boolean appendToBackward(Section newSection) {
        return containDownIdInDownIds(newSection) &&
                notContainUpIdInUpIds(newSection);
    }

    private boolean containUpIdInUpIds(Section newSection) {
        return upStationIds(sections).stream()
                .anyMatch(newSection::isSameAsUpId);
    }

    private boolean notContainDownIdInDownIds(Section newSection) {
        return downStationIds(sections).stream()
                .noneMatch(newSection::isSameAsDownId);
    }

    private boolean containDownIdInDownIds(Section newSection) {
        return downStationIds(sections).stream()
                .anyMatch(newSection::isSameAsDownId);
    }

    private boolean notContainUpIdInUpIds(Section newSection) {
        return upStationIds(sections).stream()
                .noneMatch(newSection::isSameAsDownId);
    }

    public List<Long> toSortedStationIds() {
        List<Long> stationIds = sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
        stationIds.add(lastStationId());
        return stationIds;
    }

    private Long lastStationId() {
        int lastIdx = sections.size() - 1;
        return sections.get(lastIdx).getDownStationId();

    }

    public void validateDeletable() {
        if (sections.size() < DELETABLE_COUNT) {
            throw new InvalidInsertException("구간이 한 개 이하라 삭제할 수 없습니다.");
        }
    }

    public Section findSectionByDown(Long stationId) {
        return sections.stream()
                .filter(section -> section.isSameAsDownId(stationId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("일치하는 하행역이 없습니다."));
    }

    public Section findSectionByUp(Long stationId) {
        return sections.stream()
                .filter(section -> section.isSameAsUpId(stationId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("일치하는 상행역이 없습니다."));
    }
}
