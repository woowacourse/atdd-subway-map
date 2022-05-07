package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.LineNotFoundException;

@Service
public class LineService {

    private static final int NOT_FOUND = 0;

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse createLine(final LineRequest request) {
        final Line line = new Line(request.getName(), request.getColor());
        final Long id = lineDao.save(line);
        return new LineResponse(id, request.getName(), request.getColor());
    }

    public List<LineResponse> showLines() {
        final List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse showLine(final long id) {
        final Line line = lineDao.find(id);
        return new LineResponse(id, line.getName(), line.getColor());
    }

    public void updateLine(final long id, final LineRequest request) {
        if (lineDao.update(id, request.getName(), request.getColor()) == NOT_FOUND) {
            throw new LineNotFoundException();
        }
    }

    public void deleteLine(final long id) {
        if (lineDao.delete(id) == NOT_FOUND) {
            throw new LineNotFoundException();
        }
    }
}
