package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

@Service
public class LineService {
    private static final String ALREADY_IN_LINE_ERROR_MESSAGE = "이미 해당 이름의 노선이 있습니다.";

    private final LineDao lineDao;
    private final CheckService checkService;

    public LineService(LineDao lineDao, CheckService checkService) {
        this.lineDao = lineDao;
        this.checkService = checkService;
    }

    @Transactional
    public Line save(Line line) {
        if (lineDao.hasLine(line.getName())) {
            throw new IllegalArgumentException(ALREADY_IN_LINE_ERROR_MESSAGE);
        }
        Long id = lineDao.save(line);
        return lineDao.findById(id);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        checkService.checkLineExist(id);
        return lineDao.findById(id);
    }

    @Transactional
    public void update(Long id, Line line) {
        checkService.checkLineExist(id);
        lineDao.update(id, line);
    }
}
