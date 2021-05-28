package wooteco.subway.domain.section;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.NotRemovableSectionException;
import wooteco.subway.exception.OverDistanceException;
import wooteco.subway.exception.SectionExistException;
import wooteco.subway.exception.StationForSectionNotExistException;

public class Sections {

    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        return sections.stream()
            .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
            .collect(Collectors.toList());
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        checkExistSection(section);
        checkExistStation(section);

        if (isFirstOrLastSection(section)) {
            sections.add(section);
            return;
        }

        addUpOrDown(section);
    }

    public boolean isFirstOrLastSection(Section section) {
        List<Station> downStations = sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());

        List<Station> upStations = sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());

        boolean isFirst = upStations.contains(section.getDownStation())
            && !downStations.contains(section.getUpStation());

        boolean isLast = !upStations.contains(section.getDownStation())
            && downStations.contains(section.getUpStation());

        return isFirst || isLast;
    }

    private void addUpOrDown(Section section) {
        Optional<Section> upSection = sections.stream()
            .filter(sct -> sct.getUpStation().equals(section.getUpStation()))
            .findFirst();
        if (upSection.isPresent()) {
            addUpAndMiddleSection(section, upSection.get());
            return;
        }

        sections.stream()
            .filter(sct -> sct.getDownStation().equals(section.getDownStation()))
            .findFirst()
            .ifPresent(downSection -> addDownAndMiddleSection(section, downSection));
    }

    private void addUpAndMiddleSection(Section section, Section savedSection) {
        checkDistance(section, savedSection);
        addNewSection(section);

        sections.add(new Section(
            section.getDownStation(),
            savedSection.getDownStation(),
            savedSection.getDistance() - section.getDistance()
        ));
    }

    private void addDownAndMiddleSection(Section section, Section savedSection) {
        checkDistance(section, savedSection);
        addNewSection(section);

        sections.add(new Section(
            savedSection.getUpStation(),
            section.getUpStation(),
            savedSection.getDistance() - section.getDistance()
        ));
    }

    private void checkDistance(Section section, Section savedSection) {
        if (section.getDistance() >= savedSection.getDistance()) {
            throw new OverDistanceException();
        }
        sections.remove(savedSection);
    }

    private void addNewSection(Section section) {
        this.sections.add(new Section(
            section.getUpStation(),
            section.getDownStation(),
            section.getDistance()
        ));
    }

    private void checkExistSection(Section section) {
        if (sections.contains(section)) {
            throw new SectionExistException();
        }
    }

    private void checkExistStation(Section section) {
        List<Station> stations = getStations();
        if (!stations.contains(section.getUpStation()) &&
            !stations.contains(section.getDownStation())) {
            throw new StationForSectionNotExistException();
        }
    }

    public void removeSection(Station station) {
        validateRemovable();
        List<Section> sectionsHaveStation = getSectionsHaveStation(station);

        if (sectionsHaveStation.size() == 1) {
            sections.remove(sectionsHaveStation.get(0));
            return;
        }

        Section newSection = newSectionForRemoveStation(station, sectionsHaveStation);
        for (Section section : sectionsHaveStation) {
            sections.remove(section);
        }
        sections.add(newSection);
    }

    private List<Section> getSectionsHaveStation(Station station) {
        return sections.stream()
            .filter(section -> section.contains(station))
            .collect(Collectors.toList());
    }

    private Section newSectionForRemoveStation(Station station, List<Section> sectionsHaveStation) {
        Section affectedUpSection = sectionsHaveStation.stream()
            .filter(section -> section.getUpStation().equals(station))
            .findAny().orElseThrow(NotRemovableSectionException::new);
        Section affectedDownSection = sectionsHaveStation.stream()
            .filter(section -> section.getDownStation().equals(station))
            .findAny().orElseThrow(NotRemovableSectionException::new);

        return new Section(
            affectedUpSection.getUpStation(),
            affectedDownSection.getDownStation(),
            affectedUpSection.getDistance() + affectedDownSection.getDistance()
        );
    }

    private void validateRemovable() {
        if (sections.size() < 2) {
            throw new NotRemovableSectionException();
        }
    }
}
