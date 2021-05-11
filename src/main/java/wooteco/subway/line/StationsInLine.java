package wooteco.subway.line;

import java.util.Arrays;
import java.util.List;

import wooteco.subway.exception.BothStationInLineException;
import wooteco.subway.exception.BothStationNotInLineException;
import wooteco.subway.station.Station;

public class StationsInLine {
    private final List<Station> stations;

    public StationsInLine(List<Station> stations) {
        this.stations = stations;
    }


    public void validStations(Station upStation, Station downStation) {
        if (stations.containsAll(Arrays.asList(upStation, downStation))) {
            throw new BothStationInLineException();
        }

        if(!stations.contains(upStation) && !stations.contains(downStation)) {
            throw new BothStationNotInLineException();
        }

    }

    public boolean isEndStations(Station upStation, Station downStation) {
        return stations.get(0).equals(downStation) || stations.get(stations.size() - 1).equals(upStation);
    }

    public boolean contains(Station station) {
        return stations.contains(station);
    }

    public List<Station> getStations() {
        return stations;
    }
}
