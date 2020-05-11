package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Line {
    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String bgColor;
    private Set<LineStation> stations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
        if (isNotValid(name, intervalTime, bgColor)) {
            throw new IllegalArgumentException("입력값이 잘못되었습니다.");
        }
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.bgColor = bgColor;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    private boolean isNotValid(String name, int intervalTime, String bgColor) {
        if (name == null || bgColor == null) {
            return true;
        }

        return intervalTime <= 0;
    }


    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
        this(null, name, startTime, endTime, intervalTime, bgColor);
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

    public Set<LineStation> getStations() {
        return stations;
    }

    public String getBgColor() {
        return bgColor;
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

        this.updatedAt = LocalDateTime.now();
    }

    public void addLineStation(LineStation lineStation) {
        if (lineStation.getPreStationId() == null && this.stations != null) {
            Set<LineStation> tmp = this.stations;
            this.stations = new LinkedHashSet<>();
            this.stations.add(lineStation);
            this.stations.addAll(tmp);
            return;

        }

        if (lineStation.getPreStationId() != null && this.stations != null) {
            Set<LineStation> tmp = this.stations;
            this.stations = new LinkedHashSet<>();
            for (LineStation lineStationGot : tmp) {
                if (lineStationGot.getPreStationId() == null) {
                    this.stations.add(lineStationGot);
                } else if (!lineStationGot.getPreStationId().equals(lineStation.getPreStationId())) {
                    this.stations.add(lineStationGot);
                } else {
                    this.stations.add(lineStation);
                    lineStationGot.updatePreLineStation(lineStation.getStationId());
                    this.stations.add(lineStationGot);
                }
            }

        }

        if (this.stations == null) {
            this.stations = new LinkedHashSet<>();
        }


        this.stations.add(lineStation);
    }

    public void removeLineStationById(Long stationId) {
        for (LineStation lineStation : stations) {
            if (lineStation.getStationId().equals(stationId)) {
                stations.remove(lineStation);
                break;
            }
        }
    }

    public List<Long> getLineStationsId() {
        return stations.stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());
    }
}
