package wooteco.subway.service;

import java.util.Optional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;

public class LineService {

    private final LineDao LineDao;

    public LineService(LineDao LineDao) {
        this.LineDao = LineDao;
    }

    public Line save(LineRequest LineRequest) {
        Optional<Line> findLine = LineDao.findByName(LineRequest.getName());
        if (findLine.isPresent()) {
            throw new IllegalArgumentException("중복된 ID가 존재합니다");
        }
        Line Line = new Line(LineRequest.getName(), LineRequest.getColor());
        return LineDao.save(Line);
    }
}
