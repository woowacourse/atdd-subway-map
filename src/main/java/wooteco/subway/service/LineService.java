package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.line.LineNotExistException;
import wooteco.subway.service.dto.LineDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineDto create(String color, String name) {
        return new LineDto(lineDao.insert(color, name));
    }

    public List<LineDto> findAllById() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineDto::new)
                .collect(Collectors.toList());
    }

    public LineDto findById(Long id) {
        Line line = lineDao.findById(id)
                .orElseThrow(LineNotExistException::new);
        return new LineDto(line);
    }

    public void updateById(Long id, String color, String name) {
        lineDao.update(id, color, name);
    }

    public void deleteById(Long id) {
        lineDao.delete(id);
    }
}
