package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import wooteco.subway.exception.SubwayException;

public class Line {

    private static final int UP_STATION_ID_COUNT = 1;

    private final Long id;
    private final String name;
    private final String color;
    private final List<Section> sections;

    public Line(Line line, List<Section> sections) {
        this(line.getId(), line.getName(), line.getColor(), new ArrayList<>(sections));
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, null);
    }

    public Line(Long id, String name, String color, List<Section> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public List<Section> sortSections() {
        List<Section> sortedSections = new ArrayList<>();
        Section firstSection = findFirstSection();
        sortedSections.add(firstSection);
        Long nextStationId = firstSection.getDownStationId();
        while (true) {
            Optional<Section> section = findSectionByUpStationId(nextStationId);
            if (!section.isPresent()) {
                return sortedSections;
            }
            section.get();
            sortedSections.add(section.get());
            nextStationId = section.get().getDownStationId();
        }
    }

    private Section findFirstSection() {
        List<Long> stationIds = new ArrayList<>();
        for (Section section : sections) {
            Long downStationId = section.getDownStationId();
            Long upStationId = section.getUpStationId();
            stationIds.add(downStationId);
            stationIds.add(upStationId);
        }

        for (Section section : sections) {
            int count = Collections.frequency(stationIds, section.getUpStationId());
            if (count == UP_STATION_ID_COUNT) {
                return section;
            }
        }
        throw new SubwayException("Line에 Section이 존재하지 않습니다.");
    }

    private Optional<Section> findSectionByUpStationId(Long upStationId) {
        return sections.stream()
            .filter(section -> section.getUpStationId() == upStationId)
            .findFirst();
    }

    public void validateCreateSectionInLine(Long upStationId, Long downStationId) {
        if (!containsStationInLine(upStationId) && !containsStationInLine(downStationId)) {
            throw new SubwayException("상행역과 하행역 둘 중 하나도 라인에 포함되어 있지 않으면 구간을 추가할 수 없습니다.");
        }

        if (containsStationInLine(upStationId) && containsStationInLine(downStationId)) {
            throw new SubwayException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
        }
    }

    private boolean containsStationInLine(Long stationId) {
        Optional<Long> sectionWhichHasUpStation = sections.stream().map(section -> section.getUpStationId())
            .filter(id -> id.equals(stationId))
            .findFirst();
        Optional<Long> sectionWhichHasDownStation = sections.stream().map(section -> section.getDownStationId())
            .filter(id -> id.equals(stationId))
            .findFirst();
        return sectionWhichHasUpStation.isPresent() || sectionWhichHasDownStation.isPresent();
    }
}
