package wooteco.subway.line.state;

import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;

public class Modified extends Change {
    protected Modified(Sections sections) {
        super(sections);
    }

    @Override
    public State addSection(Line line, Section targetSection) {
        throw new UnsupportedOperationException("이미 노선이 변경되었습니다.");
    }
}
