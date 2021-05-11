package wooteco.subway.section;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import wooteco.subway.exception.section.InvalidSectionOnLineException;
import wooteco.subway.exception.station.NotFoundStationException;

public class Sections {

    private static final int DELETABLE_COUNT = 2;
    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public boolean isBothEndSection(final Section section) {
        Deque<Long> ids = sortedStationIds();

        return Objects.equals(ids.peekFirst(), section.getDownStationId())
            || Objects.equals(ids.peekLast(), section.getUpStationId());
    }

    public boolean isBothEndStation(final Long stationId) {
        return stationId.equals(sortedStationIds().peekFirst())
            || stationId.equals(sortedStationIds().peekLast());
    }

    public Deque<Long> sortedStationIds() {
        Deque<Long> stationIds = new ArrayDeque<>();
        Map<Long, Long> upStationIds = new LinkedHashMap<>();
        Map<Long, Long> downStationIds = new LinkedHashMap<>();

        initStationIds(stationIds, upStationIds, downStationIds);
        sortStationsById(stationIds, upStationIds, downStationIds);
        return new ArrayDeque<>(stationIds);
    }

    private void initStationIds(Deque<Long> stationIds, Map<Long, Long> upStationIds,
        Map<Long, Long> downStationIds) {
        for (Section section : sections) {
            upStationIds.put(section.getUpStationId(), section.getDownStationId());
            downStationIds.put(section.getDownStationId(), section.getUpStationId());
        }

        Section section = sections.get(0);
        stationIds.addFirst(section.getUpStationId());
        stationIds.addLast(section.getDownStationId());
    }

    private void sortStationsById(Deque<Long> stationIds, Map<Long, Long> upStationIds, Map<Long, Long> downStationIds) {
        while (upStationIds.containsKey(stationIds.peekLast())) {
            Long id = stationIds.peekLast();
            stationIds.addLast(upStationIds.get(id));
        }

        while (downStationIds.containsKey(stationIds.peekFirst())) {
            Long id = stationIds.peekFirst();
            stationIds.addFirst(downStationIds.get(id));
        }
    }

    public void insertAvailable(final Section section) {
        boolean isUpStationExisted = isNotExistOnLine(section.getUpStationId());
        boolean isDownStationExisted = isNotExistOnLine(section.getDownStationId());

        if (isUpStationExisted == isDownStationExisted) {
            throw new InvalidSectionOnLineException();
        }
    }

    public void validateDeletableCount() {
        if (sections.size() < DELETABLE_COUNT) {
            throw new IllegalStateException("구간을 제거할 수 없습니다.");
        }
    }

    public void validateExistStation(final Long stationId) {
        if (isNotExistOnLine(stationId)) {
            throw new NotFoundStationException();
        }
    }

    private boolean isNotExistOnLine(final Long stationId) {
        boolean isMatchedAtUpStation = sections.stream()
            .anyMatch(it -> stationId.equals(it.getUpStationId()));
        boolean isMatchedAtDownStation = sections.stream()
            .anyMatch(it -> stationId.equals(it.getDownStationId()));

        return !(isMatchedAtUpStation || isMatchedAtDownStation);
    }

    public boolean isNotEmpty() {
        return !sections.isEmpty();
    }

    public Section findByStationId(final Section section) {
        return sections.stream()
            .filter(
                it -> section.getUpStationId().equals(it.getUpStationId()) ||
                    section.getDownStationId().equals(it.getDownStationId())
            )
            .findAny()
            .orElseThrow(InvalidSectionOnLineException::new);
    }
}

