package wooteco.subway.domain;

import static wooteco.subway.domain.SectionAddStatus.ADD_MIDDLE_FROM_DOWN_STATION;
import static wooteco.subway.domain.SectionAddStatus.ADD_MIDDLE_FROM_UP_STATION;
import static wooteco.subway.domain.SectionDeleteStatus.DELETE_DOWN_STATION;
import static wooteco.subway.domain.SectionDeleteStatus.DELETE_MIDDLE;
import static wooteco.subway.domain.SectionDeleteStatus.DELETE_UP_STATION;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
            final Section sameUpStationSection = getSameUpStationSection(section);
            checkDistance(section, sameUpStationSection);
            value.removeIf(it -> Objects.equals(it.getId(), sameUpStationSection.getId()));
            value.add(section);
            value.add(section.createMiddleToDownSection(sameUpStationSection));
            return;
        }

        final Section sameDownStationSection = getSameDownStationSection(section);
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

    private Section getSameUpStationSection(final Section section) {
        return value.stream()
            .filter(it -> Objects.equals(it.getUpStationId(), section.getUpStationId()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(ERROR_NO_STATION));
    }

    private Section getSameDownStationSection(final Section section) {
        return value.stream()
            .filter(it -> Objects.equals(it.getDownStationId(), section.getDownStationId()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(ERROR_NO_STATION));
    }

    private boolean hasMiddleSection(final SectionAddStatus sectionAddStatus) {
        return sectionAddStatus == ADD_MIDDLE_FROM_UP_STATION || sectionAddStatus == ADD_MIDDLE_FROM_DOWN_STATION;
    }

    private SectionAddStatus getAddSectionStatus(final Section section) {
        final List<Long> totalStationIds = getTotalStationIds();
        validateSection(totalStationIds, section);
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

    public List<Section> deleteSectionByStationId(final Long stationId) {
        validateDeleteSection(stationId);
        final SectionDeleteStatus deleteSectionStatus = getDeleteSectionStatus(stationId);
        // 1. 중간역 제거
        if (deleteSectionStatus == DELETE_MIDDLE) {

        }
        //2. 종점들 제거
        //2-1. 상행종점 제거
        if (deleteSectionStatus == DELETE_UP_STATION) {
            value.removeIf(it -> Objects.equals(it.getUpStationId(), stationId));
        }
        //2-2. 하행종점 제거
        if (deleteSectionStatus == DELETE_DOWN_STATION) {
            value.removeIf(it -> Objects.equals(it.getDownStationId(), stationId));
        }
        return getSortedByUpStationIdSections();
    }

    private void validateDeleteSection(final Long stationId) {
        checkExistingStationId(stationId);
        checkOnlyDefaultSection();
    }

    private SectionDeleteStatus getDeleteSectionStatus(final Long stationId) {
        final List<Long> totalStationIds = getTotalStationIds();
        final Long upStationId = getTotalStationIds().get(0);
        final Long downStationId = getTotalStationIds().get(totalStationIds.size() - 1);
        if (Objects.equals(stationId, upStationId)) {
            return DELETE_UP_STATION;
        }
        if (Objects.equals(stationId, downStationId)) {
            return DELETE_DOWN_STATION;
        }
        return DELETE_MIDDLE;
    }

    private void checkExistingStationId(final Long stationId) {
        if (!containsStationId(stationId)) {
            throw new SectionNotFoundException("[ERROR] 해당 이름의 지하철역이 구간내 존재하지 않습니다.");
        }
    }

    private void checkOnlyDefaultSection() {
        if (getTotalStationIds().size() == 2) {
            throw new IllegalStateException("[ERROR] 역 2개의 기본 구간만 존재하므로 더이상 구간 삭제할 수 없습니다.");
        }
    }

    public boolean containsStationId(final Long stationId) {
        return getTotalStationIds().contains(stationId);
    }
}
