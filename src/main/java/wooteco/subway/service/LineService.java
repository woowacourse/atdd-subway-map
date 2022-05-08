package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

import java.util.List;
import java.util.Optional;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(Line line) {
        validateDuplicateName(line);
        return lineDao.save(line);
    }

    private void validateDuplicateName(Line line) {
        Optional<Line> optionalLine = lineDao.findByName(line.getName());
        if (optionalLine.isPresent()) {
            throw new IllegalArgumentException("같은 이름의 노선은 등록할 수 없습니다.");
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        Optional<Line> line = lineDao.findById(id);
        return line.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
    }

    public void update(Long id, Line line) {
        lineDao.update(id, line.getName(), line.getColor());
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
