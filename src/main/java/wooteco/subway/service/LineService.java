package wooteco.subway.service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

public class LineService {

    public static Line save(Line line) {
        return LineDao.save(line);
    }
}
