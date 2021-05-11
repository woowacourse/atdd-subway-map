package wooteco.subway.section;

import wooteco.subway.exception.SubwayException;

import java.util.*;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sort(sections);
    }

    private List<Section> sort(List<Section> sections) {
        Queue<Section> waiting = new LinkedList<>(sections);
        Deque<Section> result = new ArrayDeque<>();

        result.addLast(waiting.remove());
        while (!waiting.isEmpty()) {
            Section section = waiting.remove();
            Section frontBase = result.peek();
            Section lastBase = result.peekLast();

            if (lastBase.getDownStationId().equals(section.getUpStationId())) {
                result.addLast(section);
                continue;
            }

            if (frontBase.getUpStationId().equals(section.getDownStationId())) {
                result.addFirst(section);
                continue;
            }

            waiting.add(section);
        }

        return new ArrayList<>(result);
    }


    public void validatesEndPoints(Section section) {
        boolean isUpStationExist = isExist(section.getUpStationId());
        boolean isDownStationExist = isExist(section.getDownStationId());
        if (isUpStationExist && isDownStationExist) {
            throw new SubwayException("추가할 수 없는 구간입니다.");
        }
        if (!isUpStationExist && !isDownStationExist) {
            throw new SubwayException("추가할 수 없는 구간입니다.");
        }
    }

    private boolean isExist(Long stationId) {
        return sections.stream()
                .anyMatch(section -> stationId.equals(section.getUpStationId())
                        || stationId.equals(section.getDownStationId()));
    }

    public Section findByUpStationId(Long upStationId) {
        return sections.stream()
                .filter(section -> upStationId.equals(section.getUpStationId()))
                .findAny()
                .orElseThrow(() -> new SubwayException("없는 구간입니다!"));
    }

    public Section findByDownStationId(Long downStationId) {
        return sections.stream()
                .filter(section -> downStationId.equals(section.getDownStationId()))
                .findAny()
                .orElseThrow(() -> new SubwayException("없는 구간입니다!"));
    }

    public boolean isEndPoint(Section section) {
        if (isUpStation(section.getDownStationId())) {
            return true;
        }
        return isDownStation(section.getUpStationId());
    }

    public boolean isUpStation(Long id) {
        Long upStationId = sections.get(0).getUpStationId();
        return upStationId.equals(id);
    }

    public boolean isDownStation(Long id) {
        Long downStationId = sections.get(sections.size() - 1)
                .getDownStationId();
        return downStationId.equals(id);
    }

    public boolean sectionUpStationInStartPoints(Section section) {
        return sections.stream()
                .anyMatch(s -> s.isSameUpStation(section));
    }

    public boolean sectionDownStationInEndPoints(Section section) {
        return sections.stream()
                .anyMatch(s -> s.isSameDownStation(section));
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
