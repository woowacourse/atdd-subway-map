package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.line.DuplicatedLineNameException;
import wooteco.subway.exception.line.InvalidLineIdException;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse save(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        validateLineName(line);
        final Line savedLine = lineDao.save(line);
        return new LineResponse(savedLine.getId(), savedLine.getName(), savedLine.getColor(), null);
    }

    private void validateLineName(final Line line) {
        if (lineDao.exists(line)) {
            throw new DuplicatedLineNameException();
        }
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor(), null))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        validateId(id);
        lineDao.deleteById(id);
    }

    public LineResponse findLineById(Long id) {
        validateId(id);
        final Line line = lineDao.findById(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor(), null);
    }

    public void update(Long id, LineRequest lineRequest) {
        validateId(id);
        Line updatingLine = new Line(lineRequest.getName(), lineRequest.getColor());
        lineDao.update(id, updatingLine);
    }

    private void validateId(Long id) {
        if (!lineDao.exists(id)) {
            throw new InvalidLineIdException();
        }
    }
}
