package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Line {
    @Id
    private Long id;
    private String name;
    private String bgColor;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private List<LineStation> stations = new ArrayList<>();
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

    public List<LineStation> getStations() {
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

    public void addLineStation(LineStation lineStation) {
        LineStation beforeLineStation;
        if (stations.isEmpty()) {
            stations.add(lineStation);
            return;
        }
        if (lineStation.getPreStationId() == null) {
            beforeLineStation = stations.get(0);
            stations.remove(0);
            stations.add(0, new LineStation(lineStation.getStationId(), beforeLineStation.getStationId()));
            stations.add(0, lineStation);
            return;
        }
        stations.add(lineStation);
    }

    public Set<LineStation> combineLineStations(Set<LineStation> frontLineStations, Set<LineStation> backLineStations) {
        Set<LineStation> lineStations = new LinkedHashSet<>();
        lineStations.addAll(frontLineStations);
        lineStations.addAll(backLineStations);
        return lineStations;
    }

    public void removeLineStationById(Long stationId) {

        // TODO: 구현
    }

    public List<Long> findLineStationsId() {
        List<Long> stationsIds = new ArrayList<>();
        for (LineStation lineStation : stations) {
            stationsIds.add(lineStation.getStationId());
        }
        return stationsIds;
    }

//
//    private LineStation findLineStationByPreStationId(Long id) {
//        for (LineStation lineStation : stations) {
//
//        }
//    }

}
