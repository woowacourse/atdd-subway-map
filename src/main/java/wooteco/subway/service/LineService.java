package wooteco.subway.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.DuplicateNameException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse create(String name, String color) {
        try {
            Line line = lineDao.save(new Line(name, color));
            return new LineResponse(line.getId(), line.getName(), line.getColor(), null);
        } catch (DuplicateKeyException e) {
            throw new DuplicateNameException(name + "은 이미 존재합니다.");
        }
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(l -> new LineResponse(l.getId(), l.getName(), l.getColor(), null))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long lineId) {
        Line line = lineDao.findById(lineId);
        return new LineResponse(line.getId(), line.getName(), line.getColor(), null);
    }

    public void update(Long lineId, String name, String color) {
        try {
            lineDao.update(new Line(lineId, name, color));
        } catch (DuplicateKeyException e) {
            throw new DuplicateNameException(name + "은 이미 존재합니다.");
        }
    }

    public void delete(Long lineId) {
        lineDao.delete(lineId);
    }
}
