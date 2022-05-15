package wooteco.subway.domain.line;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import wooteco.subway.domain.section.RegisteredSection;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.section.SectionsFactory;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.ExceptionType;
import wooteco.subway.exception.NotFoundException;

public class Line {

    private static final String INVALID_SECTION_COMPOSITION_EXCEPTION = "다른 노선에 등록된 구간이 포함되었습니다.";

    private final LineInfo lineInfo;
    private final Sections sections;

    private Line(LineInfo lineInfo, Sections sections) {
        this.lineInfo = lineInfo;
        this.sections = sections;
    }

    public static Line of(List<RegisteredSection> registeredSections) {
        validateNotEmpty(registeredSections);
        validateSameLineSections(registeredSections);
        Sections sections = toSections(registeredSections);
        LineInfo lineInfo = LineInfo.of(registeredSections.get(0));

        return new Line(lineInfo, sections);
    }

    private static void validateNotEmpty(List<RegisteredSection> registeredSections) {
        if (registeredSections.isEmpty()) {
            throw new NotFoundException(ExceptionType.LINE_NOT_FOUND);
        }
    }

    private static void validateSameLineSections(List<RegisteredSection> registeredSections) {
        RegisteredSection targetSection = registeredSections.get(0);
        boolean hasAnotherLineSection = registeredSections.stream()
                .anyMatch(it -> !it.isRegisteredAtSameLine(targetSection));
        if (hasAnotherLineSection) {
            throw new IllegalArgumentException(INVALID_SECTION_COMPOSITION_EXCEPTION);
        }
    }

    private static Sections toSections(List<RegisteredSection> sameLineSections) {
        List<Section> sections = sameLineSections.stream()
                .map(RegisteredSection::getSection)
                .collect(Collectors.toList());
        return SectionsFactory.generate(sections);
    }

    public Long getId() {
        return lineInfo.getId();
    }

    public LineInfo getLineInfo() {
        return lineInfo;
    }

    public List<Station> getSortedStations() {
        return sections.toSortedStations();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(lineInfo, line.lineInfo)
                && Objects.equals(sections, line.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineInfo, sections);
    }

    @Override
    public String toString() {
        return "Line{" + "lineInfo=" + lineInfo + ", sections=" + sections + '}';
    }
}
