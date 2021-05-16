package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.web.exception.NotFoundException;

@Service
public class LineService {

    private static final String LINE_RESOURCE_NAME = "노선";

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line findLine(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new NotFoundException(LINE_RESOURCE_NAME));
    }

    public Long addLine(Line line) {
        return lineDao.save(line);
    }

    public void update(Long id, Line line) {
        lineDao.update(id, line);
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }
}
