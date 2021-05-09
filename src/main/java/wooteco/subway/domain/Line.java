package wooteco.subway.domain;

import wooteco.subway.exception.SubwayException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;

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
        Map<Long, List<Section>> mapWithUpStationIdAndSection = sections.stream()
                .collect(groupingBy(section -> section.getUpStationId()));

        for (Section section : sections) {
            if (mapWithUpStationIdAndSection.get(section.getUpStationId()).size() == UP_STATION_ID_COUNT) {
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
}
