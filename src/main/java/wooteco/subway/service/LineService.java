package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineDao;
import wooteco.subway.web.exception.NotFoundException;
import wooteco.subway.web.exception.SubwayHttpException;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line add(Line line) {
        Long id = addLine(line);
        return findById(id);
    }

    private Long addLine(Line line) {
        return lineDao.save(line)
                .orElseThrow(() -> new SubwayHttpException("중복된 노선 이름입니다"));
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 노선입니다"));
    }

    public void update(Long id, Line line) {
        findById(id);
        lineDao.update(id, line);
    }

    public void delete(Long id) {
        findById(id);
        lineDao.delete(id);
    }
}
