package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Table("LINE")
public class Line {
    private static final int ONE_SIZE = 1;
    private static final int FIRST_INDEX = 0;
    private static final int SECOND_INDEX = 1;
    private static final int NEXT_INDEX = 1;
    private static final int BEFORE_INDEX = 1;
    @Id
    private Long id;
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    @MappedCollection(idColumn = "line", keyColumn = "sequence")
    private List<LineStation> lineStations = new ArrayList<>();
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
        int index = lineStations.stream()
                .filter(lineStation -> lineStation.isPreStationOf(requestLineStation))
                .map(lineStation -> lineStations.indexOf(lineStation) + 1)
                .findAny()
                .orElse(0);
        if (isAlreadyInputStations(requestLineStation, index)) return;
        lineStations.add(index, requestLineStation);
    }

    private boolean isAlreadyInputStations(LineStation requestLineStation, int index) {
        if(lineStations.size() > ONE_SIZE && lineStations.size() > index) {
            LineStation nextLineStation = lineStations.get(index);
            if (isInputFirstIndex(requestLineStation, index, nextLineStation)) return true;

            LineStation preLineStation = lineStations.get(index - 1);
            requestLineStation.updatePreLineStationId(preLineStation.getStationId());
            nextLineStation.updatePreLineStationId(requestLineStation.getStationId());
            lineStations.add(index, requestLineStation);
            return true;
        }
        return false;
    }

    private boolean isInputFirstIndex(LineStation requestLineStation, int index, LineStation nextLineStation) {
        if(index == 0) {
            nextLineStation.updatePreLineStationId(requestLineStation.getStationId());
            lineStations.add(index, requestLineStation);
            return true;
        }
        return false;
    }

    public void removeLineStationById(Long stationId) {
        int index = lineStations.stream()
                .filter(lineStation -> lineStation.isSameStationId(stationId))
                .map(lineStation -> lineStations.indexOf(lineStation))
                .findAny()
                .orElseThrow(NoSuchElementException::new);
        LineStation preLineStation;
        LineStation nextLineStation;

        if (isRemoveStationUnNormalCase(index)) return;
        nextLineStation = lineStations.get(index+ NEXT_INDEX);
        preLineStation = lineStations.get(index- BEFORE_INDEX);
        nextLineStation.updatePreLineStationId(preLineStation.getStationId());
        lineStations.remove(index);
    }

    private boolean isRemoveStationUnNormalCase(int index) {
        LineStation nextLineStation;
        if(index == FIRST_INDEX && index == lineStations.size() - 1){
            lineStations.remove(index);
            return true;
        }
        if(index == FIRST_INDEX) {
            nextLineStation = lineStations.get(SECOND_INDEX);
            nextLineStation.updatePreLineStationId(null);
            lineStations.remove(index);
            return true;
        }
        if(index == lineStations.size() -1)  {
            lineStations.remove(index);
            return true;
        }
        return false;
    }

    public List<Long> getLineStationsId() {
        return this.lineStations.stream()
                .mapToLong(LineStation::getStationId)
                .boxed()
                .collect(Collectors.toList());
    }

}
