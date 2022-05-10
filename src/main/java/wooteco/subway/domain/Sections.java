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

    public static Sections from(ArrayList<Section> mixedSections) {
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
        Relation relation = calculateRelation(section);
        validateSectionAddable(relation);

        if (relation.equals(Relation.EXTEND)) {
            extendSections(section);
            return;
        }

        for (int i = 0; i < sections.size(); i++) {
            Section origin = sections.get(i);
            if (origin.isSameUpStation(section)) {
                sections.remove(i);
                sections.addAll(i, List.of(section, origin.divideBy(section)));
                return;
            }
            if (origin.isSameDownStation(section)) {
                sections.remove(i);
                sections.addAll(i, List.of(origin.divideBy(section), section));
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

    private void validateSectionAddable(Relation relation) {
        if (relation.equals(Relation.NONE) || relation.equals(Relation.INCLUDE)) {
            throw new IllegalArgumentException("해당 노선은 추가할 수 없습니다.");
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

    public Relation calculateRelation(Section target) {
        if (target.isAlreadyIn(getStations())) {
            return Relation.INCLUDE;
        }
        if (canExtendBy(target)) {
            return Relation.EXTEND;
        }
        if (canAnyDivideBy(target)) {
            return Relation.DIVIDE;
        }
        return Relation.NONE;
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
