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
    private Set<LineStation> stations = new LinkedHashSet<>();
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

    public Set<LineStation> getStations() {
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
        // 처음에 추가
        if (lineStation.getPreStationId() == null) {
            Optional<LineStation> originFirstLineStation = stations.stream().findFirst();
            if (originFirstLineStation.isPresent()) {
                LineStation secondLineStation = new LineStation(lineStation.getStationId(),
                        originFirstLineStation.get().getStationId(), 10, 10);
                stations.remove(originFirstLineStation.get());
                stations.add(secondLineStation);
            }
        }
        //중간에 들어올 때 추가

        stations.add(lineStation);
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

    public List<LineStation> sortedLineStations() {
        List<LineStation> sorted = new ArrayList<>();
        for (LineStation station : stations) {

        }
        return sorted;
    }

    private LineStation findLineStationByPreStationId(Long id) {
        for (LineStation lineStation : stations) {

        }
    }

}
