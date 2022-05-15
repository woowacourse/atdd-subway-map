package wooteco.subway.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.entity.LineEntity;

@Repository
public class LineRepository {

    private final LineDao lineDao;

    public LineRepository(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(Line line) {
        LineEntity toSave = new LineEntity(null, line.getName(), line.getColor());
        LineEntity saved = lineDao.save(toSave);
        return convertToLine(saved);
    }

    private Line convertToLine(LineEntity entity) {
        return new Line(entity.getId(), entity.getName(), entity.getColor());
    }

    public List<Line> findAll() {
        return lineDao.findAll().stream()
                .map(this::convertToLine)
                .collect(Collectors.toList());
    }

    public Optional<Line> findById(Long id) {
        return lineDao.findById(id)
                .map(this::convertToLine);
    }

    public Optional<Line> update(Long id, Line line) {
        LineEntity entity = new LineEntity(id, line.getName(), line.getColor());
        return lineDao.update(entity)
                .map(this::convertToLine);
    }

    public Integer deleteById(Long id) {
        return lineDao.deleteById(id);
    }
}
