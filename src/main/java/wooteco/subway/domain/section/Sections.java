package wooteco.subway.domain.section;

import org.springframework.http.HttpStatus;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.SubwayException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections = new ArrayList<>();

    public Sections(List<Section> unOrderedSections) {
        Long upwardTerminalStationId = findUpwardTerminalId(unOrderedSections);
        sortSections(unOrderedSections, upwardTerminalStationId);
    }

    private Long findUpwardTerminalId(List<Section> unOrderedSections) {
        List<Long> upStationIds = unOrderedSections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());

        List<Long> downStationIds = unOrderedSections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());

        upStationIds.removeAll(downStationIds);
        return upStationIds.get(0);
    }

    private void sortSections(List<Section> unOrderedSections, Long stationId) {
        Section section;
        Long nextStationId = stationId;
        do {
            section = findSection(unOrderedSections, nextStationId);
            sections.add(section);
            nextStationId = section.getDownStationId();
        } while (hasNextDownwardSection(unOrderedSections, nextStationId));
    }

    private Section findSection(List<Section> unOrderedSections, Long terminalId) {
        return unOrderedSections.stream()
                .filter(section -> section.hasUpwardStation(terminalId))
                .findFirst()
                .get();
    }

    private boolean hasNextDownwardSection(List<Section> unOrderedSections, Long downStationId) {
        return unOrderedSections.stream()
                .anyMatch(section -> section.hasUpwardStation(downStationId));
    }

    public void validateIfPossibleToInsert(Section addedSection) {
        validateIfAlreadyExistsInLine(addedSection);
        validateIfBothStationNotExistsInLine(addedSection);
        validateDistance(addedSection);
    }

    private void validateIfAlreadyExistsInLine(Section addedSection) {
        if (isBothStationExistsInLine(addedSection)) {
            throw new SubwayException(HttpStatus.BAD_REQUEST, "두 역이 이미 노선에 등록되어 있습니다.");
        }
    }

    private boolean isBothStationExistsInLine(Section addedSection) {
        return isStationExists(addedSection.getUpStationId()) && isStationExists(addedSection.getDownStationId());
    }

    private boolean isStationExists(Long stationId) {
        boolean upwardExistence = sections.stream()
                .anyMatch(section -> section.hasUpwardStation(stationId));
        boolean downwardExistence = sections.stream()
                .anyMatch(section -> section.hasDownwardStation(stationId));

        return upwardExistence || downwardExistence;
    }

    private void validateIfBothStationNotExistsInLine(Section addedSection) {
        if (!isStationExists(addedSection.getUpStationId()) && !isStationExists(addedSection.getDownStationId())) {
            throw new SubwayException(HttpStatus.BAD_REQUEST, "노선에 역들이 존재하지 않습니다.");
        }
    }

    public void validateIfPossibleToDelete() {
        if (isNotRemovable()) {
            throw new SubwayException(HttpStatus.BAD_REQUEST, "구간이 하나뿐이므로 삭제 불가능합니다.");
        }
    }

    private boolean isNotRemovable() {
        return this.sections.size() == 1;
    }

    public boolean hasStationAsDownward(Long stationId) {
        return this.sections.stream()
                .anyMatch(section -> section.hasDownwardStation(stationId));
    }

    public boolean hasStationAsUpward(Long stationId) {
        return this.sections.stream()
                .anyMatch(section -> section.hasUpwardStation(stationId));
    }

    public Section createMergedSectionAfterDeletion(Long stationId) {
        Section upwardSection = getUpwardSection(stationId);
        Section downwardSection = getDownwardSection(stationId);
        return new Section(upwardSection.getUpwardStation(), downwardSection.getDownwardStation(), calculateNewDistanceAfterDeletion(stationId));
    }

    private int calculateNewDistanceAfterDeletion(Long stationId) {
        int upwardSectionDistance = getUpwardSection(stationId).getDistance();
        int downwardSectionDistance = getDownwardSection(stationId).getDistance();
        return upwardSectionDistance + downwardSectionDistance;
    }

    private Section getUpwardSection(Long stationId) {
        return sections.stream()
                .filter(section -> section.hasDownwardStation(stationId))
                .findAny()
                .get();
    }

    private Section getDownwardSection(Long stationId) {
        return sections.stream()
                .filter(section -> section.hasUpwardStation(stationId))
                .findAny()
                .get();
    }

    public Section getBottomSection() {
        return sections.get(sections.size() - 1);
    }

    public Section getTopSection() {
        return sections.get(0);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        for (Section section : this.sections) {
            extractStation(stations, section);
        }
        return stations;
    }

    private void extractStation(List<Station> stations, Section section) {
        if (!stations.contains(section.getUpwardStation())) {
            stations.add(section.getUpwardStation());
        }
        stations.add(section.getDownwardStation());
    }

    private void validateDistance(Section addedSection) {
        if (!isSideInsertion(addedSection)) {
            compare(addedSection);
        }
    }

    private boolean isSideInsertion(Section addedSection) {
        if (addedSection.hasDownwardStation(getTopSection().getUpStationId())) {
            return true;
        }
        return addedSection.hasUpwardStation(getBottomSection().getDownStationId());
    }

    private void compare(Section addedSection) {
        Section existingSection = null;
        if (isNewStationUpward(addedSection)) {
            existingSection = getUpwardSection(addedSection.getDownStationId());
        }

        if (isNewStationDownward(addedSection)) {
            existingSection = getDownwardSection(addedSection.getUpStationId());
        }
        compareDistance(addedSection, existingSection);
    }

    private boolean isNewStationUpward(Section addedSection) {
        return sections.stream()
                .anyMatch(section -> section.hasDownwardStation(addedSection.getDownStationId()));
    }

    public boolean isNewStationDownward(Section addedSection) {
        return sections.stream()
                .anyMatch(section -> section.hasUpwardStation(addedSection.getUpStationId()));
    }

    private void compareDistance(Section addedSection, Section existingSection) {
        if (addedSection.hasLongerDistanceThan(existingSection)) {
            throw new SubwayException(HttpStatus.BAD_REQUEST, "추가되는 구간의 거리는 기존 구간보다 클 수 없습니다.");
        }
    }
}