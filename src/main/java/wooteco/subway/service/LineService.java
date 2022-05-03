package wooteco.subway.service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

public class LineService {

    public static LineResponse createLine(LineRequest lineRequest) {
        if (LineDao.existStationByName(lineRequest.getName())) {
            throw new IllegalArgumentException("[ERROR] 중복된 이름이 존재합니다.");
        }
        Line line = LineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public static LineResponse findLineInfos(Long id) {
        Line line = LineDao.findById(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }
}
