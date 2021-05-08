package wooteco.subway.line;

import com.sun.tools.internal.ws.wsdl.framework.DuplicateEntityException;
import org.springframework.stereotype.Service;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;

import java.util.List;

@Service
public class LineService {
    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Long save(Line line) {
        if (lineDao.countLineByName(line.getName()) > 0) {
            throw new IllegalArgumentException("중복된 노선입니다.");
        }
        return lineDao.save(line);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public void update(Long id, Line line) {
        lineDao.update(id, line);
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }

    public Line findLineById(Long id) {
        return lineDao.findLineById(id);
    }
}
