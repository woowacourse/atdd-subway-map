package wooteco.subway.domain;

public class LineSection {

    private final Line line;
    private final Section section;

    public LineSection(Line line, Section section) {
        this.line = line;
        this.section = section;
    }

    public Line getLine() {
        return line;
    }

    public Section getSection() {
        return section;
    }

}
