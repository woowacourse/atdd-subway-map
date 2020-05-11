package wooteco.subway.admin.domain;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.MappedCollection;
import wooteco.subway.admin.domain.vo.BgColor;
import wooteco.subway.admin.domain.vo.LineTimeTable;
import wooteco.subway.admin.domain.vo.Name;
import wooteco.subway.admin.domain.vo.Stations;

public class Line {

    @Id
    private Long id;
    @Embedded.Nullable
    private Name name;
    @Embedded.Nullable
    private LineTimeTable lineTimeTable;
    @MappedCollection
    @Embedded.Empty
    private Stations stations;
    @Embedded.Nullable
    private BgColor bgColor;

    public Line() {
    }

    public Line(Long id, Name name, LineTimeTable lineTimeTable,
        Stations stations, BgColor bgColor) {
        this.id = id;
        this.name = name;
        this.lineTimeTable = lineTimeTable;
        this.stations = stations;
        this.bgColor = bgColor;
    }

    public Line(Long id, String name,
        LocalTime startTime, LocalTime endTime, int intervalTime,
        String bgColor) {
        this(id, new Name(name),
            new LineTimeTable(startTime, endTime, intervalTime),
            new Stations(new LinkedList<>()),
            new BgColor(bgColor));
    }

    public Line(String name,
        LocalTime startTime, LocalTime endTime, int intervalTime,
        String bgColor) {
        this(null, name, startTime, endTime, intervalTime, bgColor);
    }

    public Long getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public LineTimeTable getLineTimeTable() {
        return lineTimeTable;
    }

    public Stations getStations() {
        return stations;
    }

    public BgColor getBgColor() {
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
