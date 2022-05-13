package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import wooteco.subway.exception.IllegalInputException;
import wooteco.subway.exception.section.NoSuchSectionException;

public class Sections {

    private static final int MIN_SECTION_SIZE = 1;
    private static final int MERGE_REQUIRED_SIZE = 2;

    private final List<Section> value;

    public Sections(final List<Section> value) {
        this.value = value;
    }

    public Sections findDeletableSections(final Long stationIdToDelete) {
        validateMinSize();
        final List<Section> deletableSections = value
                .stream()
                .filter(it -> it.contains(stationIdToDelete))
                .collect(Collectors.toList());
        if (deletableSections.isEmpty()) {
            throw new NoSuchSectionException();
        }
        return new Sections(deletableSections);
    }

    private void validateMinSize() {
        if (value.size() == MIN_SECTION_SIZE) {
            throw new IllegalInputException("구간을 삭제할 수 없습니다.");
        }
    }

    public boolean needMerge() {
        return value.size() == MERGE_REQUIRED_SIZE;
    }

    public Section toMergedSection() {
        final Section first = value.get(0);
        final Section second = value.get(1);
        return first.merge(second);
    }

    public List<Long> toStationIds() {
        Long upStationId = findEndUpStation();

        final List<Long> stationIds = new ArrayList<>();
        stationIds.add(upStationId);
        while (stationIds.size() != value.size() + 1) {
            final Section section = findSectionByUpStationId(upStationId);
            upStationId = section.getDownStationId();
            stationIds.add(upStationId);
        }
        return stationIds;
    }

    private Long findEndUpStation() {
        if (value.size() < MIN_SECTION_SIZE) {
            throw new NoSuchSectionException();
        }
        final List<Long> upStationIds = value.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
        final List<Long> downStationIds = value.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());
        upStationIds.removeAll(downStationIds);
        return upStationIds.get(0);
    }

    private Section findSectionByUpStationId(final Long upStationId) {
        return value
                .stream()
                .filter(it -> it.hasSameUpStationId(upStationId))
                .findFirst()
                .orElseThrow(NoSuchSectionException::new);
    }

    public List<Long> findAllId() {
        return value.stream()
                .map(Section::getId)
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Sections sections = (Sections) o;
        return Objects.equals(value, sections.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
