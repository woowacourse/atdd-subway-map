package wooteco.subway.domain.section;

import java.util.LinkedList;
import java.util.List;
import wooteco.subway.domain.station.Station;

// TODO: Sections 구현 후 대체
public class Stations {

    private final List<Station> value;

    private Stations(List<Station> value) {
        this.value = value;
    }

    public static Stations of(List<Section> sections) {
        return new Stations(toSortedStationList(SectionStationMap.of(sections)));
    }

    private static List<Station> toSortedStationList(SectionStationMap sectionMap) {
        LinkedList<Station> list = new LinkedList<>();
        Station upperEndStation = sectionMap.findUpperEndStation();
        list.add(upperEndStation);

        Long current = upperEndStation.getId();
        while (sectionMap.hasDownStation(current)) {
            Station nextStation = sectionMap.getDownStationIdOf(current);
            list.add(nextStation);
            current = nextStation.getId();
        }
        return list;
    }

    public List<Station> getValue() {
        return value;
    }
}
