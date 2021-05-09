package wooteco.subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.line.LineNameDuplicatedException;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.line.dao.LineDao;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Transactional
    public Line create(Line line) {
        if (lineDao.findByName(line.getName()).isPresent()) {
            throw new LineNameDuplicatedException();
        }
        return lineDao.save(line);
    }

    public List<Line> showAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id).orElseThrow(LineNotFoundException::new);
    }

    @Transactional
    public void update(Long id, Line line) {
        this.findById(id);
        final Optional<Line> lineByName = lineDao.findByName(line.getName());
        if (lineByName.isPresent() && lineByName.get().isNotSameId(id)) {
            throw new LineNameDuplicatedException();
        }
        lineDao.update(id, line);
    }

    @Transactional
    public void removeById(Long id) {
        lineDao.removeById(id);
    }

}
