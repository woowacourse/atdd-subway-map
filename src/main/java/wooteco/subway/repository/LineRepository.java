package wooteco.subway.repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import wooteco.subway.domain.line.Line;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.exception.DuplicateLineColorException;
import wooteco.subway.repository.exception.DuplicateLineNameException;

@Repository
public class LineRepository {

    private final LineDao lineDao;

    public LineRepository(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Long save(Line line) {
        validate(line);
        return lineDao.save(line);
    }

    private void validate(Line line) {
        validateNameNotDuplicated(line.getName());
        validateColorNotDuplicated(line.getColor());
    }

    private void validateNameNotDuplicated(String name) {
        if (lineDao.existsByName(name)) {
            throw new DuplicateLineNameException(name);
        }
    }

    private void validateColorNotDuplicated(String color) {
        if (lineDao.existsByColor(color)) {
            throw new DuplicateLineColorException(color);
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long lineId) {
        return lineDao.findById(lineId)
                .orElseThrow(() -> new NoSuchElementException("조회하고자 하는 지하철노선이 존재하지 않습니다."));
    }

    public void update(Line line) {
        if (nonExistsById(line.getId())) {
            throw new NoSuchElementException("수정하고자 하는 지하철노선이 존재하지 않습니다.");
        }
        validate(line);
        lineDao.update(line.getId(), line.getName(), line.getColor());
    }

    public void remove(Long lineId) {
        if (nonExistsById(lineId)) {
            throw new NoSuchElementException("삭제하고자 하는 지하철노선이 존재하지 않습니다.");
        }
        lineDao.remove(lineId);
    }

    private boolean nonExistsById(Long lineId) {
        Optional<Line> line = lineDao.findById(lineId);
        return line.isEmpty();
    }
}
