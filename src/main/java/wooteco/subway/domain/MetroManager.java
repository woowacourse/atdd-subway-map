package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

public class MetroManager {
    private static final Long NO_EXIST = -1L;

    private final Map<Long, Adjacency> adjacencies;

    public MetroManager(List<Section> sections) {
        this.adjacencies = new HashMap<>();
        Set<Long> totalId = new HashSet<>();
        Map<Long, StationInfo> leftInfos = new HashMap<>();
        Map<Long, StationInfo> rightInfos = new HashMap<>();
        for (Section section : sections) {
            leftInfos.put(section.getDownStationId(), new StationInfo(section.getUpStationId(), section.getDistance()));
            rightInfos.put(section.getUpStationId(), new StationInfo(section.getDownStationId(), section.getDistance()));
            totalId.add(section.getDownStationId());
            totalId.add(section.getUpStationId());
        }
        for (Long stationId : totalId) {
            Optional<StationInfo> leftInfo = Optional.ofNullable(leftInfos.get(stationId));
            Optional<StationInfo> rightInfo = Optional.ofNullable(rightInfos.get(stationId));
            adjacencies.put(stationId, new Adjacency(leftInfo.orElseGet(() -> new StationInfo(NO_EXIST, 0L)), rightInfo.orElseGet(() -> new StationInfo(NO_EXIST, 0L))));
        }
    }

    public void add_left(Section section) {
        Long upStationId = section.getUpStationId();
        Long downStationId = section.getDownStationId();
        Long distance = section.getDistance();
        update_left(downStationId, upStationId, distance);
    }

    private void update_left(Long baseId, Long addId, Long newDistance) {
        Long rightStationId = adjacencies.get(baseId).getRight().getLinkedStationId();
        Long rightDist = adjacencies.get(baseId).getRight().getDistance();
        Long leftStationId = adjacencies.get(baseId).getLeft().getLinkedStationId();
        Long leftDist = adjacencies.get(baseId).getLeft().getDistance();
        adjacencies.put(baseId, new Adjacency(new StationInfo(addId, newDistance), new StationInfo(rightStationId, rightDist)));
        if (leftStationId == NO_EXIST.longValue()) {
            adjacencies.put(addId, new Adjacency(new StationInfo(NO_EXIST, 0L), new StationInfo(baseId, newDistance)));
            return;
        }
        Long squareLeftId = adjacencies.get(leftStationId).getLeft().getLinkedStationId();
        Long squareLeftDist = adjacencies.get(leftStationId).getLeft().getDistance();
        Long nextDistance = leftDist - newDistance;
        adjacencies.put(addId, new Adjacency(new StationInfo(leftStationId, nextDistance), new StationInfo(baseId, newDistance)));
        adjacencies.put(leftStationId, new Adjacency(new StationInfo(squareLeftId, squareLeftDist), new StationInfo(addId, nextDistance)));
    }

    private void debug(Long id) {
        System.out.println("현재 id = " + id);
        System.out.println("왼쪽 id = " + this.adjacencies.get(id).getLeft().getLinkedStationId());
        System.out.println("오른쪽 id = " + this.adjacencies.get(id).getRight().getLinkedStationId());
        System.out.println("=============");
    }

    private void update_right(Long baseId, Long addId, Long newDistance) {
        Long rightStationId = adjacencies.get(baseId).getRight().getLinkedStationId();
        Long rightDist = adjacencies.get(baseId).getRight().getDistance();
        Long leftStationId = adjacencies.get(baseId).getLeft().getLinkedStationId();
        Long leftDist = adjacencies.get(baseId).getLeft().getDistance();
        adjacencies.put(baseId, new Adjacency(new StationInfo(leftStationId, leftDist), new StationInfo(addId, newDistance)));
        if (rightStationId.longValue() == NO_EXIST.longValue()) {
            adjacencies.put(addId, new Adjacency(new StationInfo(baseId, newDistance), new StationInfo(NO_EXIST, 0L)));
            return;
        }
        Long squareRightInfo = adjacencies.get(rightStationId).getRight().getLinkedStationId();
        Long squareRightDist = adjacencies.get(rightStationId).getRight().getDistance();
        Long nextDistance = rightDist - newDistance;
        adjacencies.put(addId, new Adjacency(new StationInfo(baseId, newDistance), new StationInfo(
                rightStationId, nextDistance)));
        adjacencies.put(rightStationId, new Adjacency(new StationInfo(addId, nextDistance), new StationInfo(squareRightInfo, squareRightDist)));
    }

    public void add_right(Section section) {
        Long upStationId = section.getUpStationId();
        Long downStationId = section.getDownStationId();
        Long distance = section.getDistance();
        update_right(upStationId, downStationId, distance);
    }

    public void add_cycle(Section section) {
        Long upStationId = section.getUpStationId();
        Long downStationId = section.getDownStationId();
        Long distance = section.getDistance();
        adjacencies.put(upStationId, new Adjacency(adjacencies.get(upStationId).copyLeft(),
                new StationInfo(downStationId, distance)));
        adjacencies.put(downStationId, new Adjacency(new StationInfo(upStationId, distance),
                adjacencies.get(downStationId).copyRight()));
    }

    public Adjacency getAdjacency(Long stationId) {
        return this.adjacencies.get(stationId);
    }

