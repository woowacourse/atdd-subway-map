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
                .anyMatch(it -> it.getUpStation().equals(section.getUpStation())) &&
                sections.stream()
                        .anyMatch(it -> it.getDownStation().equals(section.getDownStation()))) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있어 등록이 불가능합니다.");
        }

        if (sections.stream()
                .anyMatch(it -> it.getUpStation().equals(section.getUpStation()))) {
            final Section section1 = sections.stream()
                    .filter(it -> it.getUpStation().equals(section.getUpStation()))
                    .findFirst()
                    .orElseThrow();
            if (section1.getDistance() <= section.getDistance()) {
                throw new IllegalArgumentException("기존 역 사이 길이보다 크거나 같으면 등록이 불가합니다.");
            }
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
            if (section1.getDistance() <= section.getDistance()) {
                throw new IllegalArgumentException("기존 역 사이 길이보다 크거나 같으면 등록이 불가합니다.");
            }
            sections.remove(section1);
            sections.add(new Section(section1.getUpStation(), section.getUpStation(), section1.getDistance() - section
                    .getDistance()));
            sections.add(section);
            return;
        }

        if (sections.stream()
                .anyMatch(it -> it.getUpStation().equals(section.getDownStation()))) {
            final Section section1 = sections.stream()
                    .filter(it -> it.getUpStation().equals(section.getDownStation()))
                    .findFirst()
                    .orElseThrow();
            sections.remove(section1);
            sections.add(section);
            sections.add(section1);
            return;
        }

        if (sections.stream()
                .anyMatch(it -> it.getDownStation().equals(section.getUpStation()))) {
            sections.add(section);
            return;
        }

        throw new IllegalArgumentException("상행역과 하행역 둘 다 노선에 포함되어 있지 않아 등록이 불가합니다.");
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
