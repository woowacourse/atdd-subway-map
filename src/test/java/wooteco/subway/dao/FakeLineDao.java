package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import wooteco.subway.domain.Line;

public class FakeLineDao extends LineDao {

    private Long seq = 0L;
    private final Map<Long, Line> lines = new HashMap<>();

    public FakeLineDao() {
        super(Mockito.mock(DataSource.class));
    }

    @Override
    public Line save(Line line) {
        Line persistLine = new Line(++seq, line.getName(), line.getColor());
        lines.put(seq, persistLine);
        return persistLine;
    }

    @Override
    public List<Line> findAll() {
        return List.copyOf(lines.values());
    }

    @Override
    public Line findById(Long id) {
        validateExist(id);
        return lines.get(id);
    }

    private void validateExist(Long id) {
        if (!lines.containsKey(id)) {
            throw new EmptyResultDataAccessException(1);
        }
    }

    @Override
    public void updateById(Long id, Line line) {
        Line updateLine = new Line(id, line.getName(), line.getColor());
        lines.put(id, updateLine);
    }

    @Override
    public void deleteById(Long id) {
        lines.remove(id);
    }

    @Override
    public boolean existsId(Long id) {
        return lines.values()
                .stream()
                .anyMatch(line -> line.getId().equals(id));
    }

    @Override
    public boolean existsName(Line line) {
        return lines.values()
                .stream()
                .filter(savedLine -> !savedLine.getId().equals(line.getId()))
                .anyMatch(savedLine -> savedLine.getName().equals(line.getName()));
    }

    @Override
    public boolean existsColor(Line line) {
        return lines.values()
                .stream()
                .filter(savedLine -> !savedLine.getId().equals(line.getId()))
                .anyMatch(savedLine -> savedLine.getColor().equals(line.getColor()));
    }
}
