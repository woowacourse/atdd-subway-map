package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Line {

    private final String name;
    private final String color;
    private final List<Section> sections;

    public Line(String name, String color, List<Section> sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public List<Station> getTrainsFromUpLine() {
        List<Station> stations = new ArrayList<>();

        List<Station> upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        List<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        Station endOftheUpLine = endOftheUpLine(upStations, downStations);
        stations.add(endOftheUpLine);
        addStation(stations, endOftheUpLine);
        return stations;
    }

    private void addStation(List<Station> stations, Station upStation) {
        Optional<Section> section = sections.stream()
                .filter(s -> s.isUpStation(upStation))
                .findAny();

        if (!section.isPresent()) {
            return;
        }

        Station downStation = section.get().getDownStation();
        stations.add(downStation);
        addStation(stations, downStation);
    }

    private Station endOftheUpLine(List<Station> upStations, List<Station> downStations) {
        return upStations.stream()
                .filter(station -> !downStations.contains(station))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 노선입니다."));
    }
}