    public List<Section> delete_cycle(Long lineId, Long stationId) {
        StationInfo rightInfo = adjacencies.get(stationId).getRight();
        StationInfo leftInfo = adjacencies.get(stationId).getLeft();
        Long rightStationId = rightInfo.getLinkedStationId();
        Long leftStationId = leftInfo.getLinkedStationId();
        adjacencies.put(rightStationId, new Adjacency(new StationInfo(NO_EXIST, 0L), adjacencies.get(rightStationId).copyRight()));
        adjacencies.put(leftStationId, new Adjacency(adjacencies.get(leftStationId).copyLeft(), new StationInfo(NO_EXIST, 0L)));
        adjacencies.remove(stationId);
        List<Section> deletedSections = new ArrayList<>();
        if (!rightInfo.isBlankLink()) {
            deletedSections.add(new Section(lineId, stationId, rightStationId, rightInfo.getDistance()));
        }
        if (!leftInfo.isBlankLink()) {
            deletedSections.add(new Section(lineId, leftStationId, stationId, leftInfo.getDistance()));
        }
        return deletedSections;
    }

    public boolean isCycle() {
        return adjacencies.values().stream()
                .noneMatch(adjacency -> adjacency.isLeftBlank() || adjacency.isRightBlank());
    }

    public Map<DeleteResult, List<Section>> delete(Long lineId, Long stationId) {
        StationInfo rightInfo = adjacencies.get(stationId).getRight();
        StationInfo leftInfo = adjacencies.get(stationId).getLeft();
        Long rightStationId = rightInfo.getLinkedStationId();
        Long leftStationId = leftInfo.getLinkedStationId();
        Map<DeleteResult, List<Section>> deleteResult = new HashMap<>();
        List<Section> deletedSections = new ArrayList<>();
        List<Section> savedSections = new ArrayList<>();
        deleteResult.put(DeleteResult.NEW_SAVE, savedSections);
        deleteResult.put(DeleteResult.NEW_DELETE, deletedSections);
        if (rightStationId != NO_EXIST.longValue() && leftStationId != NO_EXIST.longValue()) {
            adjacencies.put(rightStationId, new Adjacency(new StationInfo(leftStationId, leftInfo.getDistance() + rightInfo.getDistance()), adjacencies.get(rightStationId).copyRight()));
            adjacencies.put(leftStationId, new Adjacency(adjacencies.get(leftStationId).copyLeft(), new StationInfo(rightStationId, rightInfo.getDistance() + leftInfo.getDistance())));
            StationInfo newLeftInfo = adjacencies.get(rightStationId).getLeft();
            Long newLeftStationId = newLeftInfo.getLinkedStationId();
            savedSections.add(new Section(lineId, newLeftStationId, rightStationId, newLeftInfo.getDistance()));
        }
        deleteResult.put(DeleteResult.NEW_SAVE, savedSections);
        adjacencies.remove(stationId);
        if (!rightInfo.isBlankLink()) {
            deletedSections.add(new Section(lineId, stationId, rightStationId, rightInfo.getDistance()));
        }
        if (!leftInfo.isBlankLink()) {
            deletedSections.add(new Section(lineId, leftStationId, stationId, leftInfo.getDistance()));
        }
        deleteResult.put(DeleteResult.NEW_DELETE, deletedSections);
        return deleteResult;
    }

    public Long findUpStationEnd() {
        return adjacencies.entrySet().stream()
                .filter(station -> station.getValue().isLeftBlank())
                .mapToLong(Entry::getKey)
                .findFirst()
                .orElseGet(() -> NO_EXIST);
    }

    public Long findDownStationEnd() {
        return adjacencies.entrySet().stream()
                .filter(station -> station.getValue().isRightBlank())
                .mapToLong(Entry::getKey)
                .findFirst()
                .orElseGet(() -> NO_EXIST);
    }

    public boolean isIn(Long stationId) {
        return this.adjacencies.containsKey(stationId);
    }

    public List<Long> getStationsId() {
        Long now = new ArrayList<Long>(this.adjacencies.keySet()).get(0);
        boolean checkCycle = true;
        if (!isCycle()) {
            checkCycle = false;
            now = findUpStationEnd();
        }
        List<Long> result = new ArrayList<>();
        Map<Long, Boolean> visited = new HashMap<>();
        for (Long ids : this.adjacencies.keySet()) {
            visited.put(ids, false);
        }
        while (now != NO_EXIST.longValue() && !visited.get(now)) {
            result.add(now);
            visited.put(now, true);
            now = this.adjacencies.get(now).getRight().getLinkedStationId();
        }
        if (checkCycle) {
            result.add(result.get(0));
        }
        return result;
    }

    public boolean isOneExist() {
        return this.adjacencies.keySet().size() == 2;
    }

    public List<Section> generateUpdatedSection(Long lineId) {
        Set<Long> visitedId = new HashSet<>();
        Long now = findUpStationEnd();
        List<Section> result = new ArrayList<>();
        if (isCycle()) {
            now = new ArrayList<Long>(this.adjacencies.keySet()).get(0);
        }
        while (!visitedId.contains(now)) {
            visitedId.add(now);
            if (this.adjacencies.get(now).isRightBlank()) {
                break;
            }
            StationInfo right = this.adjacencies.get(now).getRight();
            result.add(new Section(lineId, now, right.getLinkedStationId(), right.getDistance()));
            now = right.getLinkedStationId();
        }
        return result;
    }

}
