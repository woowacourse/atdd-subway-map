package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.web.exception.NotFoundException;
import wooteco.subway.web.exception.SubwayHttpException;

@Service
public class LineService {

    private static final String LINE_RESOURCE_NAME = "노선";

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line findLineById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new NotFoundException(LINE_RESOURCE_NAME));
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Long addLine(Line line) {
        return lineDao.save(line);
    }

    public void updateLine(Long id, Line line) {
        lineDao.update(id, line);
    }

    public void deleteLine(Long id) {
        lineDao.delete(id);
    }

    public void validateLineId(Long lineId) {
        lineDao.findById(lineId)
                .orElseThrow(() -> new SubwayHttpException("유효하지 않은 노선입니다."));
    }
}
