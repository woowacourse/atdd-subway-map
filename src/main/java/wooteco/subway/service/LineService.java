package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.exception.line.LineDuplicateException;
import wooteco.subway.exception.line.LineNotExistException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse create(String color, String name) {
        validateDuplicateColorAndName(color, name);
        return new LineResponse(lineDao.insert(new Line(color, name)));
    }

    public List<LineResponse> showAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse showById(Long id) {
        Line line = lineDao.findById(id)
                .orElseThrow(() -> new LineNotExistException(id));
        return new LineResponse(line);
    }

    public void updateById(Long id, String color, String name) {
        validateDuplicateColorAndName(color, name);
        lineDao.update(id, color, name);
    }

    public void deleteById(Long id) {
        lineDao.delete(id);
    }

    private void validateDuplicateColorAndName(String color, String name) {
        lineDao.findByColor(color)
                .ifPresent(line -> {
                    throw new LineDuplicateException(line.getColor());
                });
        lineDao.findByName(name)
                .ifPresent(line -> {
                    throw new LineDuplicateException(line.getName());
                });
    }
}
