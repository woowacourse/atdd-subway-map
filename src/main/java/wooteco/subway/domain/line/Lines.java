package wooteco.subway.domain.line;

import static java.util.stream.Collectors.groupingBy;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Lines {

    private final List<Line> value;

    Lines(List<Line> value) {
        this.value = value;
    }

    public static Lines of(List<LineSection> lineSections) {
        return new Lines(toSortedLines(groupByLine(lineSections)));
    }

    private static Map<Long, List<LineSection>> groupByLine(List<LineSection> lineSections) {
        return lineSections.stream()
                .collect(groupingBy(LineSection::getLineId));
    }

    private static List<Line> toSortedLines(Map<Long, List<LineSection>> registeredSections) {
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
