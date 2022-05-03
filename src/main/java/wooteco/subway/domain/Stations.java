package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import wooteco.subway.exception.NoStationFoundException;
import wooteco.subway.exception.StationDuplicateException;

public class Stations {

    private final List<Station> value;

    public Stations() {
        value = new ArrayList<>();
    }

    public void add(Station station) {
        validateDuplicate(station);
        value.add(station);
    }

    private void validateDuplicate(Station station) {
        if (value.contains(station)) {
            throw new StationDuplicateException();
        }
    }

    public List<Station> findAll() {
        return value;
    }

    public void deleteById(long stationId) {
        value.remove(findById(stationId));
    }

    private Station findById(long stationId) {
        return value.stream()
                .filter(station -> station.isSameId(stationId))
                .findFirst()
                .orElseThrow(NoStationFoundException::new);
    }

    private boolean hasStation(long stationId) {
        return value.stream().anyMatch(station -> station.getId() == stationId);
    }

}
