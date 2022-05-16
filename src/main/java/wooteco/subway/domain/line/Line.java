package wooteco.subway.domain.line;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.ExceptionType;
import wooteco.subway.exception.NotFoundException;

public class Line {

    private static final String INVALID_SECTION_COMPOSITION_EXCEPTION = "다른 노선에 등록된 구간이 포함되었습니다.";

    private final LineInfo lineInfo;
    private final Sections sections;

    public Line(LineInfo lineInfo, Sections sections) {
        this.lineInfo = lineInfo;
        this.sections = sections;
    }

    public static Line of(List<LineSection> lineSections) {
        validateNotEmpty(lineSections);
        validateSameLineSections(lineSections);
        LineInfo lineInfo = lineSections.get(0).getLineInfo();
        Sections sections = toSections(lineSections);

        return new Line(lineInfo, sections);
    }

    private static void validateNotEmpty(List<LineSection> lineSections) {
        if (lineSections.isEmpty()) {
            throw new NotFoundException(ExceptionType.LINE_NOT_FOUND);
        }
    }

    private static void validateSameLineSections(List<LineSection> lineSections) {
        LineSection targetSection = lineSections.get(0);
        boolean hasAnotherLineSection = lineSections.stream()
                .anyMatch(it -> !it.isRegisteredAtSameLine(targetSection));
        if (hasAnotherLineSection) {
            throw new IllegalArgumentException(INVALID_SECTION_COMPOSITION_EXCEPTION);
        }
    }

    private static Sections toSections(List<LineSection> sameLineSections) {
        List<Section> sections = sameLineSections.stream()
                .map(LineSection::getSection)
                .collect(Collectors.toList());
        return new Sections(sections);
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
