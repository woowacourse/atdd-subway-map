package wooteco.subway.admin.domain;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import wooteco.subway.admin.controller.exception.InvalidLineFieldException;
import wooteco.subway.admin.controller.exception.LineStationCreateException;
import wooteco.subway.admin.controller.exception.NoLineExistException;
import wooteco.subway.admin.controller.exception.NoStationExistException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Line {
    private static final int ADD_ON_FIRST_INDEX = 0;
    private static final int FIRST_INDEX = 0;
    private static final int ONE_STATION = 1;
    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String lineColor;
    @MappedCollection
    private List<LineStation> stations = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, String lineColor) {
        validate(name, lineColor);

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

    private void validate(String name, String lineColor) {
        if (StringUtils.isBlank(name)) {
            throw new InvalidLineFieldException("노선 이름이 입력되지 않았습니다.");
        }

        if (StringUtils.isBlank(lineColor)) {
            throw new InvalidLineFieldException("노선 색상이 입력되지 않았습니다.");
        }
    }

    public void addLineStationOnFirst(LineStation inputLineStation) {
        validateFirstLineStationFormat(inputLineStation);

        if (stations.isEmpty()) {
            stations.add(ADD_ON_FIRST_INDEX, inputLineStation);
            return;
        }

        LineStation lineStation = stations.stream()
                .filter(LineStation::isFirstLineStation)
                .findFirst()
                .orElseThrow(() -> new NoStationExistException("처음 역이 없습니다."));

        lineStation.updatePreStationId(inputLineStation.getStationId());
        stations.add(ADD_ON_FIRST_INDEX, inputLineStation);
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

    private void validateFirstLineStationFormat(LineStation inputLineStation) {
        if (inputLineStation.isNotFirstLineStation()) {
            throw new LineStationCreateException("처음에 추가할 수 없는 구간 형태입니다.");
        }
    }

    public void addLineStation(LineStation inputLineStation) {
        LineStation preLineStation = stations.stream()
                .filter(lineStation -> lineStation.isPreStationOf(inputLineStation))
                .findFirst()
                .orElseThrow(() -> new LineStationCreateException("연결될 수 없는 역을 입력하셨습니다."));

        if (isLastStation(preLineStation)) {
            addLineStationOnLast(inputLineStation);
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

    public void addLineStationOnLast(LineStation lineStation) {
        stations.add(lineStation);
    }

    public void removeLineStationById(Long stationId) {
        if (stations.size() == ONE_STATION) {
            stations.remove(FIRST_INDEX);
            return;
        }

        removeFromMultiLineStations(stationId);
    }

    private void removeFromMultiLineStations(Long stationId) {
        LineStation targetLineStation = stations.stream()
                .filter(station -> station.is(stationId))
                .findFirst()
                .orElseThrow(NoLineExistException::new);

        if (isNotLastStation(targetLineStation)) {
            int index = stations.indexOf(targetLineStation);
            LineStation nextByTargetStation = stations.get(index + 1);
            nextByTargetStation.updatePreStationId(targetLineStation.getPreStationId());
        }

        stations.remove(targetLineStation);
    }

    private boolean isNotLastStation(LineStation lineStation) {
        return !isLastStation(lineStation);
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
