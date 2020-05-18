package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Line {
    @Id
    private Long id;
    private String name;
    private String bgColor;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer intervalTime;
    @Embedded.Empty
    private Edges edges = new Edges();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Line() {
    }

    public Line(Long id, String name, String bgColor, LocalTime startTime, LocalTime endTime, int intervalTime) {
        this.id = id;
        this.name = name;
        this.bgColor = bgColor;
        this.startTime = startTime;
        this.endTime = endTime;
        this.edges = new Edges();
        this.intervalTime = intervalTime;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Line toLine(String name, String bgColor, LocalTime startTime, LocalTime endTime, int intervalTime) {
        return new Line(null, name, bgColor, startTime, endTime, intervalTime);
    }

    public static Line toUpdatedLine(LocalTime startTime, LocalTime endTime, int intervalTime) {
        return new Line(null, null, null, startTime, endTime, intervalTime);
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
        if (line.getIntervalTime() != null) {
            this.intervalTime = line.getIntervalTime();
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void addEdge(Edge edge) {
        edges.addEdge(edge);
    }

    public void removeEdge(Long id) {
        edges.removeEdge(id);
    }

    public List<Long> findStationsId() {
        return edges.findStationsId();
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

    public Integer getIntervalTime() {
        return intervalTime;
    }

    public Edges getEdges() {
        return edges;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
