package wooteco.subway.section;

import wooteco.subway.exception.SubwayException;
import wooteco.subway.station.Station;

import java.util.*;

public class Sections {
    private final List<Section> sections;

    public Sections() {
        this(Collections.emptyList());
    }

    public Sections(List<Section> sections) {
        this.sections = sort(sections);
    }

    private List<Section> sort(List<Section> sections) {
        Queue<Section> waiting = new LinkedList<>(sections);
        Deque<Section> result = new ArrayDeque<>();

        if (sections.isEmpty()) {
            return sections;
        }

        result.addLast(waiting.poll());
        while (!waiting.isEmpty()) {
            sortUpToDown(waiting, result);
        }

        return new ArrayList<>(result);
    }

    private void sortUpToDown(Queue<Section> waiting, Deque<Section> result) {
        Section section = waiting.poll();
        Section frontBase = result.peek();
        Section lastBase = result.peekLast();
        if (section.isSameUpStation(lastBase.getDownStation())) {
            result.addLast(section);
            return;
        }
        if (section.isSameDownStation(frontBase.getUpStation())) {
            result.addFirst(section);
            return;
        }
        waiting.add(section);
    }

    public void addSection(Section section) {
        validatesEndPoints(section);
        if (isEndPoint(section)) {
            sections.add(section);
            return;
        }
        if (newUpStationInStartPoints(section)) {
            updateUpStation(section);
            return;
        }
        if (newDownStationInEndPoints(section)) {
            updateDownStation(section);
            return;
        }
        throw new SubwayException("추가할 수 없는 구간입니다!");
    }

    private void validatesEndPoints(Section section) {
        boolean isUpStationExist = isExist(section.getUpStation());
        boolean isDownStationExist = isExist(section.getDownStation());
        if (isUpStationExist && isDownStationExist) {
            throw new SubwayException("추가할 수 없는 구간입니다.");
        }
        if (!isUpStationExist && !isDownStationExist) {
            throw new SubwayException("추가할 수 없는 구간입니다.");
        }
    }

    private boolean isExist(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isSameUpStation(station)
                        || section.isSameDownStation(station));
    }

    private Section findByUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(upStation))
                .findAny()
                .orElseThrow(() -> new SubwayException("없는 구간입니다!"));
    }

    private Section findByDownStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.isSameDownStation(downStation))
                .findAny()
                .orElseThrow(() -> new SubwayException("없는 구간입니다!"));
    }

    private boolean isEndPoint(Section section) {
        if (isUpEndStation(section.getDownStation())) {
            return true;
        }
        return isDownEndStation(section.getUpStation());
    }

    private boolean isUpEndStation(Station station) {
        Station upStation = sections.get(0).getUpStation();
        return upStation.equals(station);
    }

    private boolean isDownEndStation(Station station) {
        Station downStation = sections.get(sections.size() - 1)
                .getDownStation();
        return downStation.equals(station);
    }

    private boolean newUpStationInStartPoints(Section section) {
        return sections.stream()
                .anyMatch(s -> s.isSameUpStation(section.getUpStation()));
    }

    private boolean newDownStationInEndPoints(Section section) {
        return sections.stream()
                .anyMatch(s -> s.isSameDownStation(section.getDownStation()));
    }

    private void updateUpStation(Section newSection) {
        Section upSection = findByUpStation(newSection.getUpStation());
        upSection.updateUpStation(newSection.getDownStation());
        addNewSection(upSection, newSection);
    }

    private void updateDownStation(Section section) {
        Section downSection = findByDownStation(section.getDownStation());
        downSection.updateDownStation(section.getUpStation());
        addNewSection(downSection, section);
    }

    private void addNewSection(Section section, Section newSection) {
        section.updateDistance(newSection.getDistance());
        sections.add(newSection);
    }

    public void delete(Station station) {
        checkRemainSectionSize();
        if (isUpEndStation(station)) {
            sections.remove(0);
            return;
        }
        if (isDownEndStation(station)) {
            sections.remove(sections.size() - 1);
            return;
        }
        deleteMiddleStation(station);
    }

    private void checkRemainSectionSize() {
        if (sections.size() <= 1) {
            throw new SubwayException("제거할 수 없습니다.");
        }
    }

    private void deleteMiddleStation(Station station) {
        Section downSection = findByDownStation(station);
        Section upSection = findByUpStation(station);
        int distance = downSection.getDistance() + upSection.getDistance();
        Section section =
                new Section(upSection.getLine(), downSection.getUpStation(), upSection.getDownStation(), distance);
        sections.remove(downSection);
        sections.remove(upSection);
        sections.add(section);
    }

    public List<Station> getStations() {
        Set<Station> sortedStations = new LinkedHashSet<>();
        for (Section section : sections) {
            sortedStations.add(section.getUpStation());
            sortedStations.add(section.getDownStation());
        }
        return new ArrayList<>(sortedStations);
    }

    public List<Section> getSections() {
        return new ArrayList<>(sections);
    }
}
