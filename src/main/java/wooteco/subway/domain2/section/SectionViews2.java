package wooteco.subway.domain2.section;

import java.util.LinkedList;
import java.util.List;
import wooteco.subway.domain2.station.Station;

public class SectionViews2 {

    private final List<Station> value;

    private SectionViews2(List<Station> value) {
        this.value = value;
    }

    public static SectionViews2 of(List<Section> sections) {
        return new SectionViews2(toSortedStationList(SectionStationMap2.of(sections)));
    }

    public static List<Station> toSortedStationList(SectionStationMap2 sectionMap) {
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
