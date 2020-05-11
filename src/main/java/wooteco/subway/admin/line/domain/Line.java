package wooteco.subway.admin.line.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Embedded;
import wooteco.subway.admin.line.domain.edge.Edge;
import wooteco.subway.admin.line.domain.edge.Edges;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Line {
    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String color;
    @Embedded.Empty
    private Edges edges = Edges.empty();
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    private Line() {
    }

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, final String color) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.color = color;
    }

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime, final String color) {
        this(null, name, startTime, endTime, intervalTime, color);
    }

    public void update(Line line) {
        if (line.getName() != null) {
            this.name = line.getName();
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
        if (line.getColor() != null) {
            this.color = line.getColor();
        }
    }

    public void addEdge(Edge edge) {
        if (edges.contain(edge)) {
            throw new IllegalArgumentException("이미 존재하는 구간입니다.");
        }
        edges.add(edge);
    }

    public void removeLineStationById(Long stationId) {
        edges.removeByStationId(stationId);
    }

    public List<Long> getEdgesStationIds() {
        return edges.getStationsId();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public Edges getEdges() {
        return this.edges;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getColor() {
        return color;
    }
}
