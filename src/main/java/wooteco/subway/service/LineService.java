package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.ExceptionMessage;
import wooteco.subway.exception.InternalServerException;
import wooteco.subway.exception.NotFoundException;

@Service
public class LineService {

    private static final int LINES_NOT_DELETED = 0;
    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse create(final LineRequest request) {
        Line line = new Line(request.getName(), request.getColor());
        try {
            final Line savedLine = lineDao.save(line);
            return LineResponse.of(savedLine);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException(ExceptionMessage.DUPLICATED_LINE_NAME.getContent());
        }
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        try {
            Line line = lineDao.findById(id);
            return LineResponse.of(line);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(ExceptionMessage.NOT_FOUND_LINE_BY_ID.getContent());
        }
    }

    public void updateById(final Long id, final LineRequest request) {
        final Line line = new Line(request.getName(), request.getColor());
        lineDao.updateById(id, line);
    }

    public void deleteById(final Long id) {
        Integer deletedLines = lineDao.deleteById(id);

        if (deletedLines == LINES_NOT_DELETED) {
            throw new InternalServerException(ExceptionMessage.UNKNOWN_DELETE_LINE_FAIL.getContent());
        }
    }
}
