package wooteco.subway.service;

import org.springframework.dao.DataIntegrityViolationException;
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
        try {
            return new LineResponse(lineDao.insert(color, name));
        } catch (DataIntegrityViolationException e) {
            throw new LineDuplicateException();
        }
    }

    public List<LineResponse> showAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse showById(Long id) {
        Line line = lineDao.findById(id)
                .orElseThrow(LineNotExistException::new);
        return new LineResponse(line);
    }

    public void updateById(Long id, String color, String name) {
        try {
            lineDao.update(id, color, name);
        } catch (DataIntegrityViolationException e) {
            throw new LineDuplicateException();
        }
    }

    public void deleteById(Long id) {
        lineDao.delete(id);
    }
}
