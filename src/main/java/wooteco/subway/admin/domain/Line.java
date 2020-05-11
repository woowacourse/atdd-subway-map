package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("LINE")
public class Line {
    @Id
    private Long id;
    private String name;
    private String bgColor;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private Set<LineStation> stations = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(Long id, String name, String bgColor, LocalTime startTime, LocalTime endTime,
        int intervalTime) {
        this.name = name;
        this.bgColor = bgColor;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Line(String name, String bgColor, LocalTime startTime, LocalTime endTime,
        int intervalTime) {
        this(null, name, bgColor, startTime, endTime, intervalTime);
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
        // TODO: 구현
        List<Long> ids = this.findLineStationsId();
        if (ids.size() == 0 || ids.get(ids.size() - 1).equals(lineStation.getPreStationId())) {
            stations.add(lineStation);
            return;
        }
        if (lineStation.getPreStationId() == null) {
            stations.stream()
                .filter(station -> station.getPreStationId() == null)
                .findFirst()
                .get()
                .updatePreLineStation(lineStation.getStationId());
            stations.add(lineStation);
            return;
        }
        stations.stream()
            .filter(station -> lineStation.getPreStationId().equals(station.getPreStationId()))
            .findFirst()
            .get()
            .updatePreLineStation(lineStation.getStationId());
        stations.add(lineStation);
    }

    public void removeLineStationById(Long stationId) {
        // TODO: 구현
        Long previousId = null;
        for (LineStation station : stations) {
            if (stationId.equals(station.getStationId())) {
                previousId = station.getPreStationId();
                stations.remove(station);
                break;
            }
        }
        for (LineStation station : stations) {
            if (stationId.equals(station.getPreStationId())) {
                stations.add(
                    new LineStation(previousId, station.getStationId(), station.getDistance(),
                        station.getDuration()));
                stations.remove(station);
                break;
            }
        }
    }

    public List<Long> findLineStationsId() {
        // TODO: 구현
        List<Long> ids = new ArrayList<>();
        for (LineStation station : stations) {
            if (station.getPreStationId() == null) {
                ids.add(station.getStationId());
            }
        }
        for (int i = 0; i < stations.size() - 1; i++) {
            for (LineStation lineStation : stations) {
                if (lineStation.getPreStationId().equals(ids.get(ids.size() - 1))) {
                    ids.add(lineStation.getStationId());
                }
            }
        }
        return ids;
    }
}
