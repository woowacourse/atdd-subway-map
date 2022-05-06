package wooteco.subway.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DataNotExistException;

@Service
public class LineService {

    private static final int ROW_SIZE_WHEN_NOT_DELETED = 0;
    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line createLine(Line line) {
        Optional<Line> foundLine = lineDao.findByName(line.getName());
        if (foundLine.isPresent()) {
            throw new IllegalArgumentException("이미 등록된 노선입니다.");
        }
        return lineDao.save(line);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        Optional<Line> foundLine = lineDao.findById(id);
        if (foundLine.isEmpty()) {
            throw new DataNotExistException("존재하지 않는 노선입니다.");
        }
        return foundLine.get();
    }

    public void update(Line line) {
        Optional<Line> foundLine = lineDao.findByName(line.getName());
        if (foundLine.isPresent() && !line.hasSameId(foundLine.get())) {
            throw new IllegalArgumentException("이미 등록된 노선입니다.");
        }
        lineDao.update(line.getId(), line.getName(), line.getColor());
    }

    public void deleteById(Long id) {
        if (lineDao.deleteById(id) == ROW_SIZE_WHEN_NOT_DELETED) {
            throw new DataNotExistException("존재하지 않는 노선입니다.");
        }
    }
}
