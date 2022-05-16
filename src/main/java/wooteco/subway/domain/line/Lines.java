package wooteco.subway.domain.line;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;

public class Lines {

    private static final String LINES_AND_SECTIONS_NOT_MATCHING_EXCEPTION = "노선 정보와 구간들의 정보가 서로 불일치합니다.";

    private final List<Line> value;

    private Lines(List<Line> value) {
        this.value = value;
    }

    public static Lines of(List<LineInfo> lineInfos, List<Section> sections) {
        validateRegisteredLines(lineInfos, sections);
        List<Line> lines = lineInfos.stream()
                .map(it -> toLine(it, sections))
                .sorted(Comparator.comparingLong(Line::getId))
                .collect(Collectors.toList());
        return new Lines(lines);
    }

    private static void validateRegisteredLines(List<LineInfo> lineInfos, List<Section> sections) {
        if (!extractAllLineIds(lineInfos).containsAll(extractAllRegisteredLineIds(sections))) {
            throw new RuntimeException(LINES_AND_SECTIONS_NOT_MATCHING_EXCEPTION);
        }
    }

    private static List<Long> extractAllLineIds(List<LineInfo> lineInfos) {
        return lineInfos.stream()
                .map(LineInfo::getId)
                .collect(Collectors.toList());
    }

    private static List<Long> extractAllRegisteredLineIds(List<Section> sections) {
        return sections.stream()
                .map(Section::getLineId)
                .distinct()
                .collect(Collectors.toList());
    }

    private static Line toLine(LineInfo lineInfo, List<Section> sections) {
        Long lineId = lineInfo.getId();
        List<Section> registeredSections = extractRegisteredSections(sections, lineId);
        return new Line(lineInfo, new Sections(registeredSections));
    }

    private static List<Section> extractRegisteredSections(List<Section> sections, Long lineId) {
        return sections.stream()
                .filter(it -> it.isRegisteredAtLine(lineId))
                .collect(Collectors.toList());
    }

    public List<Line> toSortedList() {
        return value;
    }
}
