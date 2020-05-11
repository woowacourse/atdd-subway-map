package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class Line {
    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String color;
    private Set<LineStation> stations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, String color) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.color = color;
        this.stations = new LinkedHashSet<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime, String color) {
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
        if (!line.getStations().isEmpty()) {
            this.stations = line.getStations();
        }
        if (line.getColor() != null) {
            this.color = line.getColor();
        }

        this.updatedAt = LocalDateTime.now();
    }

    public void addLineStation(LineStation lineStation) {
        if(!stations.isEmpty()) {
            realignPreviousLinks(lineStation);
        }
        stations.add(lineStation);
    }

    private void realignPreviousLinks(LineStation lineStation) {
        if (isInsertingAtFirst(lineStation)){
            LineStation firstStation = stations.stream()
                    .filter(eachLineStation -> eachLineStation.getPreStationId() == null)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("노선 시작점이 없습니다."));
            firstStation.updatePreLineStation(lineStation.getStationId());
        }else if(isInsertingBetween(lineStation)) {
            LineStation previousLineStation = stations.stream()
                    .filter(eachLineStation -> lineStation.getPreStationId().equals(eachLineStation.getPreStationId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 역명입니다."));
            previousLineStation.updatePreLineStation(lineStation.getStationId());
        }
    }

    private boolean isInsertingAtFirst(LineStation lineStation) {
        return lineStation.getPreStationId() == null;
    }

    private boolean isInsertingBetween(LineStation lineStation) {
        return stations.stream()
                .filter(eachLineStation -> eachLineStation.getPreStationId() != null)
                .anyMatch(eachLineStation -> eachLineStation.getPreStationId()
                        .equals(lineStation.getPreStationId()));
    }

    public void removeLineStationById(Long stationId) {
        LineStation previousLineStation = null;
        LineStation nextLineStation = null;

        for (LineStation lineStation : stations){
            if(stationId.equals(lineStation.getStationId())) {
                previousLineStation = lineStation;
            } else if(stationId.equals(lineStation.getPreStationId())){
                nextLineStation = lineStation;
            }
        }

        if(nextLineStation != null) {
            nextLineStation.updatePreLineStation(previousLineStation.getPreStationId());
        }
        stations.remove(previousLineStation);
    }

    public List<Long> getLineStationsId() {
        if(stations.isEmpty()) {
            return new ArrayList<>();
        }

        LineStation firstLineStation = stations.stream()
                .filter(lineStation -> lineStation.getPreStationId() == null)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        List<Long> stationIds = new ArrayList<>();

        stationIds.add(firstLineStation.getStationId());

        while(true) {
            Long lastStationId = stationIds.get(stationIds.size() - 1);
            Optional<LineStation> nextLineStation = stations.stream()
                    .filter(lineStation -> Objects.equals(lineStation.getPreStationId(), lastStationId))
                    .findFirst();

            if(!nextLineStation.isPresent()) {
                break;
            }

            stationIds.add(nextLineStation.get().getStationId());
        }

        return stationIds;
    }
}
