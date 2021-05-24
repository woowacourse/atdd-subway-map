package wooteco.subway.domain.section;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import wooteco.subway.domain.station.Station;

public class Sections {

    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        return sections.stream()
            .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
            .collect(Collectors.toList());
    }
}
