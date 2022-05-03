package wooteco.subway.application;

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

    public Line save(String name, String color) {
        if (lineDao.existsByName(name)){
            throw new IllegalArgumentException("중복된 노선 이름입니다.");
        }

        return lineDao.save(new Line(name, color));
    }

    public List<Line> showLines() {
        return lineDao.findAll();
    }

    public Line showLine(Long id) {
        if (lineDao.notExistsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 id입니다.");
        }

        return lineDao.findById(id);
    }

    public void updateLine(Long id, String name, String color) {
        if (lineDao.notExistsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 id입니다.");
        }
        lineDao.updateLineById(id, name, color);
    }

    public void deleteLine(Long id) {
        if (lineDao.notExistsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 id입니다.");
        }
        lineDao.deleteById(id);
    }
}
