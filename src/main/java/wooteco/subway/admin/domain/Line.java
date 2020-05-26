package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;

public class Line {
    public static final long START_STATION = -1L;

    @Id
    private Long id;
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String backgroundColor;
    private Set<LineStation> stations;

    private Line() {
    }

    public Line(String title, LocalTime startTime, LocalTime endTime, int intervalTime, String backgroundColor) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.backgroundColor = backgroundColor;
        stations = new HashSet<>();
    }

    public void update(Line line) {
        if (line.getTitle() != null) {
            this.title = line.getTitle();
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

    private LineStation findByPreStationId(Long preStationId) {
        return stations.stream()
            .filter(lineStation -> lineStation.getPreStationId().equals(preStationId))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("LineStation 을 PreStationId 로 찾을 수 없습니다."));
    }

    private LineStation findById(Long stationId) {
        return stations.stream()
            .filter(lineStation -> lineStation.getStationId().equals(stationId))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("LineStation 을 StationId로 찾을 수 없습니다."));
    }

    public void addLineStation(LineStation addLineStation) {
        if (isSamePreStationIdWith(addLineStation.getPreStationId())) {
            LineStation updateLineStation = findByPreStationId(addLineStation.getPreStationId());
            updateLineStation.updatePreLineStation(addLineStation.getStationId());
        }
        this.stations.add(addLineStation);
    }

    public void removeLineStationById(Long stationId) {
        LineStation LineStation = findById(stationId);

        if (isSamePreStationIdWith(stationId)) {
            LineStation nextLineStation = findByPreStationId(LineStation.getStationId());
            nextLineStation.updatePreLineStation(LineStation.getPreStationId());
        }
        this.stations.remove(LineStation);
    }

    public List<Long> getLineStationIds() {
        LineStation firstLineStation = findByPreStationId(START_STATION);

        List<Long> stationIds = new ArrayList<>();
        stationIds.add(firstLineStation.getStationId());

        Long lastStationId = stationIds.get(stationIds.size() - 1);

        while (isSamePreStationIdWith(lastStationId)) {
            stationIds.add(findByPreStationId(lastStationId).getStationId());

            lastStationId = stationIds.get(stationIds.size() - 1);
        }

        return stationIds;
    }

    private boolean isSamePreStationIdWith(Long stationId) {
        return stations.stream()
            .anyMatch(lineStation -> lineStation.getPreStationId().equals(stationId));
    }

    public boolean isEqualTitle(String title) {
        return this.title.equals(title);
    }

    public boolean isStationsEmpty() {
        return stations.isEmpty();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
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

    public String getBackgroundColor() {
        return backgroundColor;
    }
}
