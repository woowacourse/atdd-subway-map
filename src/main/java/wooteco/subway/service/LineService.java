package wooteco.subway.service;

import java.util.List;
import java.util.Optional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineDto;
import wooteco.subway.dto.LineRequest;

public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(LineRequest LineRequest) {
        Optional<Line> findLine = lineDao.findByName(LineRequest.getName());
        if (findLine.isPresent()) {
            throw new IllegalArgumentException("중복된 ID가 존재합니다");
        }
        Line Line = new Line(LineRequest.getName(), LineRequest.getColor());
        return lineDao.save(Line);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public LineDto findById(Long id) {
        Optional<Line>  findLine =lineDao.findById(id);
        if(findLine.isEmpty()) {
            throw new IllegalArgumentException("해당 ID의 노선은 존재하지 않습니다.");
        }
        Line line = findLine.get();
        return new LineDto(line.getId(), line.getName(), line.getColor());
    }

    public void deleteAll() {
        lineDao.deleteAll();
    }

}
