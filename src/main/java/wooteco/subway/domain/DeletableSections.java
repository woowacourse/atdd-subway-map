package wooteco.subway.domain;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import wooteco.subway.exception.ExceptionMessage;

public class DeletableSections {

    private static final int MAX_NEAR_SECTION_COUNT = 2;
    private static final int MERGEABLE_SECTION_COUNT = 2;

    private final List<Section> sections;

    public DeletableSections(List<Section> sections) {
        if (sections.size() > MAX_NEAR_SECTION_COUNT) {
            throw new IllegalArgumentException(ExceptionMessage.NEAR_SECTIONS_OVER_SIZE.getContent());
        }
        this.sections = sections;
    }

    public Optional<Section> mergeSections() {
        if (sections.size() < MERGEABLE_SECTION_COUNT) {
            return Optional.empty();
        }
        Section from = sections.get(0);
        Section to = sections.get(1);
        return Optional.of(from.merge(to));
    }

    public List<Long> getSectionIds() {
        return sections.stream()
                .map(Section::getId)
                .collect(Collectors.toList());
    }
}
