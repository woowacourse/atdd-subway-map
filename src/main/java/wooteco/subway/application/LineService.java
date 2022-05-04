package wooteco.subway.application;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

public class LineService {

    public Line save(String name, String color) {
        return LineDao.save(new Line(name, color));
    }
}
