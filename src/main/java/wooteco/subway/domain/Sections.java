package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import wooteco.subway.SubwayException;

public class Sections {

    private static final long NO_EXIST = -1L;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    private Set<Long> generatedAllIds() {
        Set<Long> totalId = new HashSet<>();
        for (Section section : sections) {
            totalId.add(section.getUpStationId());
            totalId.add(section.getDownStationId());
        }
        return totalId;
    }

    public List<Long> getStations() {
        List<Long> result = new ArrayList<>();
        Long nowId = findUpperEndId();
        Map<Long, Long> upToDown = generateAdjacencyUpToDown();
        if (isCycle()) {
            nowId = Collections.min(upToDown.keySet());
        }
        Set<Long> visited = new HashSet<>();
        while (!visited.contains(nowId) && upToDown.containsKey(nowId)) {
            visited.add(nowId);
            result.add(nowId);
            nowId = upToDown.get(nowId);
        }
        result.add(nowId);
        return result;
    }

    private Map<Long, Long> generateAdjacencyUpToDown() {
        Map<Long, Long> upToDown = new HashMap<>();
        for (Section section : sections) {
            upToDown.put(section.getUpStationId(), section.getDownStationId());
        }
        return upToDown;
    }

    private Map<Long, Long> generateAdjacencyDownToUp() {
        Map<Long, Long> downToUp = new HashMap<>();
        for (Section section : sections) {
            downToUp.put(section.getDownStationId(), section.getUpStationId());
        }
        return downToUp;
    }

    public ModifyResult add(Section section) {
        validateOneStationShare(section);
        validateExistSection(section);
        if (isCycleAdd(section)) {
            return addCycle(section);
        }
        return addNonCycle(section);
    }

    private void validateOneStationShare(Section section) {
        Set<Long> totalId = generatedAllIds();
        if (!totalId.contains(section.getUpStationId()) &&
                !totalId.contains(section.getDownStationId())) {
            throw new SubwayException("[ERROR] 상행역과 하행역 중 하나는 공유되는 역이여야 합니다.");
        }
    }

    private void validateExistSection(Section section) {
        if (isCycleAdd(section)) {
            return;
        }
        Set<Long> totalId = generatedAllIds();
        if (totalId.contains(section.getUpStationId()) &&
                totalId.contains(section.getDownStationId())) {
            throw new SubwayException("[ERROR] 이미 존재하는 노선은 추가할 수 없습니다.");
        }
    }

    private boolean isCycleAdd(Section section) {
        Long upperEnd = findUpperEndId();
        Long lowerEnd = findLowerEndId();
        if (section.getUpStationId() == lowerEnd.longValue() &&
                section.getDownStationId() == upperEnd.longValue()) {
            return true;
        }
        return false;
    }

    private Long findUpperEndId() {
        Set<Long> downIds = generateAdjacencyDownToUp().keySet();
        Set<Long> totalId = generatedAllIds();
        return totalId.stream()
                .filter(id -> !downIds.contains(id))
                .findFirst()
                .orElse(NO_EXIST);
    }

    private Long findLowerEndId() {
        Set<Long> upIds = generateAdjacencyUpToDown().keySet();
        Set<Long> totalId = generatedAllIds();
        return totalId.stream()
                .filter(id -> !upIds.contains(id))
                .findFirst()
                .orElse(NO_EXIST);
    }

    private ModifyResult addCycle(Section section) {
        this.sections.add(section);
        return new ModifyResult(List.of(section), List.of());
    }

    private ModifyResult addNonCycle(Section section) {
        Set<Long> totalId = generatedAllIds();
        Long upperEnd = findUpperEndId();
        Long lowerEnd = findLowerEndId();
        if (section.getUpStationId() == lowerEnd.longValue() ||
                section.getDownStationId() == upperEnd.longValue()) {
            sections.add(section);
            totalId.add(section.getUpStationId());
            totalId.add(section.getDownStationId());
            return new ModifyResult(List.of(section), List.of());
        }
        return addByCondition(section);
    }

    private ModifyResult addByCondition(Section section) {
        Set<Long> totalId = generatedAllIds();
        if (totalId.contains(section.getUpStationId())) {
            return withDivide(section, findOverlappedByUpStation(section.getUpStationId()));
        }
        return withDivide(section, findOverlappedByDownStation(section.getDownStationId()));
    }

