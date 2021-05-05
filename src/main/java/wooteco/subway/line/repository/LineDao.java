package wooteco.subway.line.repository;

import wooteco.subway.line.domain.Line;

import java.util.*;

public class LineDao {
    public static final String ID_DOES_NOT_EXIST = "존재하지 않는 아이디 입니다.";

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

        if (!store.containsKey(id)) {
            throw new IllegalArgumentException(ID_DOES_NOT_EXIST);
        }

        return store.get(id);
    }

    public void clear() {
        store.clear();
        sequence = 0L;
    }

    public void update(final Line line) {
        if (store.replace(line.getId(), line) == null) {
            throw new IllegalArgumentException(ID_DOES_NOT_EXIST);
        }
    }
}
