package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class Line {
    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    @MappedCollection(idColumn = "line_id")
    private Set<LineStation> stations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime) {
        this(null, name, startTime, endTime, intervalTime);
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

    public Set<Station> convertStations(Function<Long, Optional<Station>> findFunction) {
        List<Long> lineStationsId = this.getLineStationsId();
        Set<Station> stations = new HashSet<>();
        for (Long id : lineStationsId) {
            Station station = findFunction.apply(id).orElseThrow(NoSuchElementException::new);
            stations.add(station);
        }
        return stations;
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
    }

    public void removeLineStationById(Long stationId) {
        // TODO: 구현
    }

    public List<Long> getLineStationsId() {
        // TODO: 구현
        return new ArrayList<>();
    }
}