    private ModifyResult withDivide(Section addedSection, Section overlappedSection) {
        validateDistance(addedSection.getDistance(), overlappedSection.getDistance());
        Long nextDistance = overlappedSection.getDistance() - addedSection.getDistance();
        sections.add(addedSection);
        sections.remove(overlappedSection);
        if (addedSection.getUpStationId() == overlappedSection.getUpStationId().longValue()) {
            Section beSaved = new Section(addedSection.getLineId(), addedSection.getDownStationId(), overlappedSection.getDownStationId(), nextDistance);
            sections.add(beSaved);
            return new ModifyResult(List.of(addedSection, beSaved), List.of(overlappedSection));
        }
        Section beSaved = new Section(addedSection.getLineId(), overlappedSection.getUpStationId(), addedSection.getUpStationId(), nextDistance);
        sections.add(beSaved);
        return new ModifyResult(List.of(addedSection, beSaved), List.of(overlappedSection));
    }

    private Section findOverlappedByUpStation(Long stationId) {
        return this.sections.stream()
                .filter(eachSection -> eachSection.getUpStationId() == stationId.longValue())
                .findAny()
                .orElseThrow(() -> new SubwayException("[ERROR] 현재 구간과 상행역으로 겹치는 구간이 없습니다."));
    }

    private Section findOverlappedByDownStation(Long stationId) {
        return this.sections.stream()
                .filter(eachSection -> eachSection.getDownStationId() == stationId.longValue())
                .findAny()
                .orElseThrow(() -> new SubwayException("[ERROR] 현재 구간과 하행역으로 겹치는 구간이 없습니다."));
    }

    private void validateDistance(Long innerDistance, Long outerDistance) {
        if (outerDistance <= innerDistance) {
            throw new SubwayException("[ERROR] 현재 구간의 거리가 너무 깁니다.");
        }
    }

    private boolean isCycle() {
        return findUpperEndId() == NO_EXIST && findLowerEndId() == NO_EXIST;
    }

    public ModifyResult delete(Long lineId, Long stationId) {
        validateExistId(stationId);
        validateSize(1);
        if (isCycle()) {
            validateSize(2);
            return whenCycle(stationId);
        }
        return notCycle(lineId, stationId);
    }

    private ModifyResult whenCycle(Long stationId) {
        ModifyResult upperDeleteResult = notMerge(findOverlappedByUpStation(stationId));
        ModifyResult lowerDeleteResult = notMerge(findOverlappedByDownStation(stationId));
        return ModifyResult.addResult(upperDeleteResult, lowerDeleteResult);
    }

    private ModifyResult notCycle(Long lineId, Long stationId) {
        if (stationId == findUpperEndId().longValue()) {
            Section upperOverlap = findOverlappedByUpStation(stationId);
            return notMerge(upperOverlap);
        }
        if (stationId == findLowerEndId().longValue()) {
            Section lowerOverlap = findOverlappedByDownStation(stationId);
            return notMerge(lowerOverlap);
        }
        return withMerge(stationId, lineId);
    }

    private ModifyResult notMerge(Section overlappedSection) {
        sections.remove(overlappedSection);
        return new ModifyResult(List.of(), List.of(overlappedSection));
    }

    private ModifyResult withMerge(Long stationId, Long lineId) {
        Section upperOverlap = findOverlappedByUpStation(stationId);
        Section lowerOverlap = findOverlappedByDownStation(stationId);
        Section merged = new Section(lineId, lowerOverlap.getUpStationId(), upperOverlap.getDownStationId(), lowerOverlap.getDistance() + upperOverlap.getDistance());
        sections.add(merged);
        sections.remove(upperOverlap);
        sections.remove(lowerOverlap);
        return new ModifyResult(List.of(merged), List.of(upperOverlap, lowerOverlap));
    }

    private void validateSize(int minimum) {
        if (sections.size() == minimum) {
            throw new SubwayException("[ERROR] 노선을 유지할 수 없습니다.");
        }
    }

    private void validateExistId(Long stationId) {
        Set<Long> totalId = generatedAllIds();
        if (!totalId.contains(stationId)) {
            throw new SubwayException("[ERROR] 노선을 이루지 않는 역을 삭제할 수 없습니다.");
        }
    }
}
