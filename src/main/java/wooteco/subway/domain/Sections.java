package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {

    private static final String ERROR_INVALID_SECTIONS = "[ERROR] 존재하지 않는 구간입니다.";
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
        checkValidStationOfTargetSection(section);
    }

    private void checkValidStationOfTargetSection(final Section section) {
        final List<Long> uniqueAndSortedStationIds = getUniqueAndSortedStationIds();
        final Long upStationId = uniqueAndSortedStationIds.get(0);
        final Long downStationId = uniqueAndSortedStationIds.get(uniqueAndSortedStationIds.size() - 1);
        if (isOnlyUpStationSame(section, upStationId, downStationId)) {
            return;
        }
        if (isOnlyDownStationSame(section, upStationId, downStationId)) {
            return;
        }
        throw new IllegalStateException("[ERROR] 구간을 추가하기 위해선 상행 종점 혹은 하행 종점 둘 중 하나만 포함한 구간만 가능합니다.");
    }

    private List<Long> getUniqueAndSortedStationIds() {
        return this.value.stream()
            .flatMap(it -> Stream.of(it.getUpStationId(), it.getDownStationId()))
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    private boolean isOnlyUpStationSame(final Section section, final Long upStationId, final Long downStationId) {
        return Objects.equals(section.getUpStationId(), upStationId) &&
            !Objects.equals(section.getDownStationId(), downStationId);
    }

    private boolean isOnlyDownStationSame(final Section section, final Long upStationId, final Long downStationId) {
        return !Objects.equals(section.getUpStationId(), upStationId) &&
            Objects.equals(section.getDownStationId(), downStationId);
    }
}
