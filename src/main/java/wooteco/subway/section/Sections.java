package wooteco.subway.section;

import wooteco.subway.station.Station;

import java.util.*;

public class Sections {
    private final Set<Section> sections;

    public Sections(Set<Section> sections) {
        this.sections = new HashSet<>(sections);
    }

    public Sections(Section section) {
        sections = new HashSet<>(Collections.singletonList(section));
    }

    public List<Station> path() {
        List<Station> stations = new ArrayList<>();
        for (Optional<Section> now = firstSection(); now.isPresent(); now = next(now.get())) {
            stations.add(now.get().upStation());
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
                .filter(section1 -> sections.stream()
                        .noneMatch(section2 -> section1.downStation().equals(section2.upStation()))
                ).findFirst();
    }

    private Optional<Section> firstSection() {
        return sections.stream()
                .filter(section1 -> sections.stream()
                        .noneMatch(section2 -> section1.upStation().equals(section2.downStation()))
                ).findFirst();
    }

    public void addSection(Section section) {
        Station station = singleCommonStation(section);
        if ((section.downStation().equals(station) && sectionWithUpStation(station).equals(firstSection()))
                || (section.upStation().equals(station) && sectionWithDownStation(station).equals(lastSection()))) {
            sections.add(section);
            return;
        }
        if (section.downStation().equals(station)) {
            divideSectionBasedOnDownStation(section, station);
        }
        if (section.upStation().equals(station)) {
            divideSectionBasedOnUpStation(section, station);
        }
        sections.add(section);
    }

    private void divideSectionBasedOnUpStation(Section section, Station station) {
        Section temp = sectionWithUpStation(station).get();
        validateDistance(section, temp);
        Section insert = new Section(section.downStation(), temp.downStation(),
                Distance.of(temp.distance().intValue() - section.distance().intValue())
        );
        sections.add(insert);
        sections.remove(temp);
    }

    private void divideSectionBasedOnDownStation(Section section, Station station) {
        Section temp = sectionWithDownStation(station).get();
        validateDistance(section, temp);
        Section insert = new Section(temp.upStation(), section.upStation(),
                Distance.of(temp.distance().intValue() - section.distance().intValue())
        );
        sections.add(insert);
        sections.remove(temp);
    }

    private void validateDistance(Section section, Section temp) {
        if (section.distance().intValue() >= temp.distance().intValue()) {
            throw new IllegalArgumentException("추가할 구간 길이가 기존의 구간 길이보다 작아야 합니다.");
        }
    }

    private Station singleCommonStation(Section section) {
        List<Station> path = path();
        if (path.contains(section.upStation()) && !path.contains(section.downStation())) {
            return section.upStation();
        }
        if (!path.contains(section.upStation()) && path.contains(section.downStation())) {
            return section.downStation();
        }

        throw new IllegalArgumentException("상행역이나 하행역 둘 중 하나만 노선에 존재해야 됩니다.");
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
        validateDelete(station);
        if (sectionWithUpStation(station).equals(firstSection())) {
            sections.remove(firstSection().get());
            return;
        }
        if (sectionWithDownStation(station).equals(lastSection())) {
            sections.remove(lastSection().get());
            return;
        }

        combineSection(station);
    }

    private void combineSection(Station station) {
        Section leftSection = sectionWithDownStation(station).get();
        Section rightSection = sectionWithUpStation(station).get();
        Section insert = new Section(leftSection.upStation(), rightSection.downStation(),
                Distance.of(leftSection.distance().intValue() + rightSection.distance().intValue())
        );
        sections.add(insert);
        sections.remove(leftSection);
        sections.remove(rightSection);
    }

    private void validateDelete(Station station) {
        if (sections.size() < 2) {
            throw new IllegalArgumentException("구간이 하나일 때 지울수 업습니다.");
        }
        if (!path().contains(station)) {
            throw new IllegalArgumentException("노선에 삭제할 역이 없습니다.");
        }
    }
}
