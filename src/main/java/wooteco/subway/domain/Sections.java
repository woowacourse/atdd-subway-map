package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Sections {

    private List<Section> value;

    public Sections(final List<Section> sections) {
        this.value = sections;
    }

    public void addIfPossible(final Section newSection) {
        if (value.isEmpty() || isNewDownSection(newSection)) {
            value.add(newSection);
            return;
        }
        if (isNewUpSection(newSection)) {
            value = addFirst(newSection);
            return;
        }
        final List<Section> existedStations = new ArrayList<>(value);
        for (Section section : existedStations) {
            final MatchingResult result = section.match(newSection);
            if (canAddStation(result)) {
                add(section, newSection, result);
                break;
            }
        }
    }

    private boolean isNewUpSection(final Section newSection) {
        return value.get(0).matchStartStation(newSection) == MatchingResult.ADD_TO_LEFT;
    }

    private boolean isNewDownSection(final Section newSection) {
        return value.get(value.size() - 1).matchEndStation(newSection) == MatchingResult.ADD_TO_RIGHT;
    }

    private List<Section> addFirst(final Section newSection) {
        final List<Section> newSections =  new ArrayList<>(List.of(newSection));
        newSections.addAll(value);
        return newSections;
    }

    private boolean canAddStation(final MatchingResult result) {
        return result == MatchingResult.ADD_TO_LEFT
                || result == MatchingResult.ADD_TO_RIGHT;
    }

    private void add(final Section section, final Section newSection, final MatchingResult result) {
        final List<Section> seperatedSection = separateSection(section, newSection, result);
        final int standardSection = value.indexOf(section) + 1;
        List<Section> leftSection = value.subList(0, standardSection);
        List<Section> rightSection = new ArrayList<>(List.of(seperatedSection.get(1)));
        rightSection.addAll(value.subList(standardSection, value.size()));
        leftSection.remove(leftSection.size() - 1);
        leftSection.add(seperatedSection.get(0));
        leftSection.addAll(rightSection);
        value = new ArrayList<>(leftSection);
    }

    private List<Section> separateSection(final Section section,
                                          final Section newSection,
                                          final MatchingResult result) {
        final Station newStation = newSection.getNewStation(result);
        final Section upSection = section.changeDownStationAndDistance(newSection, newStation);
        final Section downSection = section.changeUpStationAndDistance(newSection, newStation);
        return List.of(upSection, downSection);
    }
}
