package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
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

    public LineResponse create(LineRequest request) {
        validateDataSize(request.getName(), request.getColor());
        Line line = new Line(request.getName(), request.getColor());
        Line savedLine = lineDao.save(line);
        return LineResponse.of(savedLine);
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
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long lineId) {
        Line line = lineDao.findById(lineId);
        return LineResponse.of(line);
    }

    public void update(Long lineId, LineRequest request) {
        lineDao.update(new Line(lineId, request.getName(), request.getColor()));
    }

    public void delete(Long lineId) {
        lineDao.delete(lineId);
    }
}
