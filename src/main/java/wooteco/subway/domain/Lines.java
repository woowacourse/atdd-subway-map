package wooteco.subway.domain;

import wooteco.subway.exception.ClientException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lines {

    private final List<Line> lines;

    public Lines(List<Line> lines) {
        this.lines = new ArrayList<>(lines);
    }

    public void add(Line line) {
        validateDuplicateLine(line);
        lines.add(line);
    }

    private void validateDuplicateLine(Line request) {
        boolean duplicate = lines.stream()
                .anyMatch(line -> line.isSameName(request.getName()));

        if (duplicate) {
            throw new ClientException("이미 등록된 지하철노선입니다.");
        }
    }

    public List<Line> getLines() {
        return Collections.unmodifiableList(lines);
    }
}
