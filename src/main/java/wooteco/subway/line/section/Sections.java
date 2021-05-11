package wooteco.subway.line.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class Sections {

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
        if (sections.size() < 2) {
            return sections;
        }

        final Section from = decideUpStation(sections);
        final List<Section> sortedSections = new ArrayList<>();
        sortedSections.add(from);
        findNextSection(from, sections, sortedSections);
        return sortedSections;
    }

    private void findNextSection(final Section current, final List<Section> sections, final List<Section> sortedSections) {
        final long downStationId = current.getDownStationId();
        final Optional<Section> nextSection = sections.stream()
            .filter(section -> section.getUpStationId() == downStationId)
            .findAny();

        if (!nextSection.isPresent()) {
            return;
        }
        sortedSections.add(nextSection.get());
        findNextSection(nextSection.get(), sections, sortedSections);
    }

    private Section decideUpStation(final List<Section> sections) {
        final Map<Long, Integer> frequency = calculateFrequency(sections);
        final List<Long> lastPoints = findLastPoints(frequency);

        return sections.stream()
            .filter(section -> lastPoints.contains(section.getUpStationId()))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("해당하는 상행역이 없습니다."));
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

    private List<Long> findLastPoints(final Map<Long, Integer> frequency) {
        final List<Long> candidates = new ArrayList();
        frequency.forEach((id, value) -> {
            if (value == 1) {
                candidates.add(id);
            }
        });
        return candidates;
    }

    public void validateBothExistentStation(final Long upStationId, final Long downStationId) {
        if (containsStation(upStationId) && containsStation(downStationId)) {
            throw new RuntimeException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }
    }

    public void validateNoneExistentStation(final Long upStationId, final Long downStationId) {
        if (!containsStation(upStationId) && !containsStation(downStationId)) {
            throw new RuntimeException("상행역과 하행역 둘 다 포함되어있지 않습니다.");
        }
    }

    private boolean containsStation(final Long stationId) {
        return distinctStationIds().contains(stationId);
    }

    public boolean isFirstStationId(Long downStationId) {
        return getFirstSection().getUpStationId().equals(downStationId);
    }

    public Section getFirstSection() {
        return sectionGroup.get(0);
    }

    public boolean isLastStationId(final Long upStationId) {
        return getLastSection().getDownStationId().equals(upStationId);
    }

    public Section getLastSection() {
        return sectionGroup.get(sectionGroup.size() - 1);
    }

    private boolean isUpEndStation(final long existentStationId) {
        return sectionGroup.get(0).getUpStationId() == existentStationId;
    }

    private boolean isDownEndStation(final long existentStationId) {
        return sectionGroup.get(sectionGroup.size() - 1).getDownStationId() == existentStationId;
    }

    public Section findSectionHasThisAsUpStation(long existentStationId) {
        return sectionGroup.stream()
            .filter(section -> section.getUpStationId() == existentStationId)
            .findAny()
            .get();
    }

    public Section findSectionHasThisAsDownStation(long existentStationId) {
        return sectionGroup.stream()
            .filter(section -> section.getDownStationId() == existentStationId)
            .findAny()
            .get();
    }

    public Section findSameForm(final Long upStationId, final Long downStationId) {
        final Predicate<Section> upStationFilter = section -> section.getUpStationId().equals(upStationId);
        final Predicate<Section> downStationFilter = section -> section.getDownStationId().equals(downStationId);
        return sectionGroup.stream()
            .filter(upStationFilter.or(downStationFilter))
            .findAny()
            .orElseThrow(() -> new RuntimeException("구간을 추가하기에 적합한 곳을 찾지 못했습니다."));
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
