package wooteco.subway.section;

import wooteco.subway.exception.InvalidInsertException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.line.Line;
import wooteco.subway.station.Station;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {
    private static final int ALL_SECTION_EXIST_COUNT = 2;
    private static final int NONE_EXIST_SECTION_COUNT = 0;
    private static final int DELETABLE_COUNT = 2;
    private static final int FIRST_SECTION = 0;


    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sort(sections);
    }

    public Sections(List<Section> sections, Section section) {
        this(sections);
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

    private void validateSavableSection(Section section) {
        long matchCount = Stream.concat(upStationIds(sections).stream(), downStationIds(sections).stream())
                .distinct()
                .filter(id -> section.isSameAsUpId(id) || section.isSameAsDownId(id))
                .count();

        if (matchCount == ALL_SECTION_EXIST_COUNT || matchCount == NONE_EXIST_SECTION_COUNT) {
            throw new InvalidInsertException("해당 구간에는 등록할 수 없습니다.");
        }
    }

    public void addSection(Line line, Section newSection) {
        if (isOnEdge(newSection)) {
            sections.add(newSection);
            return;
        }
        appendToForward(line, newSection);
        appendToBackward(line, newSection);

        sections.add(newSection);
    }

    public boolean isOnEdge(Section section) {
        Station downStation = section.getDownStation();
        Station upStation = section.getUpStation();
        return isOnUpEdge(downStation.getId())
                || isOnDownEdge(upStation.getId());
    }

    private boolean isOnUpEdge(Long downId) {
        return downId.equals(getFirstUpId());
    }

    private boolean isOnDownEdge(Long upId) {
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

    private void appendToForward(Line line, Section newSection) {
         sections.stream()
                .filter(section -> section.isSameAsUpId(newSection.getUpStation().getId()))
                .findFirst()
                .ifPresent(it -> addSectionToForward(it, newSection, line));
    }

    private void addSectionToForward(Section section, Section newSection, Line line) {
        int distance = section.subtractDistance(newSection);
        sections.add(new Section(line, newSection.getDownStation(), section.getDownStation(), distance));
        sections.remove(section);
    }

    private void appendToBackward(Line line, Section newSection) {
        sections.stream()
                .filter(section -> section.getDownStation().equals(newSection.getDownStation()))
                .findFirst()
                .ifPresent(section -> addSectionToBackward(section, newSection, line));
    }

    private void addSectionToBackward(Section section, Section newSection, Line line) {
        int distance = section.subtractDistance(newSection);
        sections.add(new Section(line, section.getUpStation(), newSection.getUpStation(), distance));
        sections.remove(section);
    }

    public void removeSection(Line line, Station station) {
        validateDeletable();
        if (isOnUpEdge(station.getId())) {
            sections.remove(FIRST_SECTION);
            return;
        }
        if (isOnDownEdge(station.getId())) {
            sections.remove(sections.size()-1);
            return;
        }
        removeSectionInMiddle(line, station);
    }

    private void removeSectionInMiddle(Line line, Station station) {
        Section beforeSection = findSameAsUpStation(station);
        Section afterSection = findSameAsDownStation(station);

        Station newUpStation = beforeSection.getUpStation();
        Station newDownStation = afterSection.getDownStation();
        int distance = beforeSection.plusDistance(afterSection);
        sections.add(new Section(line, newUpStation, newDownStation, distance));
        sections.remove(beforeSection);
        sections.remove(afterSection);
    }

    private Section findSameAsUpStation(Station station) {
        return sections.stream()
                .filter(it -> it.getDownStation().equals(station))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("일치하는 상행역이 없습니다."));
    }

    private Section findSameAsDownStation(Station station) {
        return sections.stream()
                .filter(it -> it.getUpStation().equals(station))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("일치하는 하행역이 없습니다."));
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

    private void validateDeletable() {
        if (sections.size() < DELETABLE_COUNT) {
            throw new InvalidInsertException("구간이 한 개 이하라 삭제할 수 없습니다.");
        }
    }

    public List<Section> toSortedSections() {
        return sort(sections);
    }
}
