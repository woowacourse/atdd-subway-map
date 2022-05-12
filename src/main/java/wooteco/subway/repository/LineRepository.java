package wooteco.subway.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.exception.RowNotFoundException;

@Repository
public class LineRepository {

    private final LineDao lineDao;

    public LineRepository(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(Line line) {
        final LineEntity saved = lineDao.save(LineEntity.from(line));
        return new Line(saved.getId(), saved.getName(), saved.getColor());
    }

    public List<Line> findAllLines() {
        return lineDao.findAll()
            .stream()
            .map(entity -> new Line(entity.getId(), entity.getName(), entity.getColor()))
            .collect(Collectors.toList());
    }

    public Line findById(Long id) {
        final LineEntity entity = lineDao.findById(id)
            .orElseThrow(() -> new RowNotFoundException("조회하고자 하는 노선이 존재하지 않습니다."));
        return new Line(entity.getId(), entity.getName(), entity.getColor());
    }

    public void update(Line line) {
        final boolean isUpdated = lineDao.update(LineEntity.from(line));
        if (!isUpdated) {
            throw new RowNotFoundException("수정하고자 하는 노선이 존재하지 않습니다.");
        }
    }

    public void delete(Long id) {
        final boolean isDeleted = lineDao.delete(id);
        if (!isDeleted) {
            throw new RowNotFoundException("삭제하고자 하는 노선이 존재하지 않습니다.");
        }
    }
}
