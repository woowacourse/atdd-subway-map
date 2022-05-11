package wooteco.subway.domain;

import static wooteco.subway.domain.SectionAddStatus.ADD_MIDDLE_FROM_DOWN_STATION;
import static wooteco.subway.domain.SectionAddStatus.ADD_MIDDLE_FROM_UP_STATION;
import static wooteco.subway.domain.SectionAddStatus.from;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {

    private static final String ERROR_INVALID_SECTIONS = "[ERROR] 존재하지 않는 구간입니다.";
    private static final String ERROR_ALREADY_CONTAIN = "[ERROR] 추가할 구간 속 지하철역이 기존 구간에 이미 존재합니다.";

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

    public void canAddSection(final Section section) {
        //1. 둘중에 하나만 같아야함. (둘다 기존에 존재시 예외 + (개별구간) 하나도 같지 않다면 예외
        final SectionAddStatus sectionAddStatus = getAddSectionStatus(section);

        //2. 4가지 경우의 수 [중간에 추가되는 2가지] 경우
        if (hasMiddleSection(sectionAddStatus)) {
            //1) 상행이 같아서 middle을 추가하는 경우, 추가section distance vs 기존 sections -> 상행-하행 distance 거리 검증이 필요하다.
            //1-1) 상행or하행 같은 기존section 찾아, 거리 비교하기 
            validateDistance(section, sectionAddStatus);
            // 추가하기
        }

//        if (hasMiddleStation(section, standardStation)) {
        // --> 안쪽이면, 반대station까지의 거리를 비교로 검증해야함.
//            validateDistance(section);
        // ---> 정말 안쪽이면, 구간을 새로 바꿔주야함.
//        }
        // --> 바깥쪽이면, 방향에 맞게 노선의 추가만 해주면 된다?
    }

    private void validateDistance(final Section section, final SectionAddStatus sectionAddStatus) {
        if (sectionAddStatus == ADD_MIDDLE_FROM_UP_STATION) {
            final Section sameUpStationSection = getSameUpStationSection(section);
            checkDistance(section, sameUpStationSection);
        }
        if (sectionAddStatus == ADD_MIDDLE_FROM_DOWN_STATION) {
            final Section sameDownStationSection = getSameDownStationSection(section);
            checkDistance(section, sameDownStationSection);
        }
    }

    private void checkDistance(final Section section, final Section sameStandardStationSection) {
        if (section.getDistance() >= sameStandardStationSection.getDistance()) {
            throw new IllegalStateException("[ERROR] 기존 구간보다 긴 구간을 추가할 순 없습니다.");
        }
    }

    private Section getSameUpStationSection(final Section section) {
        return value.stream()
            .filter(it -> Objects.equals(it.getUpStationId(), section.getUpStationId()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] 해당 상행 종점을 가지는 구간이 존재 하지 않습니다."));
    }

    private Section getSameDownStationSection(final Section section) {
        return value.stream()
            .filter(it -> Objects.equals(it.getDownStationId(), section.getDownStationId()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] 해당 상행 종점을 가지는 구간이 존재 하지 않습니다."));
    }

    private boolean hasMiddleSection(final SectionAddStatus sectionAddStatus) {
        return sectionAddStatus == ADD_MIDDLE_FROM_UP_STATION || sectionAddStatus == ADD_MIDDLE_FROM_DOWN_STATION;
    }

    private Object hasMiddleStation(final Section section, final SectionAddStatus standardStation) {
        if (standardStation.getAddMiddleFromUpStation()) {
            //상행이 서로 같다면,
            // 1) section에서 하행이
            // 1) section에서 상행-하행 중
            // 2) sections 중 상행이 같은 section에서 상행-하행 길이를 비교한다.
//            sections.
        }
        return null;
    }

    private SectionAddStatus getAddSectionStatus(final Section section) {
        final List<Long> uniqueAndSortedStationIds = getUniqueAndSortedStationIds();
        validateSection(uniqueAndSortedStationIds, section);
        //TODO 지금 구간을 1개로 잡았는데, 차후 모든 구간을 대상으로잡아야한다.
        final Long upStationId = uniqueAndSortedStationIds.get(0);
        final Long downStationId = uniqueAndSortedStationIds.get(uniqueAndSortedStationIds.size() - 1);
        return from(section, upStationId, downStationId);
    }

    private void validateSection(final List<Long> stationIds, final Section section) {
        if (stationIds.contains(section.getUpStationId()) && stationIds.contains(section.getDownStationId())) {
            throw new IllegalStateException(ERROR_ALREADY_CONTAIN);
        }
    }

    private List<Long> getUniqueAndSortedStationIds() {
        return this.value.stream()
            .flatMap(it -> Stream.of(it.getUpStationId(), it.getDownStationId()))
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
}
