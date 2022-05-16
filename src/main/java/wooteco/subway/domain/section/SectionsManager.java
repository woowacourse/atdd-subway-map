package wooteco.subway.domain.section;

import java.util.List;
import java.util.Objects;
import wooteco.subway.domain.station.Station;

public class SectionsManager {

    private static final String STATION_NOT_REGISTERED_EXCEPTION = "구간에 등록되지 않은 지하철역입니다.";
    private static final String LAST_SECTION_EXCEPTION = "노선의 마지막 구간은 제거할 수 없습니다.";
    private static final String ALL_STATIONS_REGISTERED_EXCEPTION = "이미 노선에 등록된 지하철역들입니다.";
    private static final String NO_STATION_REGISTERED_EXCEPTION = "적어도 하나의 지하철역은 이미 노선에 등록되어 있어야 합니다.";

    private final Sections sections;

    private SectionsManager(Sections sections) {
        this.sections = sections;
    }

    public SectionsManager(List<Section> value) {
        this(new Sections(value));
    }

    public Sections save(Section newSection) {
        validateSingleRegisteredStation(newSection);
        List<Section> sections = this.sections.toSortedList();
        if (!this.sections.isNewEndSection(newSection)) {
            updateOriginalSection(newSection, sections);
        }
        sections.add(newSection);
        return new Sections(sections);
    }

    private void updateOriginalSection(Section newSection, List<Section> sections) {
        boolean isRegisteredUpStation = this.sections.isRegistered(newSection.getUpStation());
        if (isRegisteredUpStation) {
            updateLowerSection(newSection, sections);
            return;
        }
        updateUpperSection(newSection, sections);
    }

    private void updateUpperSection(Section newSection, List<Section> sections) {
        Section oldSection = this.sections.findUpperSectionOfStation(newSection.getDownStation());
        sections.remove(oldSection);
        Section updatedSection = new Section(oldSection.getUpStation(),
                newSection.getUpStation(),
                oldSection.toRemainderDistance(newSection));
        sections.add(updatedSection);
    }

    private void updateLowerSection(Section newSection, List<Section> sections) {
        Section oldSection = this.sections.findLowerSectionOfStation(newSection.getUpStation());
        sections.remove(oldSection);
        Section updatedSection = new Section(newSection.getDownStation(),
                oldSection.getDownStation(),
                oldSection.toRemainderDistance(newSection));
        sections.add(updatedSection);
    }

    private void validateSingleRegisteredStation(Section section) {
        boolean isRegisteredUpStation = sections.isRegistered(section.getUpStation());
        boolean isRegisteredDownStation = sections.isRegistered(section.getDownStation());
        if (isRegisteredUpStation && isRegisteredDownStation) {
            throw new IllegalArgumentException(ALL_STATIONS_REGISTERED_EXCEPTION);
        }
        if (!isRegisteredUpStation && !isRegisteredDownStation) {
            throw new IllegalArgumentException(NO_STATION_REGISTERED_EXCEPTION);
        }
    }

    public Sections delete(Station station) {
        validateRegisteredStation(station);
        validateNotLastSection();
        if (sections.checkMiddleStation(station)) {
            return removeMiddleStation(station);
        }
        return removeEndStation(station);
    }

    private void validateRegisteredStation(Station station) {
        if (!sections.isRegistered(station)) {
            throw new IllegalArgumentException(STATION_NOT_REGISTERED_EXCEPTION);
        }
    }

    private void validateNotLastSection() {
        if (sections.hasSingleSection()) {
            throw new IllegalArgumentException(LAST_SECTION_EXCEPTION);
        }
    }

    private Sections removeMiddleStation(Station station) {
        List<Section> sections = this.sections.toSortedList();
        Section upperSection = this.sections.findUpperSectionOfStation(station);
        Section lowerSection = this.sections.findLowerSectionOfStation(station);

        sections.removeAll(List.of(upperSection, lowerSection));
        sections.add(toConnectedSection(upperSection, lowerSection));
        return new Sections(sections);
    }

    private Section toConnectedSection(Section upperSection, Section lowerSection) {
        return new Section(upperSection.getUpStation(),
                lowerSection.getDownStation(),
                upperSection.toConnectedDistance(lowerSection));
    }

    private Sections removeEndStation(Station station) {
        List<Section> sections = this.sections.toSortedList();
        sections.removeIf(section -> section.hasStationOf(station));
        return new Sections(sections);
    }

    public List<Section> extractNewSections(Sections updatedSections) {
        List<Section> previous = sections.toSortedList();
        List<Section> current = updatedSections.toSortedList();

        current.removeAll(previous);
        return current;
    }

    public List<Section> extractDeletedSections(Sections updatedSections) {
        List<Section> previous = sections.toSortedList();
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
        return Objects.equals(sections, sectionsManager.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }

    @Override
    public String toString() {
        return "Sections{" + "value=" + sections + '}';
    }
}
