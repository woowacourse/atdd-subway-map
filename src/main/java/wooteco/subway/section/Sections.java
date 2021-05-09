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
        if (!singleCommonStation(section).isPresent()) {
            throw new IllegalArgumentException("상행역이나 하행역 둘 중 하나만 노선에 존재해야 됩니다.");
        }
        Station station = singleCommonStation(section).get();
        if ((section.downStation().equals(station) && sectionWithUpStation(station).equals(firstSection()))
                || (section.upStation().equals(station) && sectionWithDownStation(station).equals(lastSection()))) {
            sections.add(section);
            return;
        }
        if (section.downStation().equals(station)) {
            Section temp = sectionWithDownStation(station).get();
            if (section.distance().intValue() >= temp.distance().intValue()) {
                throw new IllegalArgumentException("추가할 구간 길이가 기존의 구간 길이보다 작아야 합니다.");
            }
            Section insert = new Section(temp.upStation(), section.upStation(), Distance.of(temp.distance().intValue() - section.distance().intValue()));
            sections.add(insert);
            sections.remove(temp);
        }
        if (section.upStation().equals(station)) {
            Section temp = sectionWithUpStation(station).get();
            if (section.distance().intValue() >= temp.distance().intValue()) {
                throw new IllegalArgumentException("추가할 구간 길이가 기존의 구간 길이보다 작아야 합니다.");
            }
            Section insert = new Section(section.downStation(), temp.downStation(), Distance.of(temp.distance().intValue() - section.distance().intValue()));
            sections.add(insert);
            sections.remove(temp);
        }
        sections.add(section);
    }

    private Optional<Station> singleCommonStation(Section section) {
        if (path().contains(section.upStation()) && !path().contains(section.downStation())) {
            return Optional.of(section.upStation());
        }
        if (!path().contains(section.upStation()) && path().contains(section.downStation())) {
            return Optional.of(section.downStation());
        }
        return Optional.empty();
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

    public void deleteStation(Station station) {
        if (sections.size() < 2) {
            throw new IllegalArgumentException("구간이 하나일 때 지울수 업습니다.");
        }
        if (!singleCommonStation(station).isPresent()) {
            throw new IllegalArgumentException("노선에 삭제할 역이 없습니다.");
        }
        if (sectionWithUpStation(station).equals(firstSection())) {
            sections.remove(firstSection().get());
            return;
        }
        if (sectionWithDownStation(station).equals(lastSection())) {
            sections.remove(lastSection().get());
            return;
        }
        Section leftSection = sectionWithDownStation(station).get();
        Section rightSection = sectionWithUpStation(station).get();
        Section insert = new Section(leftSection.upStation(), rightSection.downStation(), Distance.of(leftSection.distance().intValue() + rightSection.distance().intValue()));
        sections.add(insert);
        sections.remove(leftSection);
        sections.remove(rightSection);
    }

    private Optional<Station> singleCommonStation(Station station) {
        if (path().contains(station)) {
            return Optional.of(station);
        }
        return Optional.empty();
    }
}
