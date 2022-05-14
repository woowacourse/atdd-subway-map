package wooteco.subway.domain.line;

import java.util.ArrayList;
import java.util.List;

import wooteco.subway.exception.IdMissingException;
import wooteco.subway.exception.RowDuplicatedException;
import wooteco.subway.exception.RowNotFoundException;

public class LineSeries {
    private final List<Line> lines;

    public LineSeries(List<Line> lines) {
        validateHasId(lines);
        this.lines = new ArrayList<>(lines);
    }

    private void validateHasId(List<Line> lines) {
        lines.stream()
            .filter(line -> line.getId() == null)
            .findAny()
            .ifPresent(line -> {
                throw new IdMissingException(line.getName() + " 노선에 ID가 없습니다.");
            });
    }

    public void add(Line line) {
        validateDistinct(line.getName());
        lines.add(line);
    }

    private void validateDistinct(String name) {
        if (lines.stream().anyMatch(line -> line.getName().equals(name))) {
            throw new RowDuplicatedException(String.format("%s 는 이미 존재하는 노선 이름입니다.", name));
        }
    }

    public void delete(Long id) {
        final boolean isRemoved = lines.removeIf(line -> line.getId().equals(id));
        if (!isRemoved) {
            throw new RowNotFoundException(String.format("%d 의 ID에 해당하는 노선이 없습니다.", id));
        }
    }

    public void update(Line updateLine) {
        final Line foundLine = findLine(updateLine);
        lines.set(lines.indexOf(foundLine), updateLine);
    }

    private Line findLine(Line updateLine) {
        return lines.stream()
            .filter(line -> line.getId().equals(updateLine.getId()))
            .findAny()
            .orElseThrow(() -> new RowNotFoundException("해당하는 노선을 찾을 수 없습니다."));
    }

    public List<Line> getLines() {
        return lines;
    }
}
