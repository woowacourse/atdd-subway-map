package wooteco.subway.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Stations {
    private static final Long NO_EXIST = -1L;

    private final Map<Long, Adjacency> adjacencies;

    public Stations(List<Section> sections) {
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
        StationInfo rightInfo = adjacencies.get(baseId).getRight();
        StationInfo leftInfo = adjacencies.get(baseId).getLeft();
        adjacencies.put(baseId, new Adjacency(new StationInfo(addId, newDistance), rightInfo.copyInfo()));
        if (leftInfo.isBlankLink()) {
            adjacencies.put(addId, new Adjacency(new StationInfo(NO_EXIST, 0L), new StationInfo(baseId, newDistance)));
            return;
        }
        StationInfo squareLeftInfo = adjacencies.get(leftInfo.getLinkedStationId()).getLeft();
        Long nextDistance = leftInfo.getDistance() - newDistance;
        adjacencies.put(addId, new Adjacency(new StationInfo(leftInfo.getLinkedStationId(), nextDistance), new StationInfo(baseId, newDistance)));
        adjacencies.put(leftInfo.getLinkedStationId(), new Adjacency(squareLeftInfo.copyInfo(), new StationInfo(addId, nextDistance)));
    }

    private void update_right(Long baseId, Long addId, Long newDistance) {
        StationInfo rightInfo = adjacencies.get(baseId).getRight();
        StationInfo leftInfo = adjacencies.get(baseId).getLeft();
        adjacencies.put(baseId, new Adjacency(leftInfo.copyInfo(), new StationInfo(addId, newDistance)));
        if (rightInfo.isBlankLink()) {
            adjacencies.put(addId, new Adjacency(new StationInfo(baseId, newDistance), new StationInfo(NO_EXIST, 0L)));
            return;
        }
        StationInfo squareRightInfo = adjacencies.get(rightInfo.getLinkedStationId()).getRight();
        Long nextDistance = rightInfo.getDistance() - newDistance;
        adjacencies.put(addId, new Adjacency(new StationInfo(baseId, newDistance), new StationInfo(
                rightInfo.getLinkedStationId(), nextDistance)));
        adjacencies.put(rightInfo.getLinkedStationId(), new Adjacency(new StationInfo(addId, nextDistance), squareRightInfo.copyInfo()));
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

    public void delete_cycle(Long stationId) {
        StationInfo rightInfo = adjacencies.get(stationId).getRight();
        StationInfo leftInfo = adjacencies.get(stationId).getLeft();
        Long rightStationId = rightInfo.getLinkedStationId();
        Long leftStationId = leftInfo.getLinkedStationId();
        adjacencies.put(rightStationId, new Adjacency(new StationInfo(NO_EXIST, 0L), adjacencies.get(rightStationId).copyRight()));
        adjacencies.put(leftStationId, new Adjacency(adjacencies.get(leftStationId).copyLeft(), new StationInfo(NO_EXIST, 0L)));
        adjacencies.remove(stationId);
    }

    public boolean isCycle() {
        return adjacencies.values().stream()
                .noneMatch(adjacency -> adjacency.isLeftBlank() || adjacency.isRightBlank());
    }

    public void delete(Long stationId) {
        StationInfo rightInfo = adjacencies.get(stationId).getRight();
        StationInfo leftInfo = adjacencies.get(stationId).getLeft();
        Long rightStationId = rightInfo.getLinkedStationId();
        Long leftStationId = leftInfo.getLinkedStationId();
        adjacencies.put(rightStationId, new Adjacency(leftInfo.copyInfo(), adjacencies.get(rightStationId).copyRight()));
        adjacencies.put(leftStationId, new Adjacency(adjacencies.get(leftStationId).copyLeft(), rightInfo.copyInfo()));
        adjacencies.remove(stationId);
    }
}
