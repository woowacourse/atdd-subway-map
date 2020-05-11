package wooteco.subway.admin.domain;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.MappedCollection;
import wooteco.subway.admin.domain.vo.LineTimeTable;
import wooteco.subway.admin.domain.vo.Stations;

public class Line {

    @Id
    private Long id;
    private String name;
    @Embedded.Nullable
    private LineTimeTable lineTimeTable;
    @MappedCollection
    @Embedded.Empty
    private Stations stations;
    private String bgColor;

    public Line() {
    }

    public Line(Long id, String name, LineTimeTable lineTimeTable,
        Stations stations, String bgColor) {
        this.id = id;
        this.name = name;
        this.lineTimeTable = lineTimeTable;
        this.stations = stations;
        this.bgColor = bgColor;
    }

    public Line(Long id, String name,
        LocalTime startTime, LocalTime endTime, int intervalTime,
        String bgColor) {
        this(id, name,
            new LineTimeTable(startTime, endTime, intervalTime),
            new Stations(new LinkedList<>()), bgColor);
    }

    public Line(String name,
        LocalTime startTime, LocalTime endTime, int intervalTime,
        String bgColor) {
        this(null, name, startTime, endTime, intervalTime, bgColor);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LineTimeTable getLineTimeTable() {
        return lineTimeTable;
    }

    public Stations getStations() {
        return stations;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void update(Line line) {
        if (line.getName() != null) {
            this.name = line.getName();
        }
        if (line.getLineTimeTable() != null) {
            this.lineTimeTable = line.getLineTimeTable();
        }
        if (line.getBgColor() != null) {
            this.bgColor = line.getBgColor();
        }
    }

    public void addLineStation(LineStation lineStation) {
        stations.addLineStation(lineStation);
    }

    public void removeLineStationById(Long stationId) {
        stations.removeLineStationById(stationId);
    }

    public List<Long> getStationIds() {
        return stations.getStationIds();
    }
}
