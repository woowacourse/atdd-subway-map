package wooteco.subway.domain.line;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.domain.section.Section2;
import wooteco.subway.domain.section.Sections;

public class Lines2 {

    private final List<Line> value;

    private Lines2(List<Line> value) {
        this.value = value;
    }

    public static Lines2 of(List<LineInfo> lineInfos, List<Section2> sections) {
        List<Line> lines = lineInfos.stream()
                .map(it -> toLine(it, sections))
                .sorted(Comparator.comparingLong(Line::getId))
                .collect(Collectors.toList());
        return new Lines2(lines);
    }

    private static Line toLine(LineInfo lineInfo, List<Section2> sections) {
        Long lineId = lineInfo.getId();
        List<Section2> registeredSections = extractRegisteredSections(sections, lineId);
        return new Line(lineInfo, Sections.of(registeredSections));
    }

    private static List<Section2> extractRegisteredSections(List<Section2> sections, Long lineId) {
        return sections.stream()
                .filter(it -> it.isRegisteredAtLine(lineId))
                .collect(Collectors.toList());
    }

    public List<Line> toSortedList() {
        return value;
    }
}
