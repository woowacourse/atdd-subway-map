package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.LineEntity;

class FakeLineDao implements LineDao {

    private final List<LineEntity> lines = new ArrayList<>();
    private Long seq = 0L;

    @Override
    public LineEntity save(LineEntity line) {
        LineEntity persistStation = createNewObject(line);
        lines.add(persistStation);
        return persistStation;
    }

    private LineEntity createNewObject(LineEntity line) {
        return new LineEntity(++seq, line.getName(), line.getColor());
    }

    @Override
    public Optional<LineEntity> findById(Long id) {
        return lines.stream().filter(line -> line.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<LineEntity> findByName(String name) {
        return lines.stream()
            .filter(line -> name.equals(line.getName()))
            .findFirst();
    }

    @Override
    public List<LineEntity> findAll() {
        return List.copyOf(lines);
    }

    @Override
    public void update(LineEntity otherLine) {
        int idx = 0;
        for (LineEntity line : lines) {
            if (line.hasSameId(otherLine)) {
                lines.set(idx, otherLine);
                return;
            }
            idx++;
        }
    }

    @Override
    public int deleteById(Long id) {
        if (!lines.removeIf(line -> line.getId().equals(id))) {
            return 0;
        }
        return 1;
    }
}
