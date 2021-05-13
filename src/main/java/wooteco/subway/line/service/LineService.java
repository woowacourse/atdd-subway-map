package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;

import java.util.List;

@Service
public class LineService {
    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(final Line line) {
        validateDuplicate(line);
        return lineDao.save(line);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(final Long id) {
        return lineDao.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("[ERROR] 존재하지 않는 노선입니다."));
    }

    public Line findByName(final String name) {
        return lineDao.findByName(name)
                .orElseThrow(() ->
                        new IllegalArgumentException("[ERROR] 존재하지 않는 노선입니다."));
    }

    public void update(final Long id, String name, String color) {
        lineDao.update(new Line(id, name, color));
    }

    public void delete(final Long id) {
        lineDao.delete(id);
    }


    private void validateDuplicate(final Line line) {
        if (lineDao.findByName(line.name()).isPresent()) {
            throw new IllegalStateException("이미 있는 역임!");
        }
    }
}
