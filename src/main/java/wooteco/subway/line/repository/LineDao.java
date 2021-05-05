package wooteco.subway.line.repository;

import wooteco.subway.line.domain.Line;

import java.util.*;

public class LineDao {
    private Map<Long, Line> store = new HashMap<>();
    private long sequence = 0L;

    public long save(Line line) {
        final Line identifiedLine = new Line(++sequence, line.getName(), line.getColor());
        store.put(identifiedLine.getId(), identifiedLine);

        return identifiedLine.getId();
    }

    public List<Line> allLines() {
        return new ArrayList<>(store.values());
    }

    public Line findById(final Long id) {
        final Optional<Line> line = Optional.ofNullable(store.get(id));

        if (!line.isPresent()) {
            throw new IllegalArgumentException("존재하지 않는 아이디 입니다.");
        }

        return line.get();
    }

    public void clear() {
        store.clear();
        sequence = 0L;
    }
}
