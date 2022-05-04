package wooteco.subway.application;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DuplicateException;

public class LineService {

    public Line save(String name, String color) {
        if(LineDao.existByName(name)) {
            throw new DuplicateException();
        }
        return LineDao.save(new Line(name, color));
    }
}
