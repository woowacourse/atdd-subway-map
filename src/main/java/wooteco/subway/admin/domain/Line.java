package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Line {
    @Id
    private Long id;
    private String name;
    private String bgColor;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private List<Edge> stations = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(Long id, String name, String bgColor, LocalTime startTime, LocalTime endTime, int intervalTime) {
        this.id = id;
        this.name = name;
        this.bgColor = bgColor;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = LocalDateTime.now();
    }

    public Line(String name, String bgColor, LocalTime startTime, LocalTime endTime, int intervalTime) {
        this(null, name, bgColor, startTime, endTime, intervalTime);
    }

    public Line(LocalTime startTime, LocalTime endTime, int intervalTime) {
        this(null, null, null, startTime, endTime, intervalTime);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBgColor() {
        return bgColor;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public List<Edge> getStations() {
        return stations;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void update(Line line) {
        if (line.getName() != null) {
            this.name = line.getName();
        }
        if (line.getBgColor() != null) {
            this.bgColor = line.getBgColor();
        }
        if (line.getStartTime() != null) {
            this.startTime = line.getStartTime();
        }
        if (line.getEndTime() != null) {
            this.endTime = line.getEndTime();
        }
        if (line.getIntervalTime() != 0) {
            this.intervalTime = line.getIntervalTime();
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void addEdge2(Edge edge) {
        List<Edge> relatedEdges = stations.stream()
                .filter(value -> value.getPreStationId().equals(edge.getPreStationId())
                        || value.getStationId().equals(edge.getPreStationId()))
                .collect(Collectors.toList());
    }

    public void addEdge(Edge edge) {
        Edge beforeEdge;

        if (stations.isEmpty()) {
            stations.add(edge);
            return;
        }
        if (edge.getPreStationId() == null) {
            beforeEdge = stations.get(0);
            stations.remove(0);
            stations.add(0, new Edge(edge.getStationId(), beforeEdge.getStationId()));
            stations.add(0, edge);
            return;
        }
        for (int i = 0; i < stations.size(); i++) {
            Edge lastEdge = stations.get(stations.size() - 1);

            if (edge.getPreStationId().equals(stations.get(i).getStationId())) {
                if (lastEdge.equals(stations.get(i))) {
                    stations.add(edge);
                    return;
                }
                Edge nextEdge = stations.get(i + 1);
                stations.add(i + 1, edge);
                stations.remove(nextEdge);
                stations.add(i + 2, new Edge(edge.getStationId(), nextEdge.getStationId()));
            }
        }
    }

    public void removeEdgeById(Long stationId) {
        if (stations.isEmpty()) {
            return;
        }
        // LS size == 1 -> 자기 자신만 있을 때 삭제 예외 처리
        // 첫번째 station (1) 삭제 ->  (null 1)
        if (findFirstEdge().getStationId().equals(stationId)) {
            Edge originSecondEdge = stations.get(1);
            Edge newFirstEdge = new Edge(null, originSecondEdge.getStationId());
            stations.remove(0);
            stations.remove(0);
            stations.add(0, newFirstEdge);
            return;
        }
        // 마지막 station 삭제
        if (findLastEdge().getStationId().equals(stationId)) {
            stations.remove(stations.size() - 1);
            return;
        }
        // 중간 station 삭제
        Map<String, Edge> betweenEdges = findBetweenEdgeById(id);
        int index = findIndexByStationId(id);
        Edge before = betweenEdges.get("before");
        Edge after = betweenEdges.get("after");
        stations.remove(before);
        stations.remove(after);
        Edge edge = new Edge(before.getPreStationId(), after.getStationId());
        stations.add(index, edge);
    }

    private Edge findFirstEdge() {
        return stations.get(0);
    }

    private Edge findLastEdge() {
        if (stations.size() == 1) {
            return stations.get(0);
        }
        return stations.get(stations.size() - 1);
    }

    private Map<String, Edge> findBetweenEdgeById(Long id) {
        Map<String, Edge> map = new HashMap<>();
        Edge before = stations.stream()
                .filter(value -> value.getPreStationId() != null && value.getPreStationId().equals(id))
                .findFirst()
                .get();
        Edge after = stations.stream()
                .filter(value -> value.getStationId().equals(id))
                .findFirst()
                .get();
        map.put("before", before);
        map.put("after", after);
        return map;
    }

    private int findIndexByStationId(Long id) {
        for (int i = 0; i < stations.size(); i++) {
            if (stations.get(i).getStationId().equals(id)) {
                return i;
            }
        }
        throw new IllegalArgumentException("해당 호선에 존재하지 않는 역입니다.");
    }

    public List<Long> findEdgesId() {
        List<Long> stationsIds = new ArrayList<>();
        for (Edge edge : stations) {
            stationsIds.add(edge.getStationId());
        }
        return stationsIds;
    }
}
