package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Sections {

    private static final int FIRST_INDEX = 0;

    private final List<Section> sections;

    public static Sections create(List<Section> sections) {
        return new Sections(sections);
    }

    public static Sections create() {
        return new Sections(new ArrayList<>());
    }


    public void addSection(Section section) {
        sections.add(section);
    }

    public Section firstSection() {
        return sections.get(FIRST_INDEX);
    }

    public List<Station> asStations() {
        LinkedList<Station> sortedStation = new LinkedList<>();
        final Section pivotSection = sections.get(0);
        sortPreviousSections(sortedStation, pivotSection);
        sortFollowingSections(sortedStation, pivotSection);
        return sortedStation;
    }

    private void sortPreviousSections(LinkedList<Station> sortedSections, Section section) {
        final Station upStation = section.getUpStation();
        sortedSections.addFirst(upStation);
        findSectionByDownStation(upStation)
            .ifPresent(sec -> sortPreviousSections(sortedSections, sec));
    }

    private Optional<Section> findSectionByDownStation(Station targetStation) {
        return sections.stream()
            .filter(section -> section.isDownStation(targetStation))
            .findAny();
    }

    private void sortFollowingSections(LinkedList<Station> sortedSections, Section section) {
        final Station downStation = section.getDownStation();
        sortedSections.addLast(downStation);
        findSectionByUpStation(downStation)
            .ifPresent(sec -> sortFollowingSections(sortedSections, sec));
    }

    private Optional<Section> findSectionByUpStation(Station targetStation) {
        return sections.stream()
            .filter(section -> section.isUpStation(targetStation))
            .findAny();
    }
}
