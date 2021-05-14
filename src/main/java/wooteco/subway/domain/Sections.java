package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Sections {
    List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public Sections(Station upStation, Station downStation, int distance) {
        this.sections = new ArrayList<>();
        sections.add(new Section(upStation, downStation, distance));
    }

    public void add(Station upStation, Station downStation, int distance) {
        boolean hasUpStation = contains(upStation);
        boolean hasDownStation = contains(downStation);

        validateRegisteredStations(hasUpStation, hasDownStation);

        if (hasUpStation) {
            if (isUp(upStation)) {
                Section sectionToUpdate = getSectionWhereUpStationIs(upStation);
                addMiddle(sectionToUpdate, downStation, distance);
                return;
            }
            addAt(new Section(upStation, downStation, distance), sections.size());
            return;
        }

        if (hasDownStation) {
            if (isDown(downStation)) {
                Section sectionToUpdate = getSectionWhereDownStationIs(downStation);
                addMiddle(sectionToUpdate, upStation, sectionToUpdate.getDistance() - distance);
                return;
            }
            addAt(new Section(upStation, downStation, distance), 0);
        }
    }

    private boolean contains(Station station) {
        return sections.stream()
                .anyMatch(section -> section.hasAny(station));
    }

    private Section getSectionWhereUpStationIs(Station upStation) {
        return sections.stream()
                .filter(section -> section.isUpStation(upStation))
                .findAny()
                .get();
    }

    private Section getSectionWhereDownStationIs(Station downStation) {
        return sections.stream()
                .filter(section -> section.isDownStation(downStation))
                .findAny()
                .get();
    }

    private void addMiddle(Section sectionToUpdate, Station station, int distance) {
        int index = sections.indexOf(sectionToUpdate);
        validateDistance(distance, sectionToUpdate);
        Section sectionUpside = new Section(sectionToUpdate.getUpStation(), station, distance);
        Section sectionDownside = new Section(station, sectionToUpdate.getDownStation(), sectionToUpdate.getDistance() - distance);

        sections.remove(index);

        sections.add(index, sectionDownside);
        sections.add(index, sectionUpside);
    }

    private void addAt(Section section, int index) {
        sections.add(index, section);
    }

    private void validateDistance(int distance, Section sectionToDelete) {
        if (distance > sectionToDelete.getDistance()) {
            throw new IllegalArgumentException("기존 구간의 길이를 넘어서는 구간을 추가할 수 없습니다.");
        }
    }

    private void validateRegisteredStations(boolean hasUpStation, boolean hasDownStation) {
        if (hasUpStation && hasDownStation) {
            throw new IllegalArgumentException("두 역이 모두 해당 노선에 등록되어 있습니다.");
        }
        if (!hasUpStation && !hasDownStation) {
            throw new IllegalArgumentException("두 역이 모두 노선에 등록되어 있지 않습니다.");
        }
    }

    public void delete(Station station) {
        boolean hasUpStation = isUp(station);
        boolean hasDownStation = isDown(station);

        if (!hasUpStation && !hasDownStation) {
            throw new IllegalArgumentException("등록되지 않은 역입니다.");
        }

        if (hasUpStation && hasDownStation) {
            removeStationFromMiddleOfSection(station);
            return;
        }

        if (hasUpStation) {
            sections.remove(0);
            return;
        }

        if (hasDownStation) {
            sections.remove(sections.size() - 1);
        }
    }

    private boolean isUp(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isUpStation(station));
    }

    private boolean isDown(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isDownStation(station));
    }

    private void removeStationFromMiddleOfSection(Station station) {
        Section sectionToUpdateLeftSide = sections.stream()
                .filter(section -> section.isDownStation(station))
                .findAny()
                .get();

        Section sectionToUpdateRightSide = sections.stream()
                .filter(section -> section.isUpStation(station))
                .findAny()
                .get();

        Section sectionMerged = mergeSection(sectionToUpdateLeftSide, sectionToUpdateRightSide);
        int index = sections.indexOf(sectionToUpdateLeftSide);

        sections.remove(sectionToUpdateLeftSide);
        sections.remove(sectionToUpdateRightSide);

        sections.add(index, sectionMerged);
    }

    private Section mergeSection(Section left, Section right) {
        return new Section(left.getUpStation(), right.getDownStation(), left.getDistance() + right.getDistance());
    }

    public List<Section> sections() {
        return sections;
    }

    public boolean isRemovable() {
        return sections.size() > 1;
    }

    public List<Station> getStations() {
        List<Station> stations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        stations.add(sections.get(sections.size() - 1).getDownStation());
        return stations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
