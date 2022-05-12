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

    public Section addSection(Section inputSection) {
        validateSameSection(inputSection);
        if (sections.isEmpty()) {
            sections.add(inputSection);
            return null;
        }
        if (!isEdgeSection(inputSection)) {
            Section section = findAddPoint(inputSection);
            Direction direction = Direction.findDirection(section, inputSection);
            syncSection(section, inputSection, direction);
            sections.add(inputSection);
            return section;
        }
        List<Section> sectionOptions = findConnectableSection(inputSection);
        Section section = findAddPoint(sectionOptions, inputSection);
        Direction direction = Direction.findDirection(section, inputSection);
        syncSection(section, inputSection, direction);
        sections.add(inputSection);
        return section;
    }

    private void validateSameSection(Section inputSection) {
        boolean isSameSection = sections.stream()
                .anyMatch(section -> section.equals(inputSection));

        if (isSameSection) {
            throw new IllegalArgumentException(SAME_SECTION_EXCEPTION);
        }
    }

    private void syncSection(Section section, Section inputSection, Direction direction) {
        if (direction == Direction.BETWEEN_UP) {
            validateDistance(section, inputSection);
            section.update(inputSection.getDownStationId(), section.getDownStationId(),
                    section.getDistance() - inputSection.getDistance());
        }
        if (direction == Direction.BETWEEN_DOWN) {
            validateDistance(section, inputSection);
            section.update(section.getUpStationId(), inputSection.getUpStationId(),
                    section.getDistance() - inputSection.getDistance());
        }
    }

    private void validateDistance(Section section, Section inputSection) {
        if (section.getDistance() <= inputSection.getDistance()) {
            throw new IllegalArgumentException(EXCEED_DISTANCE);
        }
    }

    private Section findAddPoint(Section inputSection) {
        return sections.stream()
                .filter(section -> sections.size() != 0 && section.isExistSameStation(inputSection))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_STATION_IN_LINE));
    }

    private Section findAddPoint(List<Section> sections, Section inputSection) {
        return sections.stream()
                .filter(section -> {
                    Direction direction = Direction.findDirection(section, inputSection);
                    return direction == Direction.BETWEEN_UP || direction == Direction.BETWEEN_DOWN;
                })
                .findAny()
                .orElseThrow();
    }

    private boolean isEdgeSection(Section inputSection) {
        int countOfCoincidence = (int) sections.stream()
                .filter(section -> section.isExistSameStation(inputSection))
                .count();

        return countOfCoincidence == 2;
    }

    private List<Section> findConnectableSection(Section inputSection) {
        return sections.stream()
                .filter(section -> section.isExistSameStation(inputSection))
                .collect(Collectors.toList());
    }

    public Section deleteSection(Long stationId) {
        List<Section> sections = this.sections.stream()
                .filter(section -> (stationId == section.getUpStationId()) || stationId ==
                        section.getDownStationId())
                .collect(Collectors.toList());
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("없어");
        }
        if (sections.size() == 1) {
            deleteEdgeSection(sections);
            return null;
        }
        return deleteCenterSection(sections, stationId);
    }

    private void deleteEdgeSection(List<Section> sections) {
        this.sections.removeAll(sections);
    }

    private Section deleteCenterSection(List<Section> sections, Long stationId) {
        Section section = sections.get(0);
        if (section.getUpStationId() == stationId) {
            Section newSection = new Section(section.getLineId(), sections.get(1).getUpStationId(), section.getDownStationId(),
                    section.getDistance() + sections.get(1).getDistance());
            this.sections.add(newSection);
            return newSection;
        }
        Section newSection = new Section(section.getLineId(), section.getUpStationId(), sections.get(1).getDownStationId(),
                section.getDistance() + sections.get(1).getDistance());
        this.sections.add(newSection);
        this.sections.removeAll(sections);
        return newSection;
    }

    public List<Section> getSections() {
        return List.copyOf(sections);
    }
}
