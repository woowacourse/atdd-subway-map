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

    private static final String DUPLICATED_NAME_ERROR_MESSAGE = "중복된 이름이 존재합니다.";
    private static final String NONE_LINE_ERROR_MESSAGE = "해당 ID의 노선은 존재하지 않습니다.";

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(LineRequest lineRequest) {
        validDuplicatedName(lineRequest.getName());
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Long id = lineDao.save(line);
        return new Line(id, line.getName(), line.getColor());
    }

    public void update(Long id, LineRequest lineRequest) {
        validDuplicatedName(lineRequest.getName());
        lineDao.update(id, lineRequest);
    }

    private void validDuplicatedName(String name) {
        if (lineDao.countByName(name) > 0) {
            throw new IllegalArgumentException(DUPLICATED_NAME_ERROR_MESSAGE);
        }
    }

    public LineDto findById(Long id) {
        try {
            Line line = lineDao.findById(id);
            return new LineDto(line.getId(), line.getName(), line.getColor());
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException(NONE_LINE_ERROR_MESSAGE);
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
