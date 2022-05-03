package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
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
}
