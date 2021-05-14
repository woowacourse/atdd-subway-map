package wooteco.subway.line.domain;

import java.util.Collections;
import java.util.List;

public class Lines {
    private final List<Line> lines;

    public Lines(final List<Line> lines) {
        this.lines = lines;
    }

    public List<Line> toList() {
        return Collections.unmodifiableList(lines);
    }

    public boolean doesNameExist(final Line line) {
        return lines.stream().anyMatch(thisLine -> thisLine.hasSameName(line));
    }

    public boolean doesIdNotExist(final Long id) {
        return lines.stream().noneMatch(line -> line.hasId(id));
    }
}
