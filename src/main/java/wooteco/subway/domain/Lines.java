package wooteco.subway.domain;

import java.util.List;

public class Lines {

    public static final String DUPLICATED_NAME_EXCEPTION = "노선의 이름은 중복될 수 없습니다.";
    public static final String DUPLICATED_COLOR_EXCEPTION = "노선의 색은 중복될 수 없습니다.";
    private List<Line> lines;

    public Lines(List<Line> lines) {
        this.lines = lines;
    }

    public void checkAbleToAdd(Line line) {
        checkNameIsUnique(line);
        checkColorIsUnique(line);
    }

    private void checkNameIsUnique(Line line) {
        boolean duplicated = lines.stream()
                .anyMatch(it -> it.isSameName(line));
        if (duplicated) {
            throw new IllegalArgumentException(DUPLICATED_NAME_EXCEPTION);
        }
    }

    private void checkColorIsUnique(Line line) {
        boolean duplicated = lines.stream()
                .anyMatch(it -> it.isSameColor(line));
        if (duplicated) {
            throw new IllegalArgumentException(DUPLICATED_COLOR_EXCEPTION);
        }
    }
}
