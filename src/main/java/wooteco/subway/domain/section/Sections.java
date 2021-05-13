package wooteco.subway.domain.section;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exceptions.NotAddableSectionException;

public class Sections {

    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Deque<Long> stationIds = getSortedId();
        for (long stationId : stationIds) {
            Station station = sections.stream()
                .map(section -> section.getStationById(stationId))
                .findAny().get();
            stations.add(station);
        }
        return stations;
    }

    private Deque<Long> getSortedId() {
        Map<Long, Long> upLineInfo = getUpLineInfo();
        Map<Long, Long> downLineInfo = getDownLineInfo();
        Deque<Long> stationIds = new ArrayDeque<>();

        Long initValue = upLineInfo.entrySet().iterator().next().getKey();
        stationIds.add(initValue);

        while (stationIds.size() < getSections().size() + 1) {
            if (upLineInfo.get(stationIds.getLast()) != null) {
                stationIds.addLast(upLineInfo.get(stationIds.getLast()));
            }
            if (downLineInfo.get(stationIds.getFirst()) != null) {
                stationIds.addFirst(downLineInfo.get(stationIds.getFirst()));
            }
        }
        return new ArrayDeque<>(stationIds);
    }

    private Map<Long, Long> getUpLineInfo() {
        Map<Long, Long> stationInfo = new HashMap<>();
        sections.forEach(section -> {
            stationInfo.put(section.getUpStationId(), section.getDownStationId());
        });
        return new HashMap<>(stationInfo);
    }

    private Map<Long, Long> getDownLineInfo() {
        Map<Long, Long> stationInfo = new HashMap<>();
        sections.forEach(section -> {
            stationInfo.put(section.getDownStationId(), section.getUpStationId());
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
        if (section.getDistance() <= savedSection.getDistance()) {
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
}
