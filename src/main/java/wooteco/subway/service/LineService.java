package wooteco.subway.service;

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
}
