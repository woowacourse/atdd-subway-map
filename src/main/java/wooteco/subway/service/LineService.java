package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.response.LineResponse;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse save(Line line) {
        return toLineResponseDto(lineDao.save(line));
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(this::toLineResponseDto)
                .collect(Collectors.toUnmodifiableList());
    }

    public LineResponse find(Long id) {
        Line line = lineDao.findById(id);
        return toLineResponseDto(line);
    }

    public void update(Line line) {
        lineDao.update(line);
    }

    public void delete(Long id) {
        lineDao.deleteById(id);
    }

    private LineResponse toLineResponseDto(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }
}
