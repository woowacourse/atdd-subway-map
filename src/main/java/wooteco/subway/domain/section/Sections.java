package wooteco.subway.domain.section;

import static wooteco.subway.domain.section.SectionAddStatus.ADD_MIDDLE_FROM_DOWN_STATION;
import static wooteco.subway.domain.section.SectionAddStatus.ADD_MIDDLE_FROM_UP_STATION;
import static wooteco.subway.domain.section.SectionDeleteStatus.DELETE_DOWN_STATION;
import static wooteco.subway.domain.section.SectionDeleteStatus.DELETE_MIDDLE;
import static wooteco.subway.domain.section.SectionDeleteStatus.DELETE_UP_STATION;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import wooteco.subway.exception.SectionNotFoundException;

public class Sections {

    private static final String ERROR_INVALID_SECTIONS = "[ERROR] 존재하지 않는 구간입니다.";
    private static final String ERROR_ALREADY_CONTAIN = "[ERROR] 추가할 구간 속 지하철역이 기존 구간에 이미 존재합니다.";
    private static final String ERROR_INVALID_DISTANCE = "[ERROR] 기존 구간보다 긴 구간을 추가할 순 없습니다.";
    private static final String ERROR_NO_STATION = "[ERROR] 해당 종점을 가지는 구간이 존재 하지 않습니다.";

    private final List<Section> value;

    public Sections(final List<Section> sections) {
        validateSections(sections);
        this.value = new ArrayList<>(sections);
    }

    private void validateSections(final List<Section> sections) {
        if (sections.size() == 0) {
            throw new IllegalArgumentException(ERROR_INVALID_SECTIONS);
        }
    }

    public List<Section> addSection(final Section section) {
        final SectionAddStatus sectionAddStatus = getAddSectionStatus(section);

        if (hasMiddleSection(sectionAddStatus)) {
            addMiddleSection(section, sectionAddStatus);
            return getSortedByUpStationIdSections();
        }
        value.add(section);
        return getSortedByUpStationIdSections();
    }

    private void addMiddleSection(final Section section, final SectionAddStatus sectionAddStatus) {
        if (sectionAddStatus == ADD_MIDDLE_FROM_UP_STATION) {
            addMiddleSectionFromUpStation(section);
            return;
        }
        addMiddleSectionFromDownStation(section);
    }

    private void addMiddleSectionFromUpStation(final Section section) {
        final Section sameUpStationSection = getSameConditionStationSection(
            it -> Objects.equals(it.getUpStationId(), section.getUpStationId()));
        checkDistance(section, sameUpStationSection);
        value.removeIf(it -> Objects.equals(it.getId(), sameUpStationSection.getId()));
        value.add(section);
        value.add(section.createMiddleToDownSection(sameUpStationSection));
    }

    private void addMiddleSectionFromDownStation(final Section section) {
        final Section sameDownStationSection = getSameConditionStationSection(
            it -> Objects.equals(it.getDownStationId(), section.getDownStationId()));
        checkDistance(section, sameDownStationSection);
        value.removeIf(it -> Objects.equals(it.getId(), sameDownStationSection.getId()));
        value.add(section);
        value.add(section.createUpToMiddleSection(sameDownStationSection));
    }

    private List<Section> getSortedByUpStationIdSections() {
        final List<Section> sections = value.stream()
            .sorted(Comparator.comparing(Section::getUpStationId))
            .collect(Collectors.toList());
        return List.copyOf(sections);
    }

    private void checkDistance(final Section section, final Section sameStandardStationSection) {
        if (section.getDistance() >= sameStandardStationSection.getDistance()) {
            throw new IllegalStateException(ERROR_INVALID_DISTANCE);
        }
    }

    private Section getSameConditionStationSection(final Predicate<Section> sectionPredicate) {
        return value.stream()
            .filter(sectionPredicate)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(ERROR_NO_STATION));
    }

    private boolean hasMiddleSection(final SectionAddStatus sectionAddStatus) {
        return sectionAddStatus == ADD_MIDDLE_FROM_UP_STATION || sectionAddStatus == ADD_MIDDLE_FROM_DOWN_STATION;
    }

    private SectionAddStatus getAddSectionStatus(final Section section) {
        validateSection(getTotalStationIds(), section);
        return SectionAddStatus.from(value, section);
    }

    private void validateSection(final List<Long> stationIds, final Section section) {
        if (stationIds.contains(section.getUpStationId()) && stationIds.contains(section.getDownStationId())) {
            throw new IllegalStateException(ERROR_ALREADY_CONTAIN);
        }
    }

    private List<Long> getTotalStationIds() {
        return this.value.stream()
            .flatMap(it -> Stream.of(it.getUpStationId(), it.getDownStationId()))
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    public Long deleteSectionByStationId(final Long stationId) {
        final SectionDeleteStatus deleteSectionStatus = getDeleteSectionStatus(stationId);
        if (deleteSectionStatus == DELETE_MIDDLE) {
            return deleteMiddle(stationId);
        }
        if (deleteSectionStatus == DELETE_UP_STATION) {
            return deleteUpStation(stationId);
        }
        if (deleteSectionStatus == DELETE_DOWN_STATION) {
            return deleteDownStation(stationId);
        }
        throw new IllegalStateException("[ERROR] 해당 구간을 삭제할 수 없습니다.");
    }

    private Long deleteMiddle(final Long stationId) {
        final Section upToMiddleSection = findSectionByCondition(
            it -> Objects.equals(it.getDownStationId(), stationId));
        final Section middleToDownSection = findSectionByCondition(
            it -> Objects.equals(it.getUpStationId(), stationId));
        value.removeIf(it -> it.equals(upToMiddleSection));
        value.add(
            upToMiddleSection.createUpToDownSection(middleToDownSection));
        value.removeIf(it -> it.equals(middleToDownSection));
        return middleToDownSection.getId();
    }

    private Long deleteUpStation(final Long stationId) {
        final Section firstSection = findSectionByCondition(it -> Objects.equals(it.getUpStationId(), stationId));
        value.removeIf(it -> it.equals(firstSection));
        return firstSection.getId();
    }

    private Long deleteDownStation(final Long stationId) {
        final Section lastSection = findSectionByCondition(
            it -> Objects.equals(it.getDownStationId(), stationId));
        value.removeIf(it -> it.equals(lastSection));
        return lastSection.getId();
    }

    private SectionDeleteStatus getDeleteSectionStatus(final Long stationId) {
        validateDeleteSection(stationId);
        return SectionDeleteStatus.from(value, stationId);
    }

    private void validateDeleteSection(final Long stationId) {
        checkExistingStationId(stationId);
        checkOnlyDefaultSection();
    }

    private void checkExistingStationId(final Long stationId) {
        if (!containsStationId(stationId)) {
            throw new SectionNotFoundException("[ERROR] 해당 이름의 지하철역이 구간내 존재하지 않습니다.");
        }
    }

    public boolean containsStationId(final Long stationId) {
        return getTotalStationIds().contains(stationId);
    }

    private void checkOnlyDefaultSection() {
        if (getTotalStationIds().size() == 2) {
            throw new IllegalStateException("[ERROR] 역 2개의 기본 구간만 존재하므로 더이상 구간 삭제할 수 없습니다.");
        }
    }

    private Section findSectionByCondition(final Predicate<Section> sectionPredicate) {
        return value.stream()
            .filter(sectionPredicate)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("[ERROR] 해당하는 구간이 없습니다."));
    }

    public List<Section> getValue() {
        return value;
    }
}
