package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;

@Service
public class LineService {
    private static final String ALREADY_IN_LINE_ERROR_MESSAGE = "이미 해당 이름의 노선이 있습니다.";
    private static final String NO_ID_ERROR_MESSAGE = "해당 아이디의 노선이 없습니다.";

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public Line save(Line line) {
        validateUniqueName(line.getName());
        Long id = lineDao.save(line);
        return lineDao.findById(id);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        validateID(id);
        return lineDao.findById(id);
    }

    public void update(Long id, Line line) {
        validateID(id);
        lineDao.update(id, line);
    }

    public void delete(Long id) {
        validateID(id);
        sectionDao.deleteAllByLineId(id);
        lineDao.delete(id);
    }

    private void validateUniqueName(String name) {
        if (lineDao.hasLine(name)) {
            throw new IllegalArgumentException(ALREADY_IN_LINE_ERROR_MESSAGE);
        }
    }

    private void validateID(Long id) {
        if (!lineDao.hasLine(id)) {
            throw new IllegalArgumentException(NO_ID_ERROR_MESSAGE);
        }
    }
}
