package wooteco.subway.section;

import wooteco.subway.station.Station;

import java.util.*;

public class Sections {
    private final Set<Section> sections;

    public Sections(Set<Section> sections) {
        this.sections = new HashSet<>(sections);
    }

    public Sections(Section section) {
        sections = new HashSet<>(Arrays.asList(section));
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

    private Optional<Section> before(Section now) {
        return sections.stream()
                .filter(before -> before.downStation().equals(now.upStation()))
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

    public void addSection(Section section) {
        Optional<Section> leftSection = sectionWithDownStation(section.upStation());
        Optional<Section> rightSection = sectionWithUpStation(section.downStation());
        if (!(leftSection.isPresent() ^ rightSection.isPresent())) {
            throw new IllegalArgumentException("상행역이나 하행역 둘 중 하나만 노선에 존재해야 됩니다.");
        }
        if (leftSection.isPresent()) {
            if (next(leftSection.get()).isPresent()) {
                Section temp = next(leftSection.get()).get();
                if (section.distance().intValue() >= temp.distance().intValue()) {
                    throw new IllegalArgumentException("추가할 구간 길이가 기존의 구간 길이보다 작아야 합니다.");
                }
                Section insert = new Section(section.downStation(), temp.downStation(), Distance.of(temp.distance().intValue() - section.distance().intValue()));
                sections.add(insert);
                sections.remove(temp);
            }
        }
        if (rightSection.isPresent()) {
            if (before(rightSection.get()).isPresent()) {
                Section temp = before(rightSection.get()).get();
                if (section.distance().intValue() >= temp.distance().intValue()) {
                    throw new IllegalArgumentException("추가할 구간 길이가 기존의 구간 길이보다 작아야 합니다.");
                }
                Section insert = new Section(temp.upStation(), section.upStation(), Distance.of(temp.distance().intValue() - section.distance().intValue()));
                sections.add(insert);
                sections.remove(temp);
            }
        }
        sections.add(section);
    }

    private Optional<Section> sectionWithDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.downStation().equals(station))
                .findFirst();
    }

    private Optional<Section> sectionWithUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.upStation().equals(station))
                .findFirst();
    }

    public List<Section> values() {
        return new ArrayList<>(sections);
    }
}
