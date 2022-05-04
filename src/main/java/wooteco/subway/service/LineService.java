package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public static void update(Line line) {
        LineDao.update(line);
    }

    public static void delete(Long id) {
        LineDao.delete(id);
    }

    public long save(Line line) {
        validateName(line);
        validateColor(line);
        return lineDao.save(line);
    }

    private void validateName(Line line) {
        if (lineDao.existLineByName(line.getName())) {
            throw new IllegalArgumentException("지하철 노선 이름이 중복됩니다.");
        }
    }

    private void validateColor(Line line) {
        if (lineDao.existLineByColor(line.getColor())) {
            throw new IllegalArgumentException("지하철 노선 색상이 중복됩니다.");
        }
    }
}
