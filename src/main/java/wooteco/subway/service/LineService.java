package wooteco.subway.service;

import java.util.List;
import java.util.Optional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

public class LineService {

    private static final LineService INSTANCE = new LineService(LineDao.getInstance());
    private final LineDao lineDao;

    private LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public static LineService getInstance() {
        return INSTANCE;
    }

    public Line save(Line line) {
        Optional<Line> foundLine = lineDao.findByName(line.getName());
        if (foundLine.isPresent()) {
            throw new IllegalArgumentException("이미 등록된 노선입니다.");
        }
        return lineDao.save(line);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }
}
