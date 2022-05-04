package wooteco.subway.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

@Service
public class LineService {

    private static final String NON_EXISTENT_ID_EXCEPTION = "존재하지 않는 id입니다.";
    private static final String DUPLICATE_LINE_NAME_EXCEPTION = "중복된 노선 이름입니다.";

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Transactional
    public Line save(String name, String color) {
        if (lineDao.existsByName(name)){
            throw new IllegalArgumentException(DUPLICATE_LINE_NAME_EXCEPTION);
        }

        return lineDao.save(new Line(name, color));
    }

    @Transactional(readOnly = true)
    public List<Line> showLines() {
        return lineDao.findAll();
    }

    @Transactional(readOnly = true)
    public Line showLine(Long id) {
        if (lineDao.notExistsById(id)) {
            throw new IllegalArgumentException(NON_EXISTENT_ID_EXCEPTION);
        }

        return lineDao.findById(id);
    }

    @Transactional
    public void updateLine(Long id, String name, String color) {
        if (lineDao.notExistsById(id)) {
            throw new IllegalArgumentException(NON_EXISTENT_ID_EXCEPTION);
        }
        lineDao.updateLineById(id, name, color);
    }

    @Transactional
    public void deleteLine(Long id) {
        if (lineDao.notExistsById(id)) {
            throw new IllegalArgumentException(NON_EXISTENT_ID_EXCEPTION);
        }
        lineDao.deleteById(id);
    }
}
