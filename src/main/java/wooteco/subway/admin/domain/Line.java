package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;

public class Line {
    @Id
    private Long id;
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String backgroundColor;
    @Embedded.Empty
    private LineStations lineStations;

    private Line() {
    }

    public Line(String title, LocalTime startTime, LocalTime endTime, int intervalTime, String backgroundColor) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.backgroundColor = backgroundColor;
        this.lineStations = new LineStations();
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
        if (line.getBackgroundColor() != null) {
            this.backgroundColor = line.getBackgroundColor();
        }

        this.updatedAt = LocalDateTime.now();
    }

    public void addLineStation(LineStation addLineStation) {
        lineStations.addLineStation(addLineStation);
    }

    public void removeLineStationById(Long stationId) {
        lineStations.removeLineStationById(stationId);
    }

    public List<Long> getLineStationIds() {
        return lineStations.getLineStationIds();
    }

    public boolean isStationsEmpty() {
        return lineStations.isStationsEmpty();
    }

    public boolean isEqualTitle(String title) {
        return this.title.equals(title);
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

    public Set<LineStation> getStations() {
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
