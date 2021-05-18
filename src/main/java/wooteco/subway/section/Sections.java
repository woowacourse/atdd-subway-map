package wooteco.subway.section;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import wooteco.subway.exception.section.InvalidSectionOnLineException;
import wooteco.subway.exception.station.NotFoundStationException;
import wooteco.subway.station.Station;

public class Sections {

    private static final int DELETABLE_COUNT = 2;
    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public boolean isBothEndSection(final Section section) {
        Deque<Long> ids = sortedStationIds();

        return Objects.equals(ids.peekFirst(), section.getDownStation().getId())
            || Objects.equals(ids.peekLast(), section.getUpStation().getId());
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
            upStationIds.put(section.getUpStation().getId(), section.getDownStation().getId());
            downStationIds.put(section.getDownStation().getId(), section.getUpStation().getId());
        }

        Section section = sections.get(0);
        stationIds.addFirst(section.getUpStation().getId());
        stationIds.addLast(section.getDownStation().getId());
    }

    private void sortStationsById(Deque<Long> stationIds, Map<Long, Long> upStationIds,
        Map<Long, Long> downStationIds) {
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
        boolean isUpStationExisted = isNotExistOnLine(section.getUpStation());
        boolean isDownStationExisted = isNotExistOnLine(section.getDownStation());

        if (isUpStationExisted == isDownStationExisted) {
            throw new InvalidSectionOnLineException();
        }
    }

    public void validateDeletableCount() {
        if (sections.size() < DELETABLE_COUNT) {
            throw new IllegalStateException("구간을 제거할 수 없습니다.");
        }
    }

    public void validateExistStation(final Station station) {
        if (isNotExistOnLine(station)) {
            throw new NotFoundStationException();
        }
    }

    private boolean isNotExistOnLine(final Station station) {
        boolean isMatchedAtUpStation = sections.stream()
            .anyMatch(it -> station.equals(it.getUpStation()));
        boolean isMatchedAtDownStation = sections.stream()
            .anyMatch(it -> station.equals(it.getDownStation()));

        return !(isMatchedAtUpStation || isMatchedAtDownStation);
    }

    public boolean isNotEmpty() {
        return !sections.isEmpty();
    }

    public Section findByOverlappedStation(final Section section) {
        return sections.stream()
            .filter(
                it -> (section.isIncludeUpStation(it) ||
                    section.isIncludeDownStation(it))
            )
            .findAny()
            .orElseThrow(InvalidSectionOnLineException::new);
    }

    public Section sectionForInterval(final Section section) {
        return sections.stream()
            .filter(
                it -> (section.isSameUpStation(it) ||
                    section.isSameDownStation(it))
            )
            .findAny()
            .orElseThrow(InvalidSectionOnLineException::new);
    }
}

