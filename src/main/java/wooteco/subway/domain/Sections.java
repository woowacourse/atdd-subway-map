package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private static final String SAME_SECTION_EXCEPTION = "동일한 Section은 추가할 수 없습니다.";
    private static final String NOT_EXIST_STATION_IN_LINE = "상행역, 하행역 둘 다 포함되지 않으면 추가할 수 없습니다.";
    private static final String EXCEED_DISTANCE = "새로 들어오는 구간의 거리가 추가될 구간보다 작아야 합니다.";

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public void addSection(Section inputSection) {
        validateSameSection(inputSection);
        if (countSameStation(inputSection) != 2) {
            Section section = findAddPoint(inputSection);
            Direction direction = Direction.findDirection(section, inputSection);
            syncSection(section, inputSection, direction);
            sections.add(inputSection);
            return;
        }
        List<Section> sectionOptions = getSections(inputSection);
        Section section = findSection(sectionOptions, inputSection);
        syncSection(section, inputSection, Direction.findDirection(section, inputSection));
        sections.add(inputSection);
    }

    private void syncSection(Section section, Section inputSection, Direction direction) {
        validateDistance(section, inputSection);
        if (direction == Direction.BETWEEN_UP) {
            Long downStationId = section.getDownStationId();
            int distance = section.getDistance();
            section.update(section.getUpStationId(), inputSection.getDownStationId(),
                    inputSection.getDistance());
            inputSection.update(inputSection.getDownStationId(), downStationId, distance - inputSection.getDistance());
        }
        if (direction == Direction.BETWEEN_DOWN) {
            section.update(section.getUpStationId(), inputSection.getUpStationId(),
                    section.getDistance() - inputSection.getDistance());
            inputSection.update(section.getDownStationId(), inputSection.getDownStationId(), inputSection.getDistance());
        }
    }

    private void validateSameSection(Section inputSection) {
        boolean isSameSection = sections.stream()
                .anyMatch(section -> section.equals(inputSection));

        if (isSameSection) {
            throw new IllegalArgumentException(SAME_SECTION_EXCEPTION);
        }
    }

    private Section findAddPoint(Section inputSection) {
        return sections.stream()
                .filter(section -> section.isExistSameStation(inputSection))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_STATION_IN_LINE));
    }

    private int countSameStation(Section inputSection) {
        return (int) sections.stream()
                .filter(section -> section.isExistSameStation(inputSection))
                .count();
    }

    private List<Section> getSections(Section inputSection) {
        return sections.stream()
                .filter(section -> section.isExistSameStation(inputSection))
                .collect(Collectors.toList());
    }

    private Section findSection(List<Section> sections, Section inputSection) {
        return sections.stream()
                .filter(section -> {
                    Direction direction = Direction.findDirection(section, inputSection);
                    return direction != Direction.UP || direction != Direction.DOWN;
                })
                .findAny()
                .orElseThrow();
    }

    private void validateDistance(Section section, Section inputSection) {
        if (section.getDistance() <= inputSection.getDistance()) {
            throw new IllegalArgumentException(EXCEED_DISTANCE);
        }
    }

    public List<Section> getSections() {
        return List.copyOf(sections);
    }
}
