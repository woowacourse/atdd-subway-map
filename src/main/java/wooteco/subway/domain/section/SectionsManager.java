package wooteco.subway.domain.section;

import java.util.List;
import java.util.Objects;
import wooteco.subway.domain.station.Station;

public class SectionsManager {

    private static final String STATION_NOT_REGISTERED_EXCEPTION = "구간에 등록되지 않은 지하철역입니다.";
    private static final String LAST_SECTION_EXCEPTION = "노선의 마지막 구간은 제거할 수 없습니다.";
    private static final String ALL_STATIONS_REGISTERED_EXCEPTION = "이미 노선에 등록된 지하철역들입니다.";
    private static final String NO_STATION_REGISTERED_EXCEPTION = "적어도 하나의 지하철역은 이미 노선에 등록되어 있어야 합니다.";

    private final Sections2 value;

    private SectionsManager(Sections2 value) {
        this.value = value;
    }

    public SectionsManager(List<Section> value) {
        this(new Sections2(value));
    }

    public Sections2 save(Section newSection) {
        validateSingleRegisteredStation(newSection);
        List<Section> sections = value.toSortedList();
        if (!isEndSection(newSection)) {
            updateOriginalSection(newSection, sections);
        }
        sections.add(newSection);
        return new Sections2(sections);
    }

    private void updateOriginalSection(Section newSection, List<Section> sections) {
        boolean isRegisteredUpStation = value.isRegisteredAsUpStation(newSection);
        if (isRegisteredUpStation) {
            updateLowerSection(newSection, sections);
            return;
        }
        updateUpperSection(newSection, sections);
    }

    private void updateUpperSection(Section newSection, List<Section> sections) {
        Section oldSection = value.findUpperSectionOfStation(newSection.getDownStation());
        sections.remove(oldSection);
        Section updatedSection = new Section(oldSection.getUpStation(),
                newSection.getUpStation(),
                oldSection.toRemainderDistance(newSection));
        sections.add(updatedSection);
    }

    private void updateLowerSection(Section newSection, List<Section> sections) {
        Section oldSection = value.findLowerSectionOfStation(newSection.getUpStation());
        sections.remove(oldSection);
        Section updatedSection = new Section(newSection.getDownStation(),
                oldSection.getDownStation(),
                oldSection.toRemainderDistance(newSection));
        sections.add(updatedSection);
    }

    private void validateSingleRegisteredStation(Section section) {
        boolean isRegisteredUpStation = value.isRegisteredAsUpStation(section);
        boolean isRegisteredDownStation = value.isRegisteredAsDownStation(section);
        if (isRegisteredUpStation && isRegisteredDownStation) {
            throw new IllegalArgumentException(ALL_STATIONS_REGISTERED_EXCEPTION);
        }
        if (!isRegisteredUpStation && !isRegisteredDownStation) {
            throw new IllegalArgumentException(NO_STATION_REGISTERED_EXCEPTION);
        }
    }

    private boolean isEndSection(Section section) {
        return isNewUpperEndSection(section) || isNewLowerEndSection(section);
    }

    private boolean isNewUpperEndSection(Section section) {
        Section currentUpperEndSection = value.getUpperEndSection();
        return currentUpperEndSection.hasUpStationOf(section.getDownStation());
    }

    private boolean isNewLowerEndSection(Section section) {
        Section currentLowerEndSection = value.getLowerEndSection();
        return currentLowerEndSection.hasDownStationOf(section.getUpStation());
    }

    public Sections2 delete(Station station) {
        validateRegisteredStation(station);
        validateNotLastSection();
        if (value.checkMiddleStation(station)) {
            return removeMiddleStation(station);
        }
        return removeEndStation(station);
    }

    private void validateRegisteredStation(Station station) {
        if (!value.isRegistered(station)) {
            throw new IllegalArgumentException(STATION_NOT_REGISTERED_EXCEPTION);
        }
    }

    private void validateNotLastSection() {
        if (value.hasSingleSection()) {
            throw new IllegalArgumentException(LAST_SECTION_EXCEPTION);
        }
    }

    private Sections2 removeMiddleStation(Station station) {
        List<Section> sections = value.toSortedList();
        Section upperSection = value.findUpperSectionOfStation(station);
        Section lowerSection = value.findLowerSectionOfStation(station);

        sections.removeAll(List.of(upperSection, lowerSection));
        sections.add(toConnectedSection(upperSection, lowerSection));
        return new Sections2(sections);
    }

    private Section toConnectedSection(Section upperSection, Section lowerSection) {
        return new Section(upperSection.getUpStation(),
                lowerSection.getDownStation(),
                upperSection.toConnectedDistance(lowerSection));
    }

    private Sections2 removeEndStation(Station station) {
        List<Section> sections = value.toSortedList();
        sections.removeIf(section -> section.hasStationOf(station));
        return new Sections2(sections);
    }

    public List<Section> extractNewSections(Sections2 updatedSections) {
        List<Section> previous = value.toSortedList();
        List<Section> current = updatedSections.toSortedList();

        current.removeAll(previous);
        return current;
    }

    public List<Section> extractDeletedSections(Sections2 updatedSections) {
        List<Section> previous = value.toSortedList();
        List<Section> current = updatedSections.toSortedList();

        previous.removeAll(current);
        return previous;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SectionsManager sectionsManager = (SectionsManager) o;
        return Objects.equals(value, sectionsManager.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Sections{" + "value=" + value + '}';
    }
}
