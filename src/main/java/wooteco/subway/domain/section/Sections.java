package wooteco.subway.domain.section;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.NotAddableSectionException;
import wooteco.subway.exception.NotRemovableSectionException;

public class Sections {

    private List<Section> sections;

    public Sections() {
        sections = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        List<Long> stationIds = getSortedId();
        for (long stationId : stationIds) {
            Station station = sections.stream()
                .map(section -> section.getStationById(stationId))
                .findAny().get();
            stations.add(station);
        }
        return stations;
    }

    private List<Long> getSortedId() {
        if (sections.size() == 1) {
            return Arrays.asList(
                sections.get(0).getUpStationId(),
                sections.get(0).getDownStationId()
            );
        }

        Map<Long, Long> sectionInfo = getSectionInfo();
        List<Long> stationIds = new LinkedList<>();

        Long firstValue = sectionInfo.keySet().stream()
            .filter(info -> !sectionInfo.containsValue(info))
            .findAny().get();
        Long lastValue = sectionInfo.values().stream()
            .filter(info -> !sectionInfo.containsKey(info))
            .findAny().get();

        stationIds.add(firstValue);
        while (!stationIds.contains(lastValue)) {
            stationIds.add(
                sectionInfo.get(stationIds.get(stationIds.size() - 1)
                )
            );
        }
        return new ArrayList<>(stationIds);
    }

    private Map<Long, Long> getSectionInfo() {
        Map<Long, Long> stationInfo = new HashMap<>();
        sections.forEach(section -> {
            stationInfo.put(section.getUpStationId(), section.getDownStationId());
        });
        return new HashMap<>(stationInfo);
    }

    public void addSection(Section section) {
        if (this.sections.isEmpty()) {
            this.sections.add(section);
            return;
        }

        checkExistStation(section);
        checkAlreadyExistSection(section);

        List<Station> stations = getStations();
        if (stations.get(0).equals(section.getDownStation()) ||
            stations.get(stations.size() - 1).equals(section.getUpStation())
        ) {
            addNewSection(section);
            return;
        }
        addUpOrDown(section);
    }


    private void addUpOrDown(Section section) {
        this.sections.stream()
            .filter(sct -> sct.getUpStation().equals(section.getUpStation()))
            .findFirst()
            .ifPresent(savedSection -> addUpAndMiddleSection(section, savedSection));

        this.sections.stream()
            .filter(sct -> sct.getDownStation().equals(section.getDownStation()))
            .findFirst()
            .ifPresent(savedSection -> addDownAndMiddleSection(section, savedSection));
    }

    private void addUpAndMiddleSection(Section section, Section savedSection) {
        checkDistance(section, savedSection);
        addNewSection(section);

        this.sections.add(new Section(
            savedSection.getUpStation(),
            savedSection.getUpStation(),
            savedSection.getDistance() - section.getDistance()
        ));
    }

    private void addDownAndMiddleSection(Section section, Section savedSection) {
        checkDistance(section, savedSection);
        addNewSection(section);

        this.sections.add(new Section(
            section.getDownStation(),
            savedSection.getDownStation(),
            savedSection.getDistance() - section.getDistance()
        ));
    }

    private void addNewSection(Section section) {
        this.sections.add(new Section(
            section.getUpStation(),
            section.getDownStation(),
            section.getDistance()
        ));
    }

    private void checkDistance(Section section, Section savedSection) {
        if (section.getDistance() >= savedSection.getDistance()) {
            throw new NotAddableSectionException();
        }
        this.sections.remove(savedSection);
    }

    private void checkAlreadyExistSection(Section section) {
        List<Station> stations = getStations();
        if (stations.contains(section.getUpStation()) &&
            stations.contains(section.getDownStation())) {
            throw new NotAddableSectionException();
        }
    }

    private void checkExistStation(Section section) {
        List<Station> stations = getStations();
        if (!stations.contains(section.getUpStation()) &&
            !stations.contains(section.getDownStation())) {
            throw new NotAddableSectionException();
        }
    }

    public void removeSectionsBy(Station station) {
        validateRemovable();
        sections.removeIf(section -> section.contains(station));
    }

    private void validateRemovable() {
        if (sections.size() < 2) {
            throw new NotRemovableSectionException();
        }
    }

    public Section findAffectedSectionByDeleteStation(Station station) {
        List<Section> sectionsHasStation = sections.stream()
            .filter(section -> section.contains(station))
            .collect(Collectors.toList());
        Section upSection = sectionsHasStation.stream()
            .filter(section -> section.getDownStation().equals(station))
            .findAny()
            .get();
        Section downSection = sectionsHasStation.stream()
            .filter(section -> section.getUpStation().equals(station))
            .findAny()
            .get();
        return new Section(
            upSection.getUpStation(),
            downSection.getDownStation(),
            upSection.getDistance() + downSection.getDistance()
        );
    }

    public List<Section> findAffectedSectionByAddSection(Section section) {
        List<Section> affectedSections = sections.stream()
            .filter(it -> it.contains(section.getUpStation()))
            .collect(Collectors.toList());
        affectedSections.addAll(sections.stream()
            .filter(it -> it.contains(section.getDownStation()))
            .collect(Collectors.toList()));
        return new ArrayList<>(affectedSections);
    }
}
