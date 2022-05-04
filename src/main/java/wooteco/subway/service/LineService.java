package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
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

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line find(Long id) {
        return lineDao.find(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 노선입니다."));
    }

    public void update(long id, Line line) {
        int updatedRow = lineDao.update(id, line);
        if (updatedRow == 0) {
            throw new IllegalArgumentException("존재하지 않는 지하철 노선입니다.");
        }
    }

    public void delete(Long id) {
        int deletedRow = lineDao.delete(id);
        if (deletedRow == 0) {
            throw new IllegalArgumentException("존재하지 않는 지하철 노선입니다.");
        }
        ;
    }
}
