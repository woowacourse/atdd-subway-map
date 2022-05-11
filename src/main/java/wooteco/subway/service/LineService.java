package wooteco.subway.service;

import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.CommonLineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.request.LineRequest;

@Service
public class LineService {

    private static final int NO_ROW_AFFECTED = 0;
    private static final String LINE_DUPLICATED = "이미 존재하는 노선입니다. ";
    private static final String LINE_NOT_FOUND = "요청한 노선이 존재하지 않습니다. ";

    private final CommonLineDao lineDao;

    public LineService(final CommonLineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Transactional
    public Line save(final LineRequest lineRequest) {
        final Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        try {
            return lineDao.save(line);
        } catch (DuplicateKeyException e) {
            throw new IllegalStateException(LINE_DUPLICATED + line);
        }
    }

    @Transactional(readOnly = true)
    public Line findById(final Long id) {
        return lineDao.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Line> findAll() {
        return lineDao.findAll();
    }

    @Transactional
    public void update(final Long id, final LineRequest lineRequest) {
        final Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        final int theNumberOfAffectedRow = lineDao.update(id, line);
        if (theNumberOfAffectedRow == NO_ROW_AFFECTED) {
            throw new IllegalStateException(LINE_NOT_FOUND + "id=" + id + " " + line);
        }
    }

    @Transactional
    public void deleteById(final Long id) {
        final int theNumberOfAffectedRow = lineDao.deleteById(id);
        if (theNumberOfAffectedRow == NO_ROW_AFFECTED) {
            throw new IllegalStateException(LINE_NOT_FOUND + "id=" + id);
        }
    }
}
