package wooteco.subway.repository;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.entity.LineEntity;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.NotFoundException;

@Repository
public class LineRepository {

    private final LineDao lineDao;

    public LineRepository(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(Line line) {
        if (line.getId() == null) {
            return toLine(lineDao.save(new LineEntity(line.getName(), line.getColor())));
        }
        lineDao.modifyById(new LineEntity(line.getId(), line.getName(), line.getColor()));
        return line;
    }

    public Line findById(Long id) {
        LineEntity entity = lineDao.findById(id)
            .orElseThrow(() -> new NotFoundException("조회하려는 id가 존재하지 않습니다. id : " + id));
        return toLine(entity);
    }

    public List<Line> findAll() {
        List<LineEntity> entities = lineDao.findAll();
        return entities.stream()
            .map(this::toLine)
            .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }

    public boolean existByNameAndColor(String name, String color) {
        return lineDao.existByNameAndColor(name, color);
    }

    private Line toLine(LineEntity entity) {
        return new Line(entity.getId(), entity.getName(), entity.getColor());
    }
}
