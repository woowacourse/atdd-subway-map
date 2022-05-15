package wooteco.subway.domain.section;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import wooteco.subway.domain.station.Station;

public class Sections {

    private static final String STATION_NOT_REGISTERED_EXCEPTION = "구간에 등록되지 않은 지하철역입니다.";
    private static final String LAST_SECTION_EXCEPTION = "노선의 마지막 구간은 제거할 수 없습니다.";
    private static final String ALL_STATIONS_REGISTERED_EXCEPTION = "이미 노선에 등록된 지하철역들입니다.";
    private static final String NO_STATION_REGISTERED_EXCEPTION = "적어도 하나의 지하철역은 이미 노선에 등록되어 있어야 합니다.";

    private final List<Section> value;

    Sections(List<Section> value) {
        this.value = value;
    }

    public Sections save(Section newSection) {
        validateSingleRegisteredStation(newSection);
        List<Section> sections = new ArrayList<>(value);
        if (!isEndSection(newSection)) {
            updateOriginalSection(newSection, sections);
        }
        sections.add(newSection);
        return new Sections(sections);
    }

    private void updateOriginalSection(Section newSection, List<Section> sections) {
        boolean isRegisteredUpStation = isRegistered(newSection.getUpStation());
        if (isRegisteredUpStation) {
            updateLowerSection(newSection, sections);
            return;
        }
        updateUpperSection(newSection, sections);
    }

    private void updateUpperSection(Section newSection, List<Section> sections) {
        Section oldSection = getUpperSection(newSection.getDownStation());
        sections.remove(oldSection);
        Section updatedSection = new Section(oldSection.getUpStation(),
                newSection.getUpStation(),
                oldSection.toRemainderDistance(newSection));
        sections.add(updatedSection);
    }

    private void updateLowerSection(Section newSection, List<Section> sections) {
        Section oldSection = getLowerSection(newSection.getUpStation());
        sections.remove(oldSection);
        Section updatedSection = new Section(newSection.getDownStation(),
                oldSection.getDownStation(),
                oldSection.toRemainderDistance(newSection));
        sections.add(updatedSection);
    }

    private void validateSingleRegisteredStation(Section section) {
        boolean isRegisteredUpStation = isRegistered(section.getUpStation());
        boolean isRegisteredDownStation = isRegistered(section.getDownStation());
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
        Section currentUpperEndSection = value.get(0);
        return currentUpperEndSection.hasUpStationOf(section.getDownStation());
    }

    private boolean isNewLowerEndSection(Section section) {
        Section currentLowerEndSection = value.get(value.size() - 1);
        return currentLowerEndSection.hasDownStationOf(section.getUpStation());
    }

    public Sections delete(Station station) {
        validateRegisteredStation(station);
        validateNotLastSection();
        List<Section> sections = new ArrayList<>(value);
        if (isMiddleStation(station)) {
            return removeMiddleStation(station, sections);
        }
        sections.removeIf(section -> section.hasStationOf(station));
        return new Sections(sections);
    }

    private void validateRegisteredStation(Station station) {
        if (!isRegistered(station)) {
            throw new IllegalArgumentException(STATION_NOT_REGISTERED_EXCEPTION);
        }
    }

    private boolean isRegistered(Station station) {
        return value.stream()
                .anyMatch(section -> section.hasStationOf(station));
    }

    private void validateNotLastSection() {
        if (value.size() == 1) {
            throw new IllegalArgumentException(LAST_SECTION_EXCEPTION);
        }
    }

    private boolean isMiddleStation(Station station) {
        return value.stream()
                .filter(section -> section.hasStationOf(station))
                .count() == 2;
    }

    private Sections removeMiddleStation(Station station, List<Section> sections) {
        Section upperSection = getUpperSection(station);
        Section lowerSection = getLowerSection(station);

        sections.removeAll(List.of(upperSection, lowerSection));
        sections.add(toConnectedSection(upperSection, lowerSection));
        return new Sections(sections);
    }

    private Section getUpperSection(Station station) {
        return value.stream()
                .filter(section -> section.hasDownStationOf(station))
                .findFirst()
                .get();
    }

    private Section getLowerSection(Station station) {
        return value.stream()
                .filter(section -> section.hasUpStationOf(station))
                .findFirst()
                .get();
    }

    private Section toConnectedSection(Section upperSection, Section lowerSection) {
        return new Section(upperSection.getUpStation(),
                lowerSection.getDownStation(),
                upperSection.toConnectedDistance(lowerSection));
    }

    public List<Section> extractNewSections(Sections previousSections) {
        List<Section> previous = new ArrayList<>(previousSections.value);
        List<Section> current = new ArrayList<>(value);

        current.removeAll(previous);
        return current;
    }

    public List<Section> extractDeletedSections(Sections previousSections) {
        List<Section> previous = new ArrayList<>(previousSections.value);
        List<Section> current = new ArrayList<>(value);

        previous.removeAll(current);
        return previous;
    }

    public List<Station> toSortedStations() {
        return new ArrayList<>(){{
            add(toUpperEndStation());
            addAll(toDownStations());
        }};
    }

    private List<Station> toDownStations() {
        return value.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    private Station toUpperEndStation() {
        return value.get(0).getUpStation();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sections sections = (Sections) o;
        return Objects.equals(value, sections.value);
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
