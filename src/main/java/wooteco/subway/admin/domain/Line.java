package wooteco.subway.admin.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Table("LINE")
public class Line {
    @Id
    private Long id;
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private Set<Edge> edges = new HashSet<>();
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private String bgColor;

    public Line() {
    }

    public Line(Long id, String title, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.bgColor = bgColor;
    }

    public Line(String title, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
        this(null, title, startTime, endTime, intervalTime, bgColor);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
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

    public List<Edge> getSortedEdges() {
        if (edges.isEmpty()) {
            return Collections.emptyList();
        }
        List<Edge> sortedEdges = new ArrayList<>();
        Edge pivotEdge = searchFirstEdge();
        sortedEdges.add(pivotEdge);
        while (isNotLastEdge(pivotEdge)) {
            pivotEdge = searchNextEdgeOf(pivotEdge);
            sortedEdges.add(pivotEdge);
        }
        return sortedEdges;
    }

    private Edge searchFirstEdge() {
        return edges.stream()
                .filter(edge -> edge.getPreStationId() == null)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    private boolean isNotLastEdge(Edge edge) {
        try {
            searchNextEdgeOf(edge);
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;
    }

    private Edge searchNextEdgeOf(Edge pivotEdge) {
        return edges.stream()
                .filter(edge -> edge.getPreStationId() == pivotEdge.getStationId())
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void update(Line line) {
        if (line.getTitle() != null) {
            this.title = line.getTitle();
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
        if (line.getBgColor() != null) {
            this.bgColor = line.getBgColor();
        }
    }

    public void addEdge(Edge requestEdge) {
        edges.stream()
                .filter(edge -> Objects.equals(edge.getPreStationId(), requestEdge.getPreStationId()))
                .findAny()
                .ifPresent(edge -> edge.updatePreStationId(requestEdge.getStationId()));

        edges.add(requestEdge);
    }

    public void removeEdgeById(Long stationId) {
        Edge targetEdge = edges.stream()
                .filter(it -> Objects.equals(it.getStationId(), stationId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        edges.stream()
                .filter(it -> Objects.equals(it.getPreStationId(), stationId))
                .findFirst()
                .ifPresent(it -> it.updatePreStationId(targetEdge.getPreStationId()));

        edges.remove(targetEdge);
    }

    public List<Long> getEdgeIds() {
        return getSortedEdges()
                .stream()
                .mapToLong(Edge::getStationId)
                .boxed()
                .collect(Collectors.toList());
    }

    public Set<Edge> getEdges() {
        return edges;
    }
}
