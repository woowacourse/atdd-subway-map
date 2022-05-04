package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Stations {
    private final List<Station> stations = new ArrayList<>();

    public void add(Station station) {
        validateDuplicateName(station);
        stations.add(station);
    }

    private void validateDuplicateName(Station station) {
        if (stations.contains(station)) {
            throw new IllegalArgumentException("중복된 이름이 존재합니다.");
        }
    }
}
