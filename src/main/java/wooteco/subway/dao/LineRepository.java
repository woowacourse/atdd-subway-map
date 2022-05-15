package wooteco.subway.dao;

import static java.util.stream.Collectors.toList;

import java.util.List;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.Entity.LineEntity;
import wooteco.subway.domain.Line;

@Repository
public class LineRepository {

    private final LineDao lineDao;

    public LineRepository(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Long save(Line line) {
        return lineDao.save(new LineEntity(line.getName(), line.getColor()));
    }

    public Line findById(Long id, String message) {
        LineEntity lineEntity = lineDao.findById(id).orElseThrow(() -> new IllegalArgumentException(message));
        return new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor());
    }

    public List<Line> findAll() {
        List<LineEntity> list = lineDao.findAll();
        return list.stream().map(entity -> new Line(entity.getId(), entity.getName(), entity.getColor())).collect(
                toList());
    }

    public void modifyById(Long id, Line line) {
        lineDao.modifyById(id, new LineEntity(line.getName(), line.getColor()));
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }

    public boolean existByNameAndColor(String name, String color) {
        return lineDao.existByNameAndColor(name, color);
    }
}
