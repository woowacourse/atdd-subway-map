package wooteco.subway.admin.domain;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.annotation.Id;

import wooteco.subway.admin.exception.WrongIdException;

public class Line {

    public static final int FIRST = 0;

    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String bgColor;
    private final List<LineStation> stations = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime,
        String bgColor) {
        this(null, name, startTime, endTime, intervalTime, bgColor);
    }

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime,
        String bgColor) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.bgColor = bgColor;
        this.createdAt = LocalDateTime.now();
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

    public String getBgColor() {
        return bgColor;
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
        if (line.getBgColor() != null) {
            this.bgColor = line.getBgColor();
        }

        this.updatedAt = LocalDateTime.now();
    }

    public void addLineStation(LineStation lineStation) {
        if (Objects.isNull(lineStation.getPreStationId())) {
            stations.add(FIRST, lineStation);
            return;
        }

        Optional<LineStation> next = nextLineStation(lineStation);
        if (next.isPresent()) {
            LineStation realNext = next.get();
            realNext.updatePreLineStation(lineStation.getStationId());
            stations.add(stations.indexOf(realNext), lineStation);
            return;
        }

        stations.add(lineStation);
    }

    public void removeLineStationById(Long stationId) {
        LineStation lineStation = stations.stream()
            .filter(station -> station.getStationId().equals(stationId))
            .findAny()
            .orElseThrow(WrongIdException::new);

        stations.remove(lineStation);
    }

    public List<Long> getStationsIds() {
        return stations.stream()
            .map(LineStation::getStationId)
            .collect(toList());
    }

    private Optional<LineStation> nextLineStation(LineStation lineStation) {
        return stations.stream()
            .filter(station -> Objects.nonNull(station.getPreStationId()))
            .filter(station -> station.getPreStationId()
                .equals(lineStation.getPreStationId()))
            .findAny();
    }
}
