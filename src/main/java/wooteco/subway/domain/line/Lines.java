package wooteco.subway.domain.line;

import static java.util.stream.Collectors.groupingBy;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import wooteco.subway.domain.section.RegisteredSection;

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
                .map(Line::of)
                .sorted(Comparator.comparingLong(Line::getId))
                .collect(Collectors.toList());
    }

    public List<Line> toSortedList() {
        return value;
    }
}
