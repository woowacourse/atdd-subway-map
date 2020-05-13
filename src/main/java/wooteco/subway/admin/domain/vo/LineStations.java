package wooteco.subway.admin.domain.vo;

import java.util.LinkedList;

import org.springframework.data.relational.core.mapping.MappedCollection;

import wooteco.subway.admin.domain.LineStation;

public class LineStations {

    @MappedCollection(idColumn = "line", keyColumn = "index")
    private LinkedList<LineStation> stations;

    public LineStations(LinkedList<LineStation> stations) {
        this.stations = stations;
    }

    public static LineStations empty() {
        return new LineStations(new LinkedList<>());
    }

    public LinkedList<LineStation> getStations() {
        return stations;
    }

    public void add(int insertIndex, LineStation lineStation) {
        stations.add(insertIndex, lineStation);
    }

    public LineStation get(int index) {
        return stations.get(index);
    }

    public void remove(int index) {
        stations.remove(index);
    }

    public boolean isEmpty() {
        return stations.isEmpty();
    }

    public int size() {
        return stations.size();
    }

    public LineStation getLast() {
        return stations.getLast();
    }

    public int indexOf(LineStation preStation) {
        return stations.indexOf(preStation);
    }
}
