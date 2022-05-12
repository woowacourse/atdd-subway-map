package wooteco.subway.domain;

import java.util.List;

import wooteco.subway.exception.RowDuplicatedException;

public class LineSeries {
    private final List<Line> lines;

    public LineSeries(List<Line> lines) {
        this.lines = lines;
    }

    public Line create(String name, String color) {
        validateDistinct(name);
        return new Line(name, color);
    }

    private void validateDistinct(String name) {
        if (lines.stream().anyMatch(line -> line.getName().equals(name))) {
            throw new RowDuplicatedException("이미 존재하는 노선 이름입니다.");
        }
    }
}
