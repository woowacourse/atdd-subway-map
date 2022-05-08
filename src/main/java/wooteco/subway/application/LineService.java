package wooteco.subway.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Transactional
    public Line save(String name, String color) {
        checkExistsName(name);
        return lineDao.save(new Line(name, color));
    }

    @Transactional(readOnly = true)
    public List<Line> showLines() {
        return lineDao.findAll();
    }

    @Transactional(readOnly = true)
    public Line showLine(Long id) {
        checkExistsId(id);
        return lineDao.findById(id);
    }

    @Transactional
    public int updateLine(Long id, String name, String color) {
        checkExistsId(id);
        checkExistsName(name);
        return lineDao.updateLineById(id, name, color);
    }

    @Transactional
    public int deleteLine(Long id) {
        checkExistsId(id);
        return lineDao.deleteById(id);
    }

    private void checkExistsName(String name) {
        if (lineDao.existsByName(name)){
            throw new IllegalArgumentException("중복된 노선 이름입니다.");
        }
    }

    private void checkExistsId(Long id) {
        if (lineDao.notExistsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 id입니다.");
        }
    }
}
