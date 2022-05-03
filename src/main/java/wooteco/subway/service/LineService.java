package wooteco.subway.service;

import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineDto;
import wooteco.subway.dto.LineRequest;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(LineRequest lineRequest) {
        try {
            lineDao.findByName(lineRequest.getName());
        } catch (EmptyResultDataAccessException e) {
            Line line = new Line(lineRequest.getName(), lineRequest.getColor());
            Long id = lineDao.save(line);
            return new Line(id, line.getName(), line.getColor());
        }
        throw new IllegalArgumentException("중복된 이름이 존재합니다.");
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public LineDto findById(Long id) {
        try {
            Line line = lineDao.findById(id);
            return new LineDto(line.getId(), line.getName(), line.getColor());
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("해당 ID의 노선은 존재하지 않습니다.");
        }
    }

    public void update(Long id, LineRequest lineRequest) {
        try {
            lineDao.findByName(lineRequest.getName());
        } catch (EmptyResultDataAccessException e) {
            lineDao.update(id, lineRequest);
            return;
        }
        throw new IllegalArgumentException("중복된 이름이 존재합니다.");
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
