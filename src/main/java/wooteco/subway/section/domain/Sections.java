package wooteco.subway.section.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import wooteco.subway.exception.RequestException;

public class Sections {

    private static final int FIRST = 0;
    private static final int SECOND = 1;

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public Optional<Section> change(final Section requestedSection) {
        final List<Section> relatedSections = relatedSectionsOf(requestedSection);
        validateAllDifferent(requestedSection, relatedSections);
        validateRelated(requestedSection, relatedSections);

        return relatedSections.stream()
                .filter(section -> section.canJoin(requestedSection))
                .map(section -> section.combineWith(requestedSection))
                .findFirst();
    }

    private List<Section> relatedSectionsOf(final Section requestedSection) {
        return sections.stream()
                .filter(section -> section.isRelated(requestedSection))
                .collect(Collectors.toList());
    }

    private void validateAllDifferent(Section requestedSection, List<Section> relatedSections) {
        for (final Section section : relatedSections) {
            validateDifferent(section, requestedSection);
        }
    }

    private void validateDifferent(Section firstSection, Section secondSection) {
        if (firstSection.hasSameStations(secondSection)) {
            throw new RequestException("이미 두 역을 연결하는 구간이 있습니다.");
        }
    }

    private void validateRelated(final Section requestedSection, final List<Section> relatedSections) {
        if (relatedSections.size() == 2) {
            final Section firstSection = relatedSections.get(FIRST);
            final Section secondSection = relatedSections.get(SECOND);

            validateConnected(firstSection, secondSection);
            validateNoFork(requestedSection, firstSection, secondSection);
        }
    }

    private void validateConnected(final Section firstSection, final Section secondSection) {
        if (connectedFarAway(firstSection, secondSection)) {
            throw new RequestException("이미 노선에서 서로 연결된 역들입니다.");
        }
    }

    private boolean connectedFarAway(final Section firstSection, final Section secondSection) {
        return !firstSection.canConnect(secondSection);
    }

    private void validateNoFork(final Section requestedSection, final Section firstSection, final Section secondSection) {
        if (isForked(requestedSection, firstSection, secondSection)) {
            throw new RequestException("갈래길을 형성할 수 없습니다.");
        }
    }

    private boolean isForked(final Section requestedSection, final Section firstSection, final Section secondSection) {
        return (requestedSection.canJoin(firstSection) && requestedSection.canJoin(secondSection))
                || (requestedSection.canConnect(firstSection) && requestedSection.canConnect(secondSection));
    }

    public List<Section> removeStation(final Long stationId) {
        validateSectionsLength();

        final List<Section> relatedSections = relatedSectionsOf(stationId);
        relatedSections.forEach(sections::remove);
        return Collections.unmodifiableList(relatedSections);
    }

    private void validateSectionsLength() {
        if (sections.size() == 1) {
            throw new RequestException("구간이 하나밖에 없는 노선에서는 역을 제거할 수 없습니다.");
        }
    }

    private List<Section> relatedSectionsOf(final Long stationId) {
        return sections.stream()
                .filter(section -> section.contains(stationId))
                .collect(Collectors.toList());
    }

    public List<Long> toOrderedStationIds() {
        final Map<Long, Long> stationIdMap = createIdMap(sections);
        final Long beginningUpStation = findBeginningUpStation(stationIdMap);

        return createOrderedStationIdList(beginningUpStation, stationIdMap);
    }

    private Map<Long, Long> createIdMap(final List<Section> sections) {
        final Map<Long, Long> idMap = new HashMap<>();
        for (final Section section : sections) {
            idMap.put(section.getUpStationId(), section.getDownStationId());
        }
        return idMap;
    }

    private Long findBeginningUpStation(final Map<Long, Long> stationIdMap) {
        return stationIdMap.keySet().stream()
                .filter(key -> !stationIdMap.containsValue(key))
                .findFirst()
                .orElseThrow(() -> new RequestException("노선의 구간이 올바르게 정렬되지 않았습니다."));
    }

    private List<Long> createOrderedStationIdList(final Long beginningUpStation, final Map<Long, Long> stationIdMap) {
        List<Long> orderedStationIds = new ArrayList<>();
        orderedStationIds.add(beginningUpStation);

        for (int currentIndex = 0; currentIndex < stationIdMap.size(); currentIndex++) {
            final Long currentId = orderedStationIds.get(currentIndex);
            final Long nextId = stationIdMap.get(currentId);
            orderedStationIds.add(nextId);
        }

        return Collections.unmodifiableList(orderedStationIds);
    }
}
