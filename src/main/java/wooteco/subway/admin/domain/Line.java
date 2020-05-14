package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;

public class Line {
    @Id
    private Long id;
    private String name;
    private String backgroundColor;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    @Embedded(onEmpty = Embedded.OnEmpty.USE_EMPTY)
    private LineStations lineStations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Line() {
    }

    public Line(String name, String backgroundColor, LocalTime startTime, LocalTime endTime,
        int intervalTime) {
        this.name = name;
        this.backgroundColor = backgroundColor;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lineStations = LineStations.createEmpty();
    }

    public void addLineStation(LineStation lineStation) {
        lineStations.add(lineStation);
    }

    public void removeLineStationById(Long stationId) {
        lineStations.remove(stationId);
    }

    public List<Long> getLineStationsIds() {
        return lineStations.getLineStationsIds();
    }

    public void update(Line line) {
        Objects.requireNonNull(line);
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
        this.updatedAt = LocalDateTime.now();
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

    public List<LineStation> getStations() {
        return lineStations.getStations();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }
}
