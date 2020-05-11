package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.annotation.Id;

public class Line {
    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String color;
    private Set<LineStation> lineStations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime,
        String color) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.color = color;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lineStations = new LinkedHashSet<>();
        // TODO: 2020/05/11 이부분때문에, showLine하면 스테이션즈가 전달 안됨
    }

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime,
        String color) {
        this(null, name, startTime, endTime, intervalTime, color);
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

    public String getColor() {
        return color;
    }

    public Set<LineStation> getLineStations() {
        return lineStations;
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
        if (line.getColor() != null) {
            this.color = line.getColor();
        }
        if (line.getLineStations() != null) {
            this.lineStations = line.getLineStations();
        }

        this.updatedAt = LocalDateTime.now();
    }

    public void addLineStation(LineStation lineStation) {
        lineStations.add(lineStation);

        Long preStationId = lineStation.getPreStationId();
        Long stationId = lineStation.getStationId();

        Iterator iterator = lineStations.iterator();
        while (iterator.hasNext()) {
            LineStation selectedLineStation = (LineStation)iterator.next();
            if (selectedLineStation.getPreStationId().equals(preStationId)
                && !selectedLineStation.getStationId().equals(stationId)) {
                selectedLineStation.setPreStationId(stationId);
                break;
            }
        }
    }

    public void removeLineStationById(Long stationId) {
        Long preStationId = 0L;
        Iterator iterator = lineStations.iterator();
        while (iterator.hasNext()) {
            LineStation selectedLineStation = (LineStation)iterator.next();
            if (selectedLineStation.getStationId().equals(stationId)) {
                preStationId = selectedLineStation.getPreStationId();
                break;
            }
        }
        lineStations.removeIf(lineStation -> lineStation.getStationId().equals(stationId));
        iterator = lineStations.iterator();
        while (iterator.hasNext()) {
            LineStation selectedLineStation = (LineStation)iterator.next();
            if (selectedLineStation.getPreStationId().equals(stationId)) {
                selectedLineStation.setPreStationId(preStationId);
                break;
            }
        }
    }

    public List<Long> findLineStationsId() {
        Map<Long, Long> idMap = new HashMap<>();
        Iterator iterator = lineStations.iterator();
        while (iterator.hasNext()) {
            LineStation lineStation = (LineStation)iterator.next();
            idMap.put(lineStation.getPreStationId(), lineStation.getStationId());
        }
        List<Long> stations = new ArrayList<>();
        Long preStationId = 0L;
        while (idMap.containsKey(preStationId)) {
            Long stationId = idMap.get(preStationId);
            stations.add(stationId);
            preStationId = stationId;
        }
        return stations;
    }
}
