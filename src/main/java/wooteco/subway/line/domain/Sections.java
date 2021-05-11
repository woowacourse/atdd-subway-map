package wooteco.subway.line.domain;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import wooteco.subway.line.exception.SectionException;

public class Sections {

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public Optional<Section> change(final Section requestedSection) {
        final List<Section> relatedSections = relatedSectionsOf(requestedSection);
        validateRelated(requestedSection, relatedSections);
        for (final Section section : relatedSections) {
            if (section.hasSameStations(requestedSection)) {
                break;
            }
            if (section.canConnect(requestedSection) && relatedSections.size() == 1) {
                return Optional.empty();
            }
            if (section.canJoin(requestedSection)) {
                return Optional.ofNullable(section.updateWith(requestedSection));
            }
        }
        throw new SectionException("해당 노선에 추가될 수 없는 구간입니다.");
    }

    private List<Section> relatedSectionsOf(final Section requestedSection) {
        return sections.stream()
                .filter(section -> section.isRelated(requestedSection))
                .collect(Collectors.toList());
    }

    private void validateRelated(final Section requestedSection, final List<Section> relatedSections) {
        if (relatedSections.size() == 2) {
            final Section firstSection = relatedSections.get(0);
            final Section secondSection = relatedSections.get(1);
            validateConnected(firstSection, secondSection);
            validateDifferent(requestedSection, firstSection, secondSection);
        }
    }

    private void validateConnected(final Section firstSection, final Section secondSection) {
        if (!firstSection.canConnect(secondSection)) {
            throw new SectionException("이미 노선에서 서로 연결된 역들입니다.");
        }
    }

    private void validateDifferent(final Section requestedSection, final Section firstSection, final Section secondSection) {
        if ((requestedSection.canJoin(firstSection) && requestedSection.canJoin(secondSection)) || (requestedSection.canConnect(firstSection) && requestedSection.canConnect(secondSection))) {
            throw new SectionException("갈래길을 형성할 수 없습니다.");
        }
    }
}
