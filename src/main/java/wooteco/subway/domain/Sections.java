package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Sections {

    static final String DUPLICATE_STATION_ERROR_MESSAGE = "상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.";
    static final String NONE_DUPLICATE_STATION_ERROR_MESSAGE = "상행역과 하행역 둘 중 하나는 포함되어 있어야 합니다.";
    static final String IMPOSSIBLE_DELETE_EXCEPTION_MESSAGE = "삭제할 구간이 존재하지 않습니다.";
    static final String IMPOSSIBLE_ADDING_EXCEPTION_MESSAGE = "구간을 추가할 수 없습니다.";
    private static final String CANNOT_FIND_SECTION_EXCEPTION_MESSAGE = "해당 구간을 찾을 수 없습니다.";

    private static final int FIRST_SECTION_INDEX = 0;
    private static final int TERMINAL_STATION_NUMBER = 1;
    private static final int MIDDLE_STATION_NUMBER = 2;
    private static final int MINIMUM_STATION_SIZE_TO_DELETE = 2;

    private final List<Section> value;

    public Sections(List<Section> value) {
        this.value = sort(value);
    }

    private List<Section> sort(List<Section> value) {
        List<Section> sortedSections = new ArrayList<>();
        sortedSections.add(value.get(FIRST_SECTION_INDEX));

        extendToUp(sortedSections, value);
        extendToDown(sortedSections, value);

        return sortedSections;
    }

    private void extendToUp(List<Section> orderedSections, List<Section> value) {
        int lastSectionIndex = orderedSections.size() - 1;
        Section upTerminalSection = orderedSections.get(lastSectionIndex);

        Optional<Section> newUpTerminalSection = value.stream()
                .filter(upTerminalSection::canLinkWithDownStation)
                .findAny();

        if (newUpTerminalSection.isPresent()) {
            orderedSections.add(newUpTerminalSection.get());
            extendToUp(orderedSections, value);
        }
    }

    private void extendToDown(List<Section> orderedSections, List<Section> value) {
        Section downTerminalSection = orderedSections.get(FIRST_SECTION_INDEX);

        Optional<Section> newDownTerminalSection = value.stream()
                .filter(downTerminalSection::canLinkWithUpStation)
                .findAny();

        if (newDownTerminalSection.isPresent()) {
            orderedSections.add(FIRST_SECTION_INDEX, newDownTerminalSection.get());
            extendToDown(orderedSections, value);
        }
    }

    public Sections add(Section section) {
        validateExistStations(section);
        List<Section> newSections = new ArrayList<>(value);
        for (Section eachSection : value) {
            extendTerminalStation(newSections, section, eachSection);
            extendMiddleStationWithUpStation(newSections, section, eachSection);
            extendMiddleStationWithDownStation(newSections, section, eachSection);
        }
        validateExtension(newSections);
        return new Sections(newSections);
    }

    private void validateExistStations(Section section) {
        List<Long> stationIds = getStationIds();
        if (stationIds.contains(section.getUpStationId()) && stationIds.contains(section.getDownStationId())) {
            throw new IllegalArgumentException(DUPLICATE_STATION_ERROR_MESSAGE);
        }
        if (!(stationIds.contains(section.getUpStationId()) || stationIds.contains(section.getDownStationId()))) {
            throw new IllegalArgumentException(NONE_DUPLICATE_STATION_ERROR_MESSAGE);
        }
    }

    private void extendTerminalStation(List<Section> newSections, Section section, Section eachSection) {
        if (isExtensionToUp(section, eachSection) || isExtensionToDown(section, eachSection)) {
            newSections.add(section);
        }
    }

    private boolean isExtensionToUp(Section section, Section eachSection) {
        Set<Long> downStationIds = value.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toUnmodifiableSet());
        return eachSection.canLinkWithUpStation(section) && !downStationIds.contains(section.getDownStationId());
    }

    private boolean isExtensionToDown(Section section, Section eachSection) {
        Set<Long> upStationIds = value.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toUnmodifiableSet());
        return eachSection.canLinkWithDownStation(section) && !upStationIds.contains(section.getUpStationId());
    }

    private void extendMiddleStationWithUpStation(List<Section> newSections, Section section, Section eachSection) {
        if (isExtensionWithUpStation(section, eachSection)) {
            newSections.add(section);
            Long newUpStationId = section.getDownStationId();
            Long newDownStationId = eachSection.getDownStationId();
            int newDistance = eachSection.getDistance() - section.getDistance();
            newSections.remove(eachSection);
            Section newSection = new Section(section.getLineId(), newUpStationId, newDownStationId, newDistance);
            newSections.add(newSection);
        }
    }

    private boolean isExtensionWithUpStation(Section section, Section eachSection) {
        return eachSection.isSameUpStation(section) && eachSection.isLessThanDistance(section);
    }

    private void extendMiddleStationWithDownStation(List<Section> newSections, Section section, Section eachSection) {
        if (isExtensionWithDownStation(section, eachSection)) {
            newSections.add(section);
            Long newUpStationId = eachSection.getUpStationId();
            Long newDownStationId = section.getUpStationId();
            int newDistance = eachSection.getDistance() - section.getDistance();
            newSections.remove(eachSection);
            Section newSection = new Section(section.getLineId(), newUpStationId, newDownStationId, newDistance);
            newSections.add(newSection);
        }
    }

    private boolean isExtensionWithDownStation(Section section, Section eachSection) {
        return eachSection.isSameDownStation(section) && eachSection.isLessThanDistance(section);
    }

    private void validateExtension(List<Section> newSections) {
        if (Arrays.equals(value.toArray(), newSections.toArray())) {
            throw new IllegalArgumentException(IMPOSSIBLE_ADDING_EXCEPTION_MESSAGE);
        }
    }

    public Sections delete(Long lineId, Long stationId) {
        validatePossibleToDelete(stationId);
        List<Section> newSections = new ArrayList<>(value);
        deleteTerminalStation(newSections, stationId);
        deleteMiddleStation(newSections, lineId, stationId);
        return new Sections(newSections);
    }

    private void validatePossibleToDelete(Long stationId) {
        List<Long> stationIds = getStationIds();
        if (!stationIds.contains(stationId) || stationIds.size() == MINIMUM_STATION_SIZE_TO_DELETE) {
            throw new IllegalArgumentException(IMPOSSIBLE_DELETE_EXCEPTION_MESSAGE);
        }
    }

    private void deleteTerminalStation(List<Section> newSections, Long stationId) {
        if (getDuplicateStationCounts(stationId) == TERMINAL_STATION_NUMBER) {
            newSections.removeIf(section -> section.hasStation(stationId));
        }
    }

    private void deleteMiddleStation(List<Section> newSections, Long lineId, Long stationId) {
        if (getDuplicateStationCounts(stationId) == MIDDLE_STATION_NUMBER) {
            Section linkSection = linkSection(lineId, stationId);
            newSections.removeIf(section -> section.hasStation(stationId));
            newSections.add(linkSection);
        }
    }

    private long getDuplicateStationCounts(Long stationId) {
        return value.stream()
                .map(Section::getStationIds)
                .flatMap(Collection::stream)
                .filter(it -> it.equals(stationId))
                .count();
    }

    private Section linkSection(Long lineId, Long stationId) {
        Section sectionWithUpStation = getSectionHasSameUpStation(stationId);
        Section sectionWithDownStation = getSectionHasSameDownStation(stationId);

        Long newUpStationId = sectionWithDownStation.getUpStationId();
        Long newDownStationId = sectionWithUpStation.getDownStationId();
        Integer newDistance = sectionWithUpStation.getDistance() + sectionWithDownStation.getDistance();

        return new Section(lineId, newUpStationId, newDownStationId, newDistance);
    }

    private Section getSectionHasSameUpStation(Long stationId) {
        return value.stream()
                .filter(section -> section.hasUpStation(stationId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(CANNOT_FIND_SECTION_EXCEPTION_MESSAGE));
    }

    private Section getSectionHasSameDownStation(Long stationId) {
        return value.stream()
                .filter(section -> section.hasDownStation(stationId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(CANNOT_FIND_SECTION_EXCEPTION_MESSAGE));
    }

    public List<Long> getStationIds() {
        return value.stream()
                .map(Section::getStationIds)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
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
