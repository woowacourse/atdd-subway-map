package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import wooteco.subway.exception.ExceptionMessage;

public class Sections {
    private static final int MINIMUM_SECTIONS_FOR_DELETE = 2;
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections of(Section... sections) {
        return new Sections(Arrays.asList(sections));
    }

    public Optional<Section> getDividedSectionsFrom(Section section) {
        checkInsertSectionsStations(section);
        return findDividableSection(section);
    }

    private Optional<Section> findDividableSection(Section section) {
        return sections.stream()
                .filter(it -> it.isForDivide(section))
                .map(it -> it.divideFrom(section))
                .findFirst();
    }

    private void checkInsertSectionsStations(Section section) {
        if (isAlreadyConnected(section)) {
            throw new IllegalArgumentException(ExceptionMessage.INSERT_DUPLICATED_SECTION.getContent());
        }
        if (unableConnect(section)) {
            throw new IllegalArgumentException(ExceptionMessage.INSERT_SECTION_NOT_MATCH.getContent());
        }
    }

    private boolean unableConnect(Section section) {
        List<Long> stationIds = findStationIds();
        return !stationIds.contains(section.getDownStationId())
                && !stationIds.contains(section.getUpStationId());
    }

    private boolean isAlreadyConnected(Section section) {
        List<Long> stationIds = findStationIds();
        return stationIds.contains(section.getDownStationId()) && stationIds.contains(section.getUpStationId());
    }

    public List<Long> findStationIds() {
        Set<Long> ids = new HashSet<>();
        for (Section section : sections) {
            ids.add(section.getUpStationId());
            ids.add(section.getDownStationId());
        }
        return new ArrayList<>(ids);
    }

    public List<Section> findDeletableByStationId(Long stationId) {
        if (sections.size() < MINIMUM_SECTIONS_FOR_DELETE) {
            throw new IllegalArgumentException(ExceptionMessage.SECTIONS_NOT_DELETABLE.getContent());
        }
        return sections.stream()
                .filter(it -> it.hasStation(stationId))
                .collect(Collectors.toList());
    }
}
