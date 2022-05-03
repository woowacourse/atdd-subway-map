package wooteco.subway.service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineResponse;

public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse create(String name, String color) {
        Line line = lineDao.save(new Line(name, color));
        return new LineResponse(line.getId(), line.getName(), line.getColor(), null);
    }
}
