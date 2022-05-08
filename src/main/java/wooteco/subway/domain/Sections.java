package wooteco.subway.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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

    public Section findTop() {
        return sections.getFirst();
    }

    public Section findBottom() {
        return sections.getLast();
    }

    public void add(Section section) {
        Relation relation = calculateRelation(section);
        if (relation.equals(Relation.NONE) || relation.equals(Relation.INCLUDE)) {
            throw new IllegalArgumentException("해당 노선은 추가할 수 없습니다.");
        }
        if (relation.equals(Relation.EXTEND)) {
            if (findTop().canUpExtendBy(section)) {
                sections.addFirst(section);
                return;
            }
            if (findBottom().canDownExtendBy(section)) {
                sections.addLast(section);
                return;
            }
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
        if (findTop().canUpExtendBy(target) || findBottom().canDownExtendBy(target)) {
            return Relation.EXTEND;
        }
        for (Section section : sections) {
            if (section.isSameUpStation(target) || section.isSameDownStation(target)) {
                return Relation.DIVIDE;
            }
        }
        return Relation.NONE;
    }
}
