package wooteco.subway.section;

import wooteco.subway.station.Station;

import java.util.*;

public class Sections {
    private final Set<Section> sections;

    public Sections(Set<Section> sections) {
        this.sections = new HashSet<>(sections);
    }

    public List<Station> path() {
        List<Station> stations = new ArrayList<>();
        Optional<Section> now = firstSection();
        while(now.isPresent()) {
            stations.add(now.get().upStation());
            now = next(now.get());
        }
        stations.add(lastSection().get().downStation());
        return stations;
    }

    private Optional<Section> next(Section now) {
        return sections.stream()
                .filter(next -> next.upStation().equals(now.downStation()))
                .findFirst();
    }

    private Optional<Section> lastSection() {
        return sections.stream()
                .filter(section1 -> !sections.stream()
                        .filter(section2 -> section1.downStation().equals(section2.upStation()))
                        .findFirst().isPresent()
                ).findFirst();
    }

    private Optional<Section> firstSection() {
        return sections.stream()
                .filter(section1 -> !sections.stream()
                        .filter(section2 -> section1.upStation().equals(section2.downStation()))
                        .findFirst().isPresent()
                ).findFirst();
    }
}
