package wooteco.subway.service;

import java.util.List;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DuplicatedLineException;

public class LineService {

    public static Line save(Line line) {
        if (LineDao.exists(line)) {
            throw new DuplicatedLineException();
        }
        return LineDao.save(line);
    }

    public static List<Line> findAll() {
        return LineDao.findAll();
    }
}
