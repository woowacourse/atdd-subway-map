package wooteco.subway.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.badRequest.LineInfoDuplicatedException;
import wooteco.subway.exception.notFound.LineNotFoundException;
import wooteco.subway.domain.Line;
import wooteco.subway.dao.line.LineDao;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LineService {

    private final LineDao lineDao;

    @Transactional
    public Line createLine(Line line) {
        if (lineDao.findLineByName(line.getName()).isPresent()) {
            throw new LineInfoDuplicatedException();
        }
        return lineDao.save(line);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findLine(Long id) {
        return lineDao.findLineById(id).orElseThrow(LineNotFoundException::new);
    }

    @Transactional
    public void update(Long id, String name, String color) {
        lineDao.findLineById(id).orElseThrow(LineNotFoundException::new);
        if (lineDao.findLineByNameOrColor(name, color, id).isPresent()) {
            throw new LineInfoDuplicatedException();
        }
        lineDao.update(id, name, color);
    }

    @Transactional
    public void removeLine(Long id) {
        lineDao.removeLine(id);
    }
}
