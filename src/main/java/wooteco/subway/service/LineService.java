package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.exception.LineNotFoundException;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line create(final LineRequest lineRequest) {
        final Line line = lineRequest.toEntity();
        return lineDao.save(line);
    }

    @Transactional(readOnly = true)
    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(final Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new LineNotFoundException("해당 노선이 없습니다."));
    }

    @Transactional
    public void update(final Long id, final LineRequest lineRequest) {
        findById(id);
        final Line updatedLine = lineRequest.toEntity(id);
        lineDao.update(updatedLine);
    }

    @Transactional
    public void delete(final Long id) {
        final Line targetLine = findById(id);
        lineDao.delete(targetLine);
    }
}
