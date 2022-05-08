package wooteco.subway.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Sections {

    private final LinkedList<Section> sections;

    public Sections(LinkedList<Section> sections) {
        this.sections = sections;
    }

    public Sections(Section section) {
        this.sections = new LinkedList<>();
        sections.add(section);
    }

    public static Sections from(Section first, List<Section> mixedSections) {
        Sections sections = new Sections(first);
        while (sections.getSections().size() != mixedSections.size()) {
            for (int i = 0; i < mixedSections.size(); i ++)
                if (sections.findBottom().canDownExtendBy(mixedSections.get(i))) {
                    sections.add(mixedSections.get(i));
                    break;
                }
            }
        return sections;
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

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public Set<Station> getStations() {
        Set<Station> stations = new HashSet<>();
        for (Section section : sections) {
            stations.add(section.getUp());
            stations.add(section.getDown());
        }
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
}
