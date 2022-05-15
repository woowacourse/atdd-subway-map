package wooteco.subway.domain.section;

import java.util.ArrayList;
import java.util.List;

public class SectionsSorter {

    public List<Section> create(List<Section> sections) {
        return sortSections(new ArrayList<>(sections));
    }

    private List<Section> sortSections(List<Section> sections) {
        validateSectionsNotEmpty(sections);
        Section firstSection = extractFirstSectionUsingRecursive(sections.get(0), sections);
        return orderSectionsUsingRecursive(firstSection, new ArrayList<>(List.of(firstSection)), sections);
    }

    private void validateSectionsNotEmpty(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("지하철구간은 하나 이상이어야 합니다.");
        }
    }

    private Section extractFirstSectionUsingRecursive(Section section, List<Section> sections) {
        if (isFirstSection(section, sections)) {
            return section;
        }
        Section previousSection = extractPreviousSection(section, sections);
        return extractFirstSectionUsingRecursive(previousSection, sections);
    }

    private Section extractPreviousSection(Section section, List<Section> sections) {
        return sections.stream()
                .filter(section::isNextOf)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("이전 구간이 존재하지 않습니다."));
    }

    private boolean isFirstSection(Section section, List<Section> sections) {
        return sections.stream()
                .noneMatch(section::isNextOf);
    }

    private List<Section> orderSectionsUsingRecursive(Section currentSection, List<Section> orderedSections,
                                                      List<Section> originalSections) {
        if (isLastSection(currentSection, originalSections)) {
            validateAllSectionsOrdered(orderedSections, originalSections);
            return orderedSections;
        }
        Section nextSection = extractNextSection(currentSection, originalSections);
        orderedSections.add(nextSection);
        return orderSectionsUsingRecursive(nextSection, orderedSections, originalSections);
    }

    private boolean isLastSection(Section section, List<Section> sections) {
        return sections.stream()
                .noneMatch(section::isPreviousOf);
    }

    private void validateAllSectionsOrdered(List<Section> orderedSections, List<Section> originalSections) {
        if (!orderedSections.containsAll(originalSections)) {
            throw new IllegalStateException("정렬되지 않은 구간이 존재합니다.");
        }
    }

    private Section extractNextSection(Section section, List<Section> sections) {
        return sections.stream()
                .filter(section::isPreviousOf)
                .findAny()
                .orElse(section);
    }
}
