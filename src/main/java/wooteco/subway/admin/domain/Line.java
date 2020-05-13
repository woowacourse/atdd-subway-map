package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("LINE")
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
    private List<LineStation> lineStations = new LinkedList<>();
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

    public List<LineStation> getLineStations() {
        return Collections.unmodifiableList(lineStations);
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
        if (doAlreadyExist(lineStation)) {
            throw new IllegalArgumentException(this.id + " line에 해당 역이 이미 존재합니다.");
        }
        if (!isInOrder(lineStation)) {
            throw new IllegalArgumentException("라인에 역 등록은 시작역부터 순서대로 해주세요.");
        }
        this.lineStations.add(lineStation);
    }

    private boolean doAlreadyExist(LineStation lineStation) {
        return this.lineStations.stream()
            .anyMatch(anyLineStation -> anyLineStation.isStationId(lineStation.getStationId()));
    }

    private boolean isInOrder(LineStation lineStation) {
        return (lineStations.isEmpty() && lineStation.isStart())
            || lineStation.isPreStationId(lineStations.get(this.lineStations.size() - 1).getStationId());
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
        return lineStations.stream().anyMatch(LineStation::isStart);
    }

    public String getColor() {
        return color;
    }
}
