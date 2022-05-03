package wooteco.subway.service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

public class LineService {

    public static LineResponse createLine(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = LineDao.save(line);
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }
}
