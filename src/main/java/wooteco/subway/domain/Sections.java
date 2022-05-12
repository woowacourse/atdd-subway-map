package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Sections {

    private final LinkedList<Section> sections;

    public Sections(LinkedList<Section> sections) {
        this.sections = sections;
    }

    public Sections(List<Section> sections) {
        this.sections = new LinkedList<>(sections);
    }

    public Sections(Section section) {
        this.sections = new LinkedList<>();
        sections.add(section);
    }

    public static Sections from(List<Section> mixedSections) {
        LinkedList<Section> sections = new LinkedList<>(List.of(mixedSections.remove(0)));
        while (!mixedSections.isEmpty()) {
            for (int i = 0; i < mixedSections.size(); i++) {
                Section section = mixedSections.get(i);
                if (sections.getFirst().canUpExtendBy(section)) {
                    sections.addFirst(mixedSections.remove(i));
                    break;
                }
                if (sections.getLast().canDownExtendBy(section)) {
                    sections.addLast(mixedSections.remove(i));
                    break;
                }
            }
        }
        return new Sections(sections);
    }

    public Section findTop() {
        return sections.getFirst();
    }

    public Section findBottom() {
        return sections.getLast();
    }

    public Station findTopStation() {
        return findTop().getUp();
    }

    public Station findBottomStation() {
        return findBottom().getDown();
    }

    public void add(Section section) {
        if (section.isAlreadyIn(getStations())) {
            throw new IllegalArgumentException("이미 포함 된 구간입니다.");
        }
        if (canExtendBy(section)) {
            extendSections(section);
            return;
        }
        if (canAnyDivideBy(section)) {
            addSectionInside(section);
            return;
        }
        throw new IllegalArgumentException("겹치는 역이 없이 추가할 수 없습니다.");
    }

    private void addSectionInside(Section section) {
        for (int i = 0; i < sections.size(); i++) {
            Section origin = sections.get(i);
            if (origin.isSameUpStation(section) || origin.isSameDownStation(section)) {
                sections.remove(i);
                sections.addAll(i, origin.divideBy(section));
                return;
            }
        }
    }

    private void extendSections(Section section) {
        if (findTop().canUpExtendBy(section)) {
            sections.addFirst(section);
            return;
        }
        if (findBottom().canDownExtendBy(section)) {
            sections.addLast(section);
            return;
        }
    }

    public List<Section> findDifferentSections(Sections other) {
        LinkedList<Section> result = new LinkedList<>(this.sections);
        result.removeAll(other.sections);
        return result;
    }

    public List<Section> getSections() {
        return new LinkedList<>(sections);
    }

    public List<Station> getStations() {
        List<Station> stations = sections.stream()
                .map(Section::getUp).collect(Collectors.toList());
        stations.add(sections.getLast().getDown());
        return stations;
    }

    private boolean canExtendBy(Section target) {
        return findTop().canUpExtendBy(target) || findBottom().canDownExtendBy(target);
    }

    private boolean canAnyDivideBy(Section target) {
        for (Section section : sections) {
            if (section.isSameUpStation(target) || section.isSameDownStation(target)) {
                return true;
            }
        }
        return false;
    }

    public void delete(Station station) {
        validateDeletable();
        if (station.equals(findTopStation())) {
            sections.removeFirst();
            return;
        }
        if (station.equals(findBottomStation())) {
            sections.removeLast();
            return;
        }

        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            if (section.getDown().equals(station)) {
                Section combined = section.combine(sections.get(i + 1));
                sections.remove(i);
                sections.remove(i);
                sections.add(i, combined);
                return;
            }
        }
    }

    private void validateDeletable() {
        if (sections.size() < 2) {
            throw new IllegalArgumentException("구간이 한 개 일 때는 역을 삭제할 수 없습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
