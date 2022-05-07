package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;
import wooteco.subway.exception.line.DuplicateLineException;
import wooteco.subway.exception.line.NoSuchLineException;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse create(final LineRequest request) {
        final Line line = new Line(request.getName(), request.getColor());
        final Line savedStation = lineDao.insert(line)
                .orElseThrow(DuplicateLineException::new);
        return LineResponse.from(savedStation);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(final Long id) {
        final Line line = lineDao.findById(id)
                .orElseThrow(NoSuchLineException::new);
        return LineResponse.from(line);
    }

    public void updateById(final Long id, final LineRequest request) {
        final Line line = lineDao.findById(id)
                .orElseThrow(NoSuchLineException::new);
        line.updateName(request.getName());
        line.updateColor(request.getColor());
        lineDao.updateById(id, line)
                .orElseThrow(DuplicateLineException::new);
    }

    public void deleteById(final Long id) {
        lineDao.deleteById(id);
    }
}
