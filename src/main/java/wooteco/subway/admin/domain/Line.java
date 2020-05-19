package wooteco.subway.admin.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Table("LINE")
public class Line {
    private static final int ONE_SIZE = 1;
    private static final int FIRST_INDEX = 0;
    private static final int SECOND_INDEX = 1;
    private static final int NEXT_INDEX = 1;
    private static final int BEFORE_INDEX = 1;
    @Id
    private Long id;
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    @MappedCollection(idColumn = "line", keyColumn = "sequence")
    private List<Edge> edges = new ArrayList<>();
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

    public List<Edge> getEdges() {
        return edges;
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
        int index = edges.stream()
                .filter(edge -> edge.isSameStationId(stationId))
                .map(edge -> edges.indexOf(edge))
                .findAny()
                .orElseThrow(NoSuchElementException::new);
        Edge preEdge;
        Edge nextEdge;

        if (isRemoveStationUnNormalCase(index)) {
            return;
        }
        nextEdge = edges.get(index + NEXT_INDEX);
        preEdge = edges.get(index - BEFORE_INDEX);
        nextEdge.updatePreStationId(preEdge.getStationId());
        edges.remove(index);
    }

    private boolean isRemoveStationUnNormalCase(int index) {
        Edge nextEdge;
        if (index == FIRST_INDEX && index == edges.size() - 1) {
            edges.remove(index);
            return true;
        }
        if (index == FIRST_INDEX) {
            nextEdge = edges.get(SECOND_INDEX);
            nextEdge.updatePreStationId(null);
            edges.remove(index);
            return true;
        }
        if (index == edges.size() - 1) {
            edges.remove(index);
            return true;
        }
        return false;
    }

    public List<Long> getEdgeIds() {
        return this.edges.stream()
                .mapToLong(Edge::getStationId)
                .boxed()
                .collect(Collectors.toList());
    }
}
