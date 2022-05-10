package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Transactional
    public LineResponse save(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line savedLine = lineDao.save(line);
        return new LineResponse(savedLine.getId(), savedLine.getName(), savedLine.getColor());
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    @Transactional
    public LineResponse update(Long id, LineRequest lineRequest) {
        Line line = lineDao.update(id, new Line(lineRequest.getName(), lineRequest.getColor()));
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    @Transactional
    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
