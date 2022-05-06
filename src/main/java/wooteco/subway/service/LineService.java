package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.DataLengthException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse create(String name, String color) {
        validateDataSize(name, color);
        Line line = new Line(name, color);
        Line savedLine = lineDao.save(line);
        return new LineResponse(savedLine.getId(), savedLine.getName(), savedLine.getColor());
    }

    private void validateDataSize(String name, String color) {
        if (name.isEmpty() || name.length() > 255) {
            throw new DataLengthException("노선 이름이 빈 값이거나 최대 범위를 초과했습니다.");
        }
        if (color.isEmpty() || color.length() > 20) {
            throw new DataLengthException("노선 색이 빈 값이거나 최대 범위를 초과했습니다.");
        }
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(l -> new LineResponse(l.getId(), l.getName(), l.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long lineId) {
        Line line = lineDao.findById(lineId);
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public void update(Long lineId, String name, String color) {
        lineDao.update(new Line(lineId, name, color));
    }

    public void delete(Long lineId) {
        lineDao.delete(lineId);
    }
}
