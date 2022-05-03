package wooteco.subway.service;

import java.util.List;
import java.util.Optional;
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
        Optional<Line> findLine = lineDao.findByName(lineRequest.getName());
        if (findLine.isPresent()) {
            throw new IllegalArgumentException("중복된 ID가 존재합니다");
        }
        Line Line = new Line(lineRequest.getName(), lineRequest.getColor());
        return lineDao.save(Line);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public LineDto findById(Long id) {
        Optional<Line> findLine = lineDao.findById(id);
        if (findLine.isEmpty()) {
            throw new IllegalArgumentException("해당 ID의 노선은 존재하지 않습니다.");
        }
        Line line = findLine.get();
        return new LineDto(line.getId(), line.getName(), line.getColor());
    }

    public void deleteAll() {
        lineDao.deleteAll();
    }

    public void update(Long id, LineRequest lineRequest) {
        Optional<Line> findLine = lineDao.findByName(lineRequest.getName());
        if (findLine.isPresent()) {
            throw new IllegalArgumentException("중복된 ID가 존재합니다");
        }
        lineDao.update(id, lineRequest);
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
