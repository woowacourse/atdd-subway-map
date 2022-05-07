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

    public Station findUpperTerminal() {
        return sections.getFirst().getUp();
    }

    public Station findBottomTerminal() {
        return sections.getLast().getDown();
    }
//
//    public void add(Section newSection) {
//        for (Section section : sections) {
//            Relation relation = section.calculateRelation(newSection);
//            if (relation.equals(Relation.EXTEND)) {
//                sections.addFirst(newSection);
//                return;
//            }
//        }
//    }

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
        Set<Station> stations = getStations();
        if (stations.contains(target.getUp()) && stations.contains(target.getDown())) {
            return Relation.INCLUDE;
        }
        if (findUpperTerminal().equals(target.getDown()) || findBottomTerminal().equals(target.getUp())) {
            return Relation.EXTEND;
        }
        for (Section section : sections) {
            if (section.getUp().equals(target.getUp()) || section.getDown().equals(target.getDown())) {
                return Relation.DIVIDE;
            }
        }
        return Relation.NONE;
    }
}
