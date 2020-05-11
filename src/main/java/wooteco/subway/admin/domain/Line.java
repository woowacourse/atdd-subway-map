package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Table("Line")
public class Line {
    private static final long PRE_ID_OF_FIRST_STATION = -1L;

    @Id
    private Long id;
    @Column("name")
    private String name;
    @Column("start_time")
    private LocalTime startTime;
    @Column("end_time")
    private LocalTime endTime;
    @Column("interval_time")
    private int intervalTime;
    @Column("line")
    private Set<LineStation> lineStations = new HashSet<>();
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("updated_at")
    private LocalDateTime updatedAt;
    @Column("color")
    private String color;

    public Line() {}

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, String color) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.color = color;
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

        this.updatedAt = LocalDateTime.now();
    }

    public void addLineStation(LineStation lineStation) {
        Optional<LineStation> lineStationWithSamePreStation = this.lineStations.stream()
                .filter(anyLineStation -> anyLineStation.isPreStationId(lineStation.getPreStationId()))
                .findFirst();
        lineStationWithSamePreStation.ifPresent(station ->
            updatePreOfLineStation(station.getStationId(), lineStation.getStationId()));
        this.lineStations.add(lineStation);
    }

    public void removeLineStationByStationId(Long stationId) {
        Optional<LineStation> preStationId = this.lineStations.stream()
                .filter(lineStation -> lineStation.isStationId(stationId))
                .findFirst();
        Optional<LineStation> nextStationId = this.lineStations.stream()
                .filter(lineStation -> lineStation.isPreStationId(stationId))
                .findFirst();
        if (nextStationId.isPresent() && preStationId.isPresent()) {
            updatePreOfLineStation(nextStationId.get().getStationId(), preStationId.get().getPreStationId());
        }
        this.lineStations.removeIf(lineStation -> lineStation.isStationId(stationId));
    }

    private void updatePreOfLineStation(Long stationId, Long newPreStationId) {
        this.lineStations.stream()
                .filter(lineStation -> lineStation.isStationId(stationId))
                .forEach(lineStation -> lineStation.updatePreStationId(newPreStationId));
    }

    public List<Long> getSortedStationIds() {
        List<Long> orderedStations = new ArrayList<>();

        if (lineStations.isEmpty()) {
            return orderedStations;
        }
        if (!isExistStartStation()) {
            throw new IllegalStateException("구간이 존재하는데 시작역이 존재하지 않을 수 없습니다.");
        }
        return getSortedStationIDsWhenStartStationExist();
    }

    private List<Long> getSortedStationIDsWhenStartStationExist() {
        List<Long> orderedStations = new ArrayList<>();
        Map<Long, Long> stationIdsPerPreId = createStationIdsPerPreId();

        Long now = PRE_ID_OF_FIRST_STATION;
        for (int i = 0; i < stationIdsPerPreId.size(); i++) {
            now = stationIdsPerPreId.get(now);
            orderedStations.add(now);
        }
        return Collections.unmodifiableList(orderedStations);
    }

    /**
     * @return key: 전 역 ID, value: 현재 역 ID
     * 단, 시작역의 preStationId 는 -1L (constant : PRE_ID_OF_FIRST_STATION) 로 대체함 */
    private Map<Long, Long> createStationIdsPerPreId() {
        Map<Long, Long> stationIdOrder = new HashMap<>();

        lineStations.forEach(lineStation -> {
            if (Objects.isNull(lineStation.getPreStationId())) {
                stationIdOrder.put(PRE_ID_OF_FIRST_STATION, lineStation.getStationId());
            } else {
                stationIdOrder.put(lineStation.getPreStationId(), lineStation.getStationId());
            }
        });
        return Collections.unmodifiableMap(stationIdOrder);
    }

    private boolean isExistStartStation() {
        return lineStations.stream().anyMatch(LineStation::isNotExistPreStation);
    }

    public String getColor() {
        return color;
    }
}
