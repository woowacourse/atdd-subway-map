package wooteco.subway.service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

public class LineService {
    private static final String ALREADY_IN_LINE_ERROR_MESSAGE = "이미 해당 이름의 노선이 있습니다.";

    public Line save(Line line) {
        if (isExistLineName(line)) {
            throw new IllegalArgumentException(ALREADY_IN_LINE_ERROR_MESSAGE);
        }
        return LineDao.save(line);
    }

    private boolean isExistLineName(Line line) {
        return LineDao.findAll()
                .stream()
                .anyMatch(inLine -> inLine.isSameName(line));
    }
}
