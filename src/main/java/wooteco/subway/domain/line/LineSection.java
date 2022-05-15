package wooteco.subway.domain.line;

import java.util.Objects;
import wooteco.subway.domain.section.Section;

public class LineSection {

    private final LineInfo lineInfo;
    private final Section section;

    public LineSection(LineInfo lineInfo, Section section) {
        this.lineInfo = lineInfo;
        this.section = section;
    }

    public boolean isRegisteredAtSameLine(LineSection target) {
        return Objects.equals(lineInfo.getId(), target.getLineId());
    }

    public Long getLineId() {
        return lineInfo.getId();
    }

    public LineInfo getLineInfo() {
        return lineInfo;
    }

    public Section getSection() {
        return section;
    }
}
