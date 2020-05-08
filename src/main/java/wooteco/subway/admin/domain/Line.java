package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;

public class Line {
    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String backgroundColor;
    private List<LineStation> stations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, String backgroundColor) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.backgroundColor = backgroundColor;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.stations = new ArrayList<>();
    }

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime, String backgroundColor) {
        this(null, name, startTime, endTime, intervalTime, backgroundColor);
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

    public String getBackgroundColor() {
        return backgroundColor;
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

    public void addLineStation(LineStation lineStation) {
        if (stations.size() != 0) {
            for (int i = 0; i < stations.size(); i++) {
                if (stations.get(i).getPreStationId() == lineStation.getPreStationId()) {
                    stations.get(i).updatePreLineStation(lineStation.getStationId());
                    stations.add(i, lineStation);
                    return;
                }
            }
            stations.add(lineStation);
        }
        if (stations.size() == 0) {
            if (lineStation.getPreStationId() != null) {
                stations.add(new LineStation(null, lineStation.getPreStationId(), 0, 0));
            }
            stations.add(lineStation);
        }

    }

    public void removeLineStationById(Long stationId) {
        LineStation lineStation = stations.stream()
                .filter(station -> station.getStationId().equals(stationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 station 정보가 없습니다."));
        stations.remove(lineStation);

        for (int i = 0; i < stations.size() - 1; i++){
            if (i == 0) {
                if (stations.get(i).getPreStationId() != null) {
                    stations.get(i).updatePreLineStation(null);
                }
            }
            if (i != 0) {
                if (stations.get(i).getStationId() != stations.get(i+1).getPreStationId()) {
                    stations.get(i+1).updatePreLineStation(stations.get(i).getStationId());
                }
            }
        }
    }

    public List<Long> getLineStationsId() {
        return stations.stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());
    }
}
