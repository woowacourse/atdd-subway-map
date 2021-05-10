package wooteco.subway.station;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Sections {
    List<Section> sections;

    public Sections(Station upStation, Station downStation, int distance) {
        this.sections = new ArrayList<>();
        sections.add(new Section(upStation, downStation, distance));
    }

    public void add(Station upStation, Station downStation, int distance) {
        boolean hasUpStation = this.sections.stream()
                .anyMatch(section -> section.hasAny(upStation));
        boolean hasDownStation = this.sections.stream()
                .anyMatch(section -> section.hasAny(downStation));

        if (hasUpStation && hasDownStation) {
            throw new IllegalArgumentException("이미 노선에 등록된 역들입니다.");
        }

        if (hasUpStation) {
            addDownStation(upStation, downStation, distance);
            return;
        }
        if (hasDownStation) {
            addUpStation(upStation, downStation, distance);
            return;
        }

        throw new IllegalArgumentException("노선에 등록되지 않은 역들입니다.");
    }

    private void addUpStation(Station upStation, Station downStation, int distance) {
        Section sectionToDelete = this.sections.stream()
                .filter(section -> section.isDownStation(downStation))
                .findAny()
                .get();
        int index = sections.indexOf(sectionToDelete);

        validateDistance(distance, sectionToDelete);

        Section section1 = new Section(sectionToDelete.getUpStation(), upStation, sectionToDelete.getDistance() - distance);
        Section section2 = new Section(upStation, downStation, distance);

        sections.remove(index);

        sections.add(index, section2);
        sections.add(index, section1);
    }

    private void addDownStation(Station upStation, Station downStation, int distance) {
        Section sectionToDelete = this.sections.stream()
                .filter(section -> section.isUpStation(upStation))
                .findAny()
                .get();
        int index = sections.indexOf(sectionToDelete);

        validateDistance(distance, sectionToDelete);

        Section section1 = new Section(upStation, downStation, distance);
        Section section2 = new Section(downStation,
                sectionToDelete.getDownStation(),
                sectionToDelete.getDistance() - distance);

        sections.remove(index);

        sections.add(index, section2);
        sections.add(index, section1);
    }

    private void validateDistance(int distance, Section sectionToDelete) {
        if (distance > sectionToDelete.getDistance()) {
            throw new IllegalArgumentException("기존 구간의 길이를 넘어서는 구간을 추가할 수 없습니다.");
        }
    }

    public void delete(Station station) {
        boolean hasUpStation = this.sections.stream()
                .anyMatch(section -> section.isUpStation(station));
        boolean hasDownStation = this.sections.stream()
                .anyMatch(section -> section.isDownStation(station));

        if (!hasUpStation && !hasDownStation) {
            throw new IllegalArgumentException("등록되지 않은 역입니다.");
        }

        if (hasUpStation && hasDownStation) {
            Section sectionToDelete1 = this.sections.stream()
                    .filter(section -> section.isDownStation(station))
                    .findAny()
                    .get();

            Section sectionToDelete2 = this.sections.stream()
                    .filter(section -> section.isUpStation(station))
                    .findAny()
                    .get();

            Section section = new Section(sectionToDelete1.getUpStation(),
                    sectionToDelete2.getDownStation(),
                    sectionToDelete1.getDistance() + sectionToDelete2.getDistance());

            int index = sections.indexOf(sectionToDelete1);

            sections.remove(index);
            sections.remove(index);
            sections.add(index, section);
            return;
        }

        if (hasUpStation) {
            sections.remove(0);
            return;
        }

        if (hasDownStation) {
            sections.remove(sections.size() - 1);
            return;
        }

    }

    // todo : remove
    public Stream<Section> stream() {
        return this.sections.stream();
    }
}
