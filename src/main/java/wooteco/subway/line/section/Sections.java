package wooteco.subway.line.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.ValidationFailureException;

public class Sections {

    private static final int MINIMUM_SORTING_COUNT = 2;

    private final List<Section> sectionGroup;

    public Sections(final List<Section> sectionGroup) {
        this.sectionGroup = sort(sectionGroup);
    }

    public List<Long> distinctStationIds() {
        final Set<Long> ids = new LinkedHashSet<>();
        for (Section section : sectionGroup) {
            ids.add(section.getUpStationId());
            ids.add(section.getDownStationId());
        }
        return new ArrayList<>(ids);
    }

    private List<Section> sort(final List<Section> sections) {
        if (sections.size() < MINIMUM_SORTING_COUNT) {
            return sections;
        }

        final Section firstTerminalStation = decideFirstTerminalStation(sections);
        final List<Section> sortedSections = new ArrayList<>();
        sortedSections.add(firstTerminalStation);
        findNextSection(firstTerminalStation, sections, sortedSections);
        return sortedSections;
    }

    private Section decideFirstTerminalStation(final List<Section> sections) {
        final Map<Long, Integer> frequency = calculateFrequency(sections);
        final List<Long> terminalStationIds = findTerminalStationId(frequency);

        return sections.stream()
            .filter(section -> terminalStationIds.contains(section.getUpStationId()))
            .findAny()
            .orElseThrow(() -> new DataNotFoundException("해당하는 상행역이 없습니다."));
    }

    private void findNextSection(final Section current, final List<Section> sections,
        final List<Section> sortedSections) {

        final long downStationId = current.getDownStationId();
        final Optional<Section> nextSection = sections.stream()
            .filter(section -> section.getUpStationId().equals(downStationId))
            .findAny();

        if (!nextSection.isPresent()) {
            return;
        }
        sortedSections.add(nextSection.get());
        findNextSection(nextSection.get(), sections, sortedSections);
    }

    private Map<Long, Integer> calculateFrequency(final List<Section> sections) {
        final Map<Long, Integer> frequency = new HashMap<>();
        for (final Section section : sections) {
            final long upStationId = section.getUpStationId();
            final long downStationId = section.getDownStationId();
            frequency.merge(upStationId, 1, (key, oldValue) -> oldValue + 1);
            frequency.merge(downStationId, 1, (key, oldValue) -> oldValue + 1);
        }
        return frequency;
    }

    private List<Long> findTerminalStationId(final Map<Long, Integer> frequency) {
        final Predicate<Entry<Long, Integer>> terminalStationFilter = entry -> entry.getValue().equals(1);
        return frequency.entrySet()
            .stream()
            .filter(terminalStationFilter)
            .map(Entry::getKey)
            .collect(Collectors.toList());
    }

    public void validateBothExistentStation(final Long upStationId, final Long downStationId) {
        if (containsStation(upStationId) && containsStation(downStationId)) {
            throw new ValidationFailureException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }
    }

    public void validateNoneExistentStation(final Long upStationId, final Long downStationId) {
        if (!containsStation(upStationId) && !containsStation(downStationId)) {
            throw new ValidationFailureException("상행역과 하행역 둘 다 포함되어있지 않습니다.");
        }
    }

    private boolean containsStation(final Long stationId) {
        return distinctStationIds().contains(stationId);
    }

    public boolean isFirstStationId(Long stationId) {
        return getFirstSection().getUpStationId().equals(stationId);
    }

    public Section getFirstSection() {
        return sectionGroup.get(0);
    }

    public boolean isLastStationId(final Long stationId) {
        return getLastSection().getDownStationId().equals(stationId);
    }

    public Section getLastSection() {
        return sectionGroup.get(sectionGroup.size() - 1);
    }

    public Section findSectionHasUpStation(long stationId) {
        return sectionGroup.stream()
            .filter(section -> section.getUpStationId().equals(stationId))
            .findAny()
            .orElseThrow(() -> new DataNotFoundException(
                String.format("해당 역을 상행역으로 가지는 구간을 찾을 수 없습니다. (id:%s)", stationId))
            );
    }

    public Section findSectionHasDownStation(long stationId) {
        return sectionGroup.stream()
            .filter(section -> section.getDownStationId().equals(stationId))
            .findAny()
            .orElseThrow(() -> new DataNotFoundException(
                String.format("해당 역을 하행역으로 가지는 구간을 찾을 수 없습니다. (id:%s)", stationId))
            );
    }

    public Section findSameForm(final Long upStationId, final Long downStationId) {
        final Predicate<Section> upStationFilter = section -> section.getUpStationId().equals(upStationId);
        final Predicate<Section> downStationFilter = section -> section.getDownStationId().equals(downStationId);
        return sectionGroup.stream()
            .filter(upStationFilter.or(downStationFilter))
            .findAny()
            .orElseThrow(() -> new ValidationFailureException("구간을 추가하기에 적합한 곳을 찾지 못했습니다."));
    }

    public int size() {
        return sectionGroup.size();
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
        return Objects.equals(sectionGroup, sections.sectionGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectionGroup);
    }
}
