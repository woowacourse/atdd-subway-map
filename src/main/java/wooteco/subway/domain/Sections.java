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

    public Long deleteSectionByStationId(final Long stationId) {
        validateDeleteSection(stationId);
        final SectionDeleteStatus deleteSectionStatus = getDeleteSectionStatus(stationId);
        // 1. 중간역 제거
        if (deleteSectionStatus == DELETE_MIDDLE) {
            //TODO up--middle(stationId)  (stationId)middle--down -> 둘다 삭제
            //     up---- down -> 1개는 생성 => 둘 중에 1개는 따로 삭제되어야한다 => 따로 service로 반환
            final Section upToMiddleSection = findSectionByCondition(
                it -> Objects.equals(it.getDownStationId(), stationId));
            final Section middleToDownSection = findSectionByCondition(
                it -> Objects.equals(it.getUpStationId(), stationId));
//            value.removeIf(it -> Objects.equals(it.getUpStationId(), stationId)); //middle--down은 삭제
//            value.removeIf(it -> Objects.equals(it.getDownStationId(), stationId)); //up--middle도 삭제?
            // -> section으로 안찾고 바로 list에서 삭제하면... sectionId보유해서 한놈은 수정생성 /한놈은 진짜 삭제 가 안된다.
            value.removeIf(it -> it.equals(upToMiddleSection)); // 이놈은 id살려서 생성하자
            value.add(
                upToMiddleSection.createUpToDownSection(middleToDownSection)); //거리 합해서 생성해야하므로 2 section 비교로 섹션 생성~
            value.removeIf(it -> it.equals(middleToDownSection)); // 이놈은 삭제하는 id로서 반환해야한다..
            return middleToDownSection.getId();
        }
        //2. 종점들 제거
        //2-1. 상행종점의 구간 제거
        if (deleteSectionStatus == DELETE_UP_STATION) {
            final Section firstSection = findSectionByCondition(it -> Objects.equals(it.getUpStationId(), stationId));
            value.removeIf(it -> it.equals(firstSection));
            return firstSection.getId();
        }
        //2-2. 하행종점의 구간 제거
        if (deleteSectionStatus == DELETE_DOWN_STATION) {
            final Section lastSection = findSectionByCondition(
                it -> Objects.equals(it.getDownStationId(), stationId));
            value.removeIf(it -> it.equals(lastSection));
            return lastSection.getId();
        }
        //각 if에서 return해줬으면.. 맨 마지막에 조건없는 곳엔 thr 던져주기
        throw new IllegalStateException("[ERROR] 해당 구간을 삭제할 수 없습니다.");
    }

    private Section findSectionByCondition(final Predicate<Section> sectionPredicate) {
        return value.stream()
            .filter(sectionPredicate)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("[ERROR] 해당하는 구간이 없습니다."));
    }

//    private Section findUpToMiddleSection(final Long stationId) {
//        return value.stream()
//            .filter(it -> Objects.equals(it.getDownStationId(), stationId))
//            .findFirst()
//            .orElseThrow(() -> new IllegalStateException("[ERROR] 해당하는 구간이 없습니다."));
//    }
//
//    private Section findMiddleToDownSection(final Long stationId) {
//        return value.stream()
//            .filter(it -> Objects.equals(it.getUpStationId(), stationId))
//            .findFirst()
//            .orElseThrow(() -> new IllegalStateException("[ERROR] 해당하는 구간이 없습니다."));
//    }

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

    public List<Section> getValue() {
        return value;
    }
}
