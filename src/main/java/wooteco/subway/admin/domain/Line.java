package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Line {
    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String lineColor;
    @MappedCollection
    private List<LineStation> stations = new LinkedList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, String lineColor) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.lineColor = lineColor;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime, String lineColor) {
        this(null, name, startTime, endTime, intervalTime, lineColor);
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
        if (line.getLineColor() != null) {
            this.lineColor = line.getLineColor();
        }

        this.updatedAt = LocalDateTime.now();
    }

    public void addLineStation(LineStation inputLineStation) {
        if (stations.isEmpty()) {
            stations.add(inputLineStation);
            return;
        }

        if (inputLineStation.isFirstLineStation()) {
            LineStation lineStation = getFirstLineStation();
            lineStation.updatePreStationId(inputLineStation.getStationId());
            stations.add(0, inputLineStation);
            return;
        }

        LineStation preLineStation = getPreStationOf(inputLineStation);
        if (isLastStation(preLineStation)) {
            stations.add(inputLineStation);
            return;
        }

        int index = stations.indexOf(preLineStation);
        LineStation nextByInputLineStation = stations.get(index + 1);
        nextByInputLineStation.updatePreStationId(inputLineStation.getStationId());
        stations.add(index + 1, inputLineStation);
    }

    private boolean isLastStation(LineStation lineStation) {
        return stations.indexOf(lineStation) == stations.size() - 1;
    }

    private boolean isNotLastStation(LineStation lineStation) {
        return !isLastStation(lineStation);
    }

    public void removeLineStationById(Long stationId) {
        LineStation targetLineStation = getStationBy(stationId);

        if (stations.size() == 1) {
            stations.remove(0);
            return;
        }

        if (targetLineStation.isFirstLineStation()) {
            stations.remove(0);
            stations.get(0).updatePreStationId(null);
            return;
        }

        if (isNotLastStation(targetLineStation)) {
            int index = stations.indexOf(targetLineStation);
            LineStation nextByTargetStation = stations.get(index + 1);
            nextByTargetStation.updatePreStationId(targetLineStation.getPreStationId());
        }

        stations.remove(targetLineStation);
    }

    private LineStation getStationBy(Long stationId) {
        return stations.stream()
                .filter(station -> station.is(stationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 역이 노선에 존재하지 않습니다."));
    }

    private LineStation getPreStationOf(LineStation inputLineStation) {
        return stations.stream()
                .filter(lineStation -> lineStation.isPreStationOf(inputLineStation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("연결될 수 없는 역을 입력하셨습니다."));
    }

    private LineStation getFirstLineStation() {
        return stations.stream()
                .filter(LineStation::isFirstLineStation)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("처음 역이 없습니다."));
    }

    public List<Long> getLineStationsId() {
        return stations.stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());
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

    public String getLineColor() {
        return lineColor;
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
}
