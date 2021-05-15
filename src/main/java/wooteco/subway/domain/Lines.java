package wooteco.subway.domain;

import java.util.List;
import wooteco.subway.exception.DuplicateException;

public class Lines {

    private final List<Line> lines;

    public Lines(List<Line> lines) {
        this.lines = lines;
    }

    public void validateDuplicate(Line line) {
        if (lines.stream().anyMatch(line::isDuplicate)) {
            throw new DuplicateException();
        }
    }
}
