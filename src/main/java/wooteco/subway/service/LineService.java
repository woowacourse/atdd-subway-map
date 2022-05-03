package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

public class LineService {

    public static Line save(Line line) {
        validateName(line);
        validateColor(line);
        return LineDao.save(line);
    }

    private static void validateName(Line line) {
        if (getLineNames().contains(line.getName())) {
            throw new IllegalArgumentException("지하철 노선 이름이 중복됩니다.");
        }
    }

    private static List<String> getLineNames() {
        return LineDao.findAll().stream()
                .map(Line::getName)
                .collect(Collectors.toList());
    }

    private static void validateColor(Line line) {
        if (getLineColors().contains(line.getColor())) {
            throw new IllegalArgumentException("지하철 노선 색상이 중복됩니다.");
        }
    }

    private static List<String> getLineColors() {
        return LineDao.findAll().stream()
                .map(Line::getColor)
                .collect(Collectors.toList());
    }

    public static void update(Line line) {
        LineDao.update(line);
    }

    public static void delete(Long id) {
        LineDao.delete(id);
    }
}
