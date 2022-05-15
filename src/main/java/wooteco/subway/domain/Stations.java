package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Stations {

    private final List<Station> values;

    public Stations(List<Station> values) {
        this.values = values;
    }

    public List<Station> sortByOrder(List<Long> order) {
        List<Station> sorted = new ArrayList<>();
        for(Long id : order) {
            sorted.add(find(id));
        }
        return sorted;
    }

    private Station find(Long id) {
        return values.stream()
                .filter(station -> station.hasId(id))
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }
}
