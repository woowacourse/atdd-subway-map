package wooteco.subway.service;

import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
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

    public Line save(String name, String color) {
        validDuplicatedLine(name, color);
        Long id = lineDao.save(new Line(name, color));
        return new Line(id, name, color);
    }

    public void update(Long id, LineRequest lineRequest) {
        validDuplicatedLine(lineRequest.getName(), lineRequest.getColor());
        lineDao.update(id, lineRequest);
    }

    private void validDuplicatedLine(String name, String color) {
        if (lineDao.existByName(name) || lineDao.existByColor(color)) {
            throw new IllegalArgumentException("중복된 Line 이 존재합니다.");
        }
    }

    public LineResponse findById(Long id) {
        try {
            Line line = lineDao.findById(id);
            return new LineResponse(line);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("해당 ID의 노선은 존재하지 않습니다.");
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
