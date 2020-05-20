package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Table("LINE")
public class Line {
    private static final int NO_SIZE = 0;
    private static final int FIRST_INDEX = 0;
    private static final int NEXT_INDEX = 1;
    private static final int MINIMUM_SIZE_VALUE = 1;

    @Id
    private Long id;
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    @MappedCollection(idColumn = "line", keyColumn = "sequence")
    private List<LineStation> lineStations = new LinkedList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String bgColor;

    public Line() {
    }

    public Line(Long id, String title, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.bgColor = bgColor;
    }

    public Line(String title, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
        this(null, title, startTime, endTime, intervalTime, bgColor);
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

    public List<LineStation> getLineStations() {
        return lineStations;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getBgColor() {
        return bgColor;
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
        if (line.getBgColor() != null) {
            this.bgColor = line.getBgColor();
        }

        this.updatedAt = LocalDateTime.now();
    }

    public void addLineStation(LineStation requestLineStation) {
        int index = findIndex(requestLineStation);

        if(isAddFirstIndex(index)) {
            LineStation nextLineStation = lineStations.get(index);
            nextLineStation.updatePreLineStationId(requestLineStation.getStationId());
            lineStations.add(index, requestLineStation);
            return;
        }
        if (isAddMiddleIndex(index)) {
            LineStation nextLineStation = lineStations.get(index);
            addLineStationInMiddleIndex(requestLineStation, index, nextLineStation);
            return;
        }
        lineStations.add(index, requestLineStation);
    }

    private Integer findIndex(LineStation requestLineStation) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.isPreStationOf(requestLineStation))
                .map(lineStation -> lineStations.indexOf(lineStation) + 1)
                .findAny()
                .orElse(0);
    }

    private boolean isAddMiddleIndex(int index) {
        return lineStations.size() > MINIMUM_SIZE_VALUE && lineStations.size() > index;
    }

    private boolean isAddFirstIndex(int index) {
        return lineStations.size() > MINIMUM_SIZE_VALUE && lineStations.size() > index && index == NO_SIZE;
    }

    private void addLineStationInMiddleIndex(LineStation requestLineStation, int index, LineStation nextLineStation) {
        LineStation preLineStation = lineStations.get(index - 1);
        requestLineStation.updatePreLineStationId(preLineStation.getStationId());
        nextLineStation.updatePreLineStationId(requestLineStation.getStationId());
        lineStations.add(index, requestLineStation);
    }

    public void removeLineStationById(Long stationId) {
        int removeIndex = findLineStationIndex(stationId);
        Long preLineStationId = getPreLineStationId(removeIndex);

        updateLineStationId(removeIndex + NEXT_INDEX, preLineStationId);
        lineStations.remove(removeIndex);
    }

    private void updateLineStationId(int nextIndex, Long preLineStationId) {
        if(lineStations.size() <= nextIndex) {
            return;
        }

        LineStation lineStation = lineStations.get(nextIndex);
        lineStation.updatePreLineStationId(preLineStationId);
    }

    private Long getPreLineStationId(int removeIndex) {
        if(removeIndex == FIRST_INDEX) {
            return null;
        }

        return lineStations.get(removeIndex-1).getPreStationId();
    }

    private int findLineStationIndex(Long stationId) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.isSameStationId(stationId))
                .map(lineStation -> lineStations.indexOf(lineStation))
                .findAny()
                .orElseThrow(NoSuchElementException::new);
    }

    public List<Long> getLineStationsId() {
        return this.lineStations.stream()
                .mapToLong(LineStation::getStationId)
                .boxed()
                .collect(Collectors.toList());
    }
}
