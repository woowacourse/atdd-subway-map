package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections {

    private static final String SAME_SECTION_EXCEPTION = "동일한 Section은 추가할 수 없습니다.";
    private static final String NOT_EXIST_STATION_IN_LINE = "상행역, 하행역 둘 다 포함되지 않으면 추가할 수 없습니다.";
    private static final String EXCEED_DISTANCE = "새로 들어오는 구간의 거리가 추가될 구간보다 작아야 합니다.";
    private static final String NON_REMOVABLE_EXCEPTION = "노선에 역이 2개 존재하는 경우는 삭제할 수 없습니다.";

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public Optional<Section> addSection(Section inputSection) {
        validateSameSection(inputSection);
        if (sections.isEmpty()) {
            sections.add(inputSection);
            return Optional.empty();
        }
        if (!isEdgeSection(inputSection)) {
            Section connectedSection = findAddPoint(inputSection);
            Direction direction = Direction.findDirection(connectedSection, inputSection);
            syncSection(connectedSection, inputSection, direction);
            sections.add(inputSection);
            return Optional.of(connectedSection);
        }
        List<Section> sectionOptions = findConnectableSection(inputSection);
        Section connectedSection = findAddPoint(sectionOptions, inputSection);
        Direction direction = Direction.findDirection(connectedSection, inputSection);
        syncSection(connectedSection, inputSection, direction);
        sections.add(inputSection);
        return Optional.of(connectedSection);
    }

    private void validateSameSection(Section inputSection) {
        boolean isSameSection = sections.stream()
                .anyMatch(section -> isSameAllStations(section, inputSection));

        if (isSameSection) {
            throw new IllegalArgumentException(SAME_SECTION_EXCEPTION);
        }
    }

    private boolean isSameAllStations(Section section, Section inputSection) {
        List<Long> sectionStations = List.of(section.getUpStationId(), section.getDownStationId());
        List<Long> inputSectionStations = List.of(inputSection.getUpStationId(), inputSection.getDownStationId());

        return sectionStations.containsAll(inputSectionStations);
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

    public Optional<Section> deleteSection(Long stationId) {
        if (sections.size() == 1) {
            throw new IllegalArgumentException(NON_REMOVABLE_EXCEPTION);
        }
        List<Section> overlapSections = sections.stream()
                .filter(section -> (stationId == section.getUpStationId()) || stationId ==
                        section.getDownStationId())
                .collect(Collectors.toList());

        if (overlapSections.size() == 1) {
            sections.removeAll(overlapSections);
            return Optional.empty();
        }

        return Optional.of(deleteCenterSection(overlapSections, stationId));
    }


    private Section deleteCenterSection(List<Section> overlapSections, Long stationId) {
        Section section = overlapSections.get(0);

        if (section.getUpStationId() == stationId) {
            Section newSection = new Section(section.getLineId(), overlapSections.get(1).getUpStationId(), section.getDownStationId(),
                    section.getDistance() + overlapSections.get(1).getDistance());
            sections.add(newSection);
            sections.removeAll(overlapSections);
            return newSection;
        }
        Section newSection = new Section(section.getLineId(), section.getUpStationId(), overlapSections.get(1).getDownStationId(),
                section.getDistance() + overlapSections.get(1).getDistance());
        sections.add(newSection);
        sections.removeAll(overlapSections);
        return newSection;
    }

    public List<Section> getSections() {
        return List.copyOf(sections);
    }
}
