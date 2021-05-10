package wooteco.subway.line.section;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

    public List<Section> toList() {
        return sectionGroup;
    }
}
