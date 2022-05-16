package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Sections {

    private static final String CANNOT_ADD_SECTION = "섹션을 추가할 수 없습니다.";
    private static final String CANNOT_DELETE_SECTION = "역을 삭제할 수 없습니다.";

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
            final AddMatchingResult result = AddMatchingResult.matchMiddleStation(section, newSection);
            if (canAddStation(result, section, newSection)) {
                addSection(section, newSection, result);
                return;
            }
        }
        throw new IllegalArgumentException(CANNOT_ADD_SECTION + " " + newSection);
    }

    private boolean isNewUpSection(final Section newSection) {
        return AddMatchingResult.matchStartStation(value.get(0), newSection)
                == AddMatchingResult.ADD_TO_LEFT;
    }

    private boolean isNewDownSection(final Section newSection) {
        return AddMatchingResult.matchEndStation(value.get(value.size() - 1), newSection)
                == AddMatchingResult.ADD_TO_RIGHT;
    }

    private List<Section> addFirst(final Section newSection) {
        final List<Section> newSections =  new ArrayList<>(List.of(newSection));
        newSections.addAll(value);
        return newSections;
    }

    private boolean canAddStation(final AddMatchingResult result, final Section section, final Section newSection) {
        return (result == AddMatchingResult.ADD_TO_LEFT
                || result == AddMatchingResult.ADD_TO_RIGHT)
                && section.isDistanceLongerThan(newSection);
    }

    private void addSection(final Section section, final Section newSection, final AddMatchingResult result) {
        final List<Section> seperatedSection = separateSection(section, newSection, result);
        final int standardSection = value.indexOf(section) + 1;
        final List<Section> leftSection = value.subList(0, standardSection);
        final List<Section> rightSection = new ArrayList<>(List.of(seperatedSection.get(1)));

        rightSection.addAll(value.subList(standardSection, value.size()));
        leftSection.remove(leftSection.size() - 1);
        leftSection.add(seperatedSection.get(0));
        leftSection.addAll(rightSection);

        value = new ArrayList<>(leftSection);
    }

    private List<Section> separateSection(final Section section,
                                          final Section newSection,
                                          final AddMatchingResult result) {
        final Station newStation = newSection.getNewStation(result);
        final Section upSection = section.changeDownStationAndDistance(newSection, newStation);
        final Section downSection = section.changeUpStationAndDistance(newSection, newStation);
        return List.of(upSection, downSection);
    }

    public void deleteIfPossible(final Station target) {
        if (!canDelete()) {
            throw new IllegalArgumentException(CANNOT_DELETE_SECTION + " " + target);
        }
        if (sameWithLastUpStation(target) == DeleteMatchingResult.POSSIBLE_TO_DELETE) {
            value = value.subList(1, value.size());
            return;
        }
        if (sameWithLastDownStation(target) == DeleteMatchingResult.POSSIBLE_TO_DELETE) {
            value = value.subList(0, value.size() - 1);
            return;
        }
        final List<Section> existedStations = new ArrayList<>(value);
        for (Section section : existedStations) {
            final DeleteMatchingResult result = DeleteMatchingResult.matchStation(section, target);
            if (canDeleteStation(result)) {
                deleteStation(section, target);
                return;
            }
        }
        throw new IllegalArgumentException(CANNOT_DELETE_SECTION + " " + target);
    }

    private DeleteMatchingResult sameWithLastDownStation(final Station target) {
        return DeleteMatchingResult.matchWithLastDownStation(value.get(value.size() - 1), target);
    }

    private DeleteMatchingResult sameWithLastUpStation(final Station target) {
        return DeleteMatchingResult.matchWithLastUpStation(value.get(0), target);
    }

    private void deleteStation(final Section section, final Station target) {
        final int targetIndex = value.indexOf(section);
        final int targetNextIndex = targetIndex + 1;
        final Section targetNext = value.get(targetNextIndex);
        final Section newSection = section.combineTwoSection(targetNext);

        if (value.size() == 2) {
            value = new ArrayList<>(List.of(newSection));
            return;
        }

        final List<Section> leftSections = value.subList(0, targetIndex);
        final List<Section> rightSections = value.subList(targetIndex + 2, value.size());

        leftSections.add(newSection);
        leftSections.addAll(rightSections);
        value = new ArrayList<>(leftSections);
    }

    private boolean canDeleteStation(final DeleteMatchingResult result) {
        return result == DeleteMatchingResult.POSSIBLE_TO_DELETE;
    }

    private boolean canDelete() {
        return value.size() >= 2;
    }

    public List<Section> getDeletedSections(final List<Section> sections) {
        final List<Section> previousSections = new ArrayList<>(sections);
        previousSections.removeAll(value);
        return previousSections;
    }

    public List<Section> getAddSections(final List<Section> sections) {
        final List<Section> currentSections = new ArrayList<>(value);
        currentSections.removeAll(sections);
        if (currentSections.isEmpty()) {
            return value;
        }
        return currentSections;
    }

    public List<Section> getValue() {
        return value;
    }
}
