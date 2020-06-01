package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import wooteco.subway.admin.error.NotFoundException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Line {
    private static final int MIN_INTERVAL_TIME = 1;

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

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
        this(name, startTime, endTime, intervalTime, bgColor, new LinkedHashSet<>());
    }

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor, Set<LineStation> stations) {
        if (isNotValid(name, intervalTime, bgColor)) {
            throw new IllegalArgumentException("입력값이 잘못되었습니다.");
        }
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.bgColor = bgColor;
        this.stations = stations;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    private boolean isNotValid(String name, int intervalTime, String bgColor) {
        return (Objects.isNull(name)) || intervalTime < MIN_INTERVAL_TIME || Objects.isNull(bgColor);
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
        findNextLineStation(lineStation.getPreStationId())
                .ifPresent(it -> it.updatePreLineStation(lineStation.getStationId()));

        stations.add(lineStation);
    }

    public void removeLineStationById(Long stationId) {
        LineStation targetLineStation = stations.stream()
                .filter(it -> Objects.equals(it.getStationId(), stationId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(NotFoundException.STATION_NOT_FOUND));

        findNextLineStation(stationId)
                .ifPresent(it -> it.updatePreLineStation(targetLineStation.getPreStationId()));

        stations.remove(targetLineStation);
    }

    public List<Long> getLineStationsId() {
        if (stations.isEmpty()) {
            return new ArrayList<>();
        }

        LineStation firstLineStation = findFirstLineStation();

        List<Long> stationIds = new ArrayList<>(Collections.singletonList(firstLineStation.getStationId()));
        return lineUpStationsId(stationIds);
    }

    private Optional<LineStation> findNextLineStation(Long stationId) {
        return stations.stream()
                .filter(it -> Objects.equals(it.getPreStationId(), stationId))
                .findFirst();
    }

    private LineStation findFirstLineStation() {
        return stations.stream()
                .filter(it -> it.getPreStationId() == null)
                .findFirst()
                .orElse(new LineStation(null, 1L, 10, 10));
    }

    private List<Long> lineUpStationsId(List<Long> stationIds) {
        while (true) {
            Long lastStationId = stationIds.get(stationIds.size() - 1);
            Optional<LineStation> nextLineStation = findNextLineStation(lastStationId);

            if (!nextLineStation.isPresent()) {
                break;
            }

            stationIds.add(nextLineStation.get().getStationId());
        }

        return stationIds;
    }
}
