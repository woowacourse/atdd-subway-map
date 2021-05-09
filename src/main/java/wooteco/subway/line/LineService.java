package wooteco.subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.line.LineNameDuplicatedException;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.line.dao.LineDao;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Transactional
    public Line create(Line line) {
        validateDuplicatedByName(line);
        return lineDao.save(line);
    }

    public List<Line> showAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        validateExistById(id);

        return lineDao.findById(id);
    }

    @Transactional
    public void update(Long id, Line line) {
        validateDuplicatedByName(line);
        validateExistById(id);

        Line previousLine = lineDao.findById(id);
        previousLine.changeInfo(line.getName(), line.getColor());

        lineDao.update(id, previousLine);
    }

    private void validateDuplicatedByName(Line line) {
        if (lineDao.existsByName(line.getName())) {
            throw new LineNameDuplicatedException();
        }
    }

    @Transactional
    public void removeById(Long id) {
        validateExistById(id);
        lineDao.removeById(id);
    }

    private void validateExistById(Long id) {
        if (!lineDao.existsById(id)) {
            throw new LineNotFoundException();
        }
    }

}
