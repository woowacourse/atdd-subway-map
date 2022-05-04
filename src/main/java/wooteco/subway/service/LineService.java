package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

@Service
public class LineService {
    protected static final String DUPLICATE_EXCEPTION_MESSAGE = "이름이나 색깔이 중복된 노선은 만들 수 없습니다.";

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line create(Line line) {
        validateDuplicate(line);
        return lineDao.save(line);
    }

    private void validateDuplicate(Line line) {
        List<Line> lines = lineDao.findAll();
        boolean isDuplicate = lines.stream()
                .anyMatch(it -> it.isSameName(line.getName()) || it.isSameColor(line.getColor()));
        if (isDuplicate) {
            throw new IllegalArgumentException(DUPLICATE_EXCEPTION_MESSAGE);
        }
    }

    public List<Line> showAll() {
        return lineDao.findAll();
    }

    public Line show(Long id) {
        return lineDao.findById(id);
    }

    public void update(Line line) {
        lineDao.update(line);
    }

    public void delete(Long id) {
    }
}
