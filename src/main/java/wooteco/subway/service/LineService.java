package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicateNameException;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line createLine(final Line line) {
        validateDuplicateName(line);
        return lineDao.save(line);
    }

    public List<Line> getAllLines() {
        return lineDao.findAll();
    }

    public Line getLineById(final Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 노선 ID입니다."));
    }

    public void update(final Long id, final Line newLine) {
        validateDuplicateName(newLine);
        final Line result = getLineById(id);
        result.update(newLine);

        lineDao.update(id, result);
    }

    public void delete(final Long id) {
        validateExist(id);
        lineDao.deleteById(id);
    }

    private void validateDuplicateName(final Line line) {
        if (lineDao.existByName(line.getName())) {
            throw new DuplicateNameException("이미 존재하는 노선입니다.");
        }
    }

    private void validateExist(final Long id) {
        if (!lineDao.existById(id)) {
            throw new DataNotFoundException("대상 노선 ID가 존재하지 않습니다.");
        }
    }
}
