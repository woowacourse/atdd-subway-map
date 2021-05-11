package wooteco.subway.line.domain;

import java.util.ArrayList;
import java.util.List;

public class Lines {
    private List<Line> lines;

    public Lines(final List<Line> lines) {
        this.lines = new ArrayList<>(lines);
    }

    public boolean haveSameName(Line line) {
       return lines.stream()
               .filter(savedLine -> savedLine.getName().equals(line.getName())).count() == 1;
    }

    public List<Line> toList() {
        return this.lines;
    }
}
