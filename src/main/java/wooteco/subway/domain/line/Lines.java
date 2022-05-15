package wooteco.subway.domain.line;

import static java.util.stream.Collectors.groupingBy;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import wooteco.subway.domain.section.RegisteredSection;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.section.SectionsFactory;

public class Lines {

    private final List<Line> value;

    private Lines(List<Line> value) {
        this.value = value;
    }

    public static Lines of(List<RegisteredSection> sections) {
        return new Lines(toSortedLines(groupByLine(sections)));
    }

    private static Map<Long, List<RegisteredSection>> groupByLine(List<RegisteredSection> sections) {
        return sections.stream()
                .collect(groupingBy(RegisteredSection::getLineId));
    }

    private static List<Line> toSortedLines(Map<Long, List<RegisteredSection>> registeredSections) {
        return registeredSections.keySet()
                .stream()
                .map(registeredSections::get)
                .map(Lines::toLine)
                .sorted(Comparator.comparingLong(Line::getId))
                .collect(Collectors.toList());
    }

    private static Line toLine(List<RegisteredSection> sameLineSections) {
        RegisteredSection commonSection = sameLineSections.get(0);
        Long lineId = commonSection.getLineId();
        String lineName = commonSection.getLineName();
        String lineColor = commonSection.getLineColor();
        LineInfo lineInfo = new LineInfo(lineId, lineName, lineColor);
        Sections sections = toSections(sameLineSections);

        return new Line(lineInfo, sections);
    }

    private static Sections toSections(List<RegisteredSection> sameLineSections) {
        List<Section> sections = sameLineSections.stream()
                .map(RegisteredSection::getSection)
                .collect(Collectors.toList());
        return SectionsFactory.generate(sections);
    }

    public List<Line> toSortedList() {
        return value;
    }
}
