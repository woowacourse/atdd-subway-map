package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Sections {

    static final String DUPLICATE_STATION_ERROR_MESSAGE = "상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.";
    static final String NONE_DUPLICATE_STATION_ERROR_MESSAGE = "상행역과 하행역 둘 중 하나는 포함되어 있어야 합니다.";
    static final String IMPOSSIBLE_DELETE_EXCEPTION_MESSAGE = "삭제할 구간이 존재하지 않습니다.";
    static final String IMPOSSIBLE_ADDING_EXCEPTION_MESSAGE = "구간을 추가할 수 없습니다.";
    private static final String CANNOT_FIND_SECTION_EXCEPTION_MESSAGE = "해당 구간을 찾을 수 없습니다.";

    private static final int TERMINAL_STATION_NUMBER = 1;
    private static final int MIDDLE_STATION_NUMBER = 2;
    private static final int MINIMUM_STATION_SIZE_TO_DELETE = 2;

    private final List<Section> value;

    public Sections(List<Section> value) {
        this.value = value;
    }

    public Sections add(Section section) {
        validateExistStations(section);
        List<Section> newSections = new ArrayList<>(value);
        for (Section eachSection : value) {
            addWithTerminal(section, newSections, eachSection);
            addWithUpStation(section, newSections, eachSection);
            addWithDownStation(section, newSections, eachSection);
        }
        validateAdding(newSections);
        return new Sections(newSections);
    }

    private void validateExistStations(Section section) {
        Set<Long> stationIds = getStationIds();
        if (stationIds.contains(section.getUpStationId()) && stationIds.contains(section.getDownStationId())) {
            throw new IllegalArgumentException(DUPLICATE_STATION_ERROR_MESSAGE);
        }
        if (!(stationIds.contains(section.getUpStationId()) || stationIds.contains(section.getDownStationId()))) {
            throw new IllegalArgumentException(NONE_DUPLICATE_STATION_ERROR_MESSAGE);
        }
    }

    private void addWithTerminal(Section section, List<Section> newSections, Section eachSection) {
        if (isAddingWithUpTerminal(section, eachSection) || isAddingWithDownTerminal(section, eachSection)) {
            newSections.add(section);
        }
    }

    private boolean isAddingWithUpTerminal(Section section, Section eachSection) {
        return eachSection.isUpTerminal(section) && !getDownStationIds().contains(section.getDownStationId());
    }

    private boolean isAddingWithDownTerminal(Section section, Section eachSection) {
        return eachSection.isDownTerminal(section) && !getUpStationIds().contains(section.getUpStationId());
    }

    private void addWithUpStation(Section section, List<Section> newSections, Section eachSection) {
        if (isAddingWithUpStation(section, eachSection)) {
            newSections.add(section);
            Long newUpStationId = section.getDownStationId();
            Long newDownStationId = eachSection.getDownStationId();
            int newDistance = eachSection.getDistance() - section.getDistance();
            newSections.remove(eachSection);
            Section newSection = new Section(section.getLineId(), newUpStationId, newDownStationId, newDistance);
            newSections.add(newSection);
        }
    }

    private boolean isAddingWithUpStation(Section section, Section eachSection) {
        return eachSection.isSameUpStation(section) && eachSection.isLessThanDistance(section);
    }

    private void addWithDownStation(Section section, List<Section> newSections, Section eachSection) {
        if (isAddingWithDownStation(section, eachSection)) {
            newSections.add(section);
            Long newUpStationId = eachSection.getUpStationId();
            Long newDownStationId = section.getUpStationId();
            int newDistance = eachSection.getDistance() - section.getDistance();
            newSections.remove(eachSection);
            Section newSection = new Section(section.getLineId(), newUpStationId, newDownStationId, newDistance);
            newSections.add(newSection);
        }
    }

    private boolean isAddingWithDownStation(Section section, Section eachSection) {
        return eachSection.isSameDownStation(section) && eachSection.isLessThanDistance(section);
    }

    private void validateAdding(List<Section> newSections) {
        if (Arrays.equals(value.toArray(), newSections.toArray())) {
            throw new IllegalArgumentException(IMPOSSIBLE_ADDING_EXCEPTION_MESSAGE);
        }
    }

    public Sections delete(Long lineId, Long stationId) {
        validatePossibleToDelete(stationId);
        List<Section> newSections = new ArrayList<>(value);
        long duplicateStationCounts = getDuplicateStationCounts(stationId);
        deleteTerminalStation(stationId, newSections, duplicateStationCounts);
        deleteMiddleStation(lineId, stationId, newSections, duplicateStationCounts);
        return new Sections(newSections);
    }

    private void validatePossibleToDelete(Long stationId) {
        Set<Long> stationIds = getStationIds();
        if (!stationIds.contains(stationId) || stationIds.size() == MINIMUM_STATION_SIZE_TO_DELETE) {
            throw new IllegalArgumentException(IMPOSSIBLE_DELETE_EXCEPTION_MESSAGE);
        }
    }

    private long getDuplicateStationCounts(Long stationId) {
        return getStationIdsToList().stream()
                .filter(it -> it == stationId)
                .count();
    }

    private void deleteTerminalStation(Long stationId, List<Section> newSections, long duplicateStationCounts) {
        if (duplicateStationCounts == TERMINAL_STATION_NUMBER) {
            newSections.removeIf(section -> section.hasStation(stationId));
        }
    }

    private void deleteMiddleStation(Long lineId, Long stationId, List<Section> newSections, long duplicateStationCounts) {
        if (duplicateStationCounts == MIDDLE_STATION_NUMBER) {
            Section newSection = makeNewSection(lineId, stationId);
            newSections.removeIf(section -> section.hasStation(stationId));
            newSections.add(newSection);
        }
    }

    private Section makeNewSection(Long lineId, Long stationId) {
        Section sectionWithUpStation = getSectionWithUpStation(stationId);
        Section sectionWithDownStation = getSectionWithDownStation(stationId);

        Long newUpStationId = sectionWithDownStation.getUpStationId();
        Long newDownStationId = sectionWithUpStation.getDownStationId();
        Integer newDistance = sectionWithUpStation.getDistance() + sectionWithDownStation.getDistance();

        return new Section(lineId, newUpStationId, newDownStationId, newDistance);
    }

    private Section getSectionWithUpStation(Long stationId) {
        Section sectionWithUpStation = value.stream()
                .filter(section -> section.hasUpStation(stationId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(CANNOT_FIND_SECTION_EXCEPTION_MESSAGE));
        return sectionWithUpStation;
    }

    private Section getSectionWithDownStation(Long stationId) {
        Section sectionWithDownStation = value.stream()
                .filter(section -> section.hasDownStation(stationId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(CANNOT_FIND_SECTION_EXCEPTION_MESSAGE));
        return sectionWithDownStation;
    }

    private Set<Long> getUpStationIds() {
        return value.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Set<Long> getDownStationIds() {
        return value.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Long> getStationIds() {
        return value.stream()
                .map(Section::getStationId)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableSet());
    }

    public List<Long> getStationIdsToList() {
        return value.stream()
                .map(Section::getStationId)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Section> getValue() {
        return Collections.unmodifiableList(value);
    }

    @Override
    public String toString() {
        return "Sections{" +
                "value=" + value +
                '}';
    }
}
