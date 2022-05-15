package wooteco.subway.domain.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.NotFoundException;

public class SectionsFactory {

    private static final String NOT_EXISTING_LINE_EXCEPTION = "존재하지 않는 노선입니다.";

    private SectionsFactory() {
    }

    public static Sections generate(List<Section> sections) {
        validateLineExistence(sections);
        Station upperEndStation = toUpperEndStation(sections);
        Map<Station, Section> sectionMap = toSectionMap(sections);
        return new Sections(toSortedSectionList(upperEndStation, sectionMap));
    }

    private static void validateLineExistence(List<Section> value) {
        if (value.isEmpty()) {
            throw new NotFoundException(NOT_EXISTING_LINE_EXCEPTION);
        }
    }

    private static Station toUpperEndStation(List<Section> sections) {
        Set<Station> downStations = toDownStations(sections);
        return sections.stream()
                .map(Section::getUpStation)
                .filter(section -> !downStations.contains(section))
                .findFirst()
                .get();
    }

    private static Set<Station> toDownStations(List<Section> sections) {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toSet());
    }

    private static Map<Station, Section> toSectionMap(List<Section> sections) {
        Map<Station, Section> sectionMap = new HashMap<>();
        for (Section section : sections) {
            Station upStation = section.getUpStation();
            sectionMap.put(upStation, section);
        }
        return sectionMap;
    }

    private static List<Section> toSortedSectionList(Station initialStation,
                                                     Map<Station, Section> sectionMap) {
        List<Section> list = new ArrayList<>();
        Station current = initialStation;
        while (sectionMap.containsKey(current)) {
            Section section = sectionMap.get(current);
            list.add(section);
            current = section.getDownStation();
        }
        return list;
    }
}
