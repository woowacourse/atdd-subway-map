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
        validateNameForSave(line);
        validateColorForSave(line);
        return lineDao.save(line);
    }

    private void validateNameForSave(Line line) {
        if (lineDao.existLineByName(line.getName())) {
            throw new IllegalArgumentException("지하철 노선 이름이 중복됩니다.");
        }
    }

    private void validateColorForSave(Line line) {
        if (lineDao.existLineByColor(line.getColor())) {
            throw new IllegalArgumentException("지하철 노선 색상이 중복됩니다.");
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        validateExistLine(id);
        return lineDao.findById(id);
    }

    private void validateExistLine(Long id) {
        if (!lineDao.existLineById(id)) {
            throw new IllegalArgumentException("존재하지 않는 지하철 노선입니다.");
        }
    }

    public void update(Line line) {
        validateExistLine(line.getId());
        validateNameForUpdate(line);
        validateColorForUpdate(line);
        lineDao.update(line);
    }

    private void validateNameForUpdate(Line line) {
        findAll().stream()
                .filter(it -> it.getName().equals(line.getName()))
                .filter(it -> !it.getId().equals(line.getId()))
                .findAny()
                .ifPresent(ignored -> {
                    throw new IllegalArgumentException("지하철 노선 이름이 중복됩니다.");
                });
    }

    private void validateColorForUpdate(Line line) {
        findAll().stream()
                .filter(it -> it.getColor().equals(line.getColor()))
                .filter(it -> !it.getId().equals(line.getId()))
                .findAny()
                .ifPresent(ignored -> {
                    throw new IllegalArgumentException("지하철 노선 색상이 중복됩니다.");
                });
    }

    public void delete(Long id) {
        validateExistLine(id);
        lineDao.delete(id);
    }
}
