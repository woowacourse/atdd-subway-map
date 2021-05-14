package wooteco.subway.section;

import wooteco.subway.exception.SubwayException;
import wooteco.subway.station.Station;

import java.util.*;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sort(sections);
    }

    private List<Section> sort(List<Section> sections) {
        Queue<Section> waiting = new LinkedList<>(sections);
        Deque<Section> result = new ArrayDeque<>();

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


    public void validatesEndPoints(Section section) {
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

    public Section findByUpStationId(Station upStation) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(upStation))
                .findAny()
                .orElseThrow(() -> new SubwayException("없는 구간입니다!"));
    }

    public Section findByDownStationId(Station downStation) {
        return sections.stream()
                .filter(section -> section.isSameDownStation(downStation))
                .findAny()
                .orElseThrow(() -> new SubwayException("없는 구간입니다!"));
    }

    public boolean isEndPoint(Section section) {
        if (isUpEndPoint(section.getDownStation())) {
            return true;
        }
        return isDownEndPoint(section.getUpStation());
    }

    public boolean isUpEndPoint(Station station) {
        Station upStation = sections.get(0).getUpStation();
        return upStation.equals(station);
    }

    public boolean isDownEndPoint(Station station) {
        Station downStation = sections.get(sections.size() - 1)
                .getDownStation();
        return downStation.equals(station);
    }

    public boolean newUpStationInStartPoints(Section section) {
        return sections.stream()
                .anyMatch(s -> s.isSameUpStation(section.getUpStation()));
    }

    public boolean newDownStationInEndPoints(Section section) {
        return sections.stream()
                .anyMatch(s -> s.isSameDownStation(section.getDownStation()));
    }

    public void checkRemainSectionSize() {
        if (sections.size() <= 1) {
            throw new SubwayException("제거할 수 없습니다.");
        }
    }

    public List<Section> getSections() {
        return new ArrayList<>(sections);
    }
}
