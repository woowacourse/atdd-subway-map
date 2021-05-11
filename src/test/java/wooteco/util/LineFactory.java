package wooteco.util;

import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.section.Section;
import wooteco.subway.domain.line.section.Sections;
import wooteco.subway.domain.line.value.line.LineColor;
import wooteco.subway.domain.line.value.line.LineId;
import wooteco.subway.domain.line.value.line.LineName;

import java.util.List;

public class LineFactory {

    public static Line create(Long id, String name, String color, List<Section> sections) {
        return new Line(
                id == null ? LineId.empty() : new LineId(id),
                new LineName(name),
                new LineColor(color),
                new Sections(sections)
        );
    }

    public static Line create(String name, String color, List<Section> sections) {
        return create(null, name, color, sections);
    }

}
