package wooteco.subway.domain;

import java.util.List;
import java.util.stream.Collectors;

public class Sections {
    private final Line line;
    private final List<Section> values;

    public Sections(Line line, List<Section> values) {
        this.line = line;
        this.values = values;
    }

    public List<Station> getStations() {
        List<Station> stations = values.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        Section lastSection = values.get(values.size() - 1);
        stations.add(lastSection.getDownStation());
        return stations;
    }
}
