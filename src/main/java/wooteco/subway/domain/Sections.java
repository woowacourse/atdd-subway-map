package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {
    private List<Section> sections;

    public Sections(Station upStation, Station downStation, int distance) {
        final Section section = new Section(upStation, downStation, distance);
        this.sections = new ArrayList<>() {
            {
                add(section);
            }
        };
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section section) {
        if (sections.stream()
                .anyMatch(it -> it.getUpStation().equals(section.getUpStation()))) {
            final Section section1 = sections.stream()
                    .filter(it -> it.getUpStation().equals(section.getUpStation()))
                    .findFirst()
                    .orElseThrow();
            sections.remove(section1);
            sections.add(section);
            sections.add(new Section(section.getDownStation(), section1.getDownStation(),
                    section1.getDistance() - section.getDistance()));
            return;
        }
        if (sections.stream()
                .anyMatch(it -> it.getDownStation().equals(section.getDownStation()))) {
            final Section section1 = sections.stream()
                    .filter(it -> it.getDownStation().equals(section.getDownStation()))
                    .findFirst()
                    .orElseThrow();
            sections.remove(section1);
            sections.add(new Section(section1.getUpStation(), section.getUpStation(), section1.getDistance() - section
                    .getDistance()));
            sections.add(section);
            return;
        }
//        sections.add(section);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return stations.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return sections;
    }


}
