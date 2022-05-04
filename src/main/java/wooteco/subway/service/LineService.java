package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;

public class LineService {

    public static LineResponse createLine(LineRequest lineRequest) {
        validateDuplicate(lineRequest.getName(), lineRequest.getColor());
        Line line = LineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        return new LineResponse(line);
    }

    public static LineResponse findLineInfos(Long id) {
        Line line = LineDao.findById(id);
        return new LineResponse(line);
    }

    public static List<LineResponse> findAll() {
        var allLines = LineDao.findAll();

        return allLines.stream()
                .map(it -> new LineResponse(it))
                .collect(Collectors.toList());
    }

    public static void updateById(Long id, String name, String color) {
        validateDuplicate(name, color);
        LineDao.updateById(id, name, color);
    }

    private static void validateDuplicate(String name, String color) {
        validateDuplicateName(name);
        validateDuplicateColor(color);
    }

    private static void validateDuplicateName(String name) {
        if (name == null || LineDao.existLineByName(name)) {
            throw new IllegalArgumentException("[ERROR] 중복된 이름이 존재합니다.");
        }
    }

    private static void validateDuplicateColor(String color) {
        if (color == null || LineDao.existLineByColor(color)) {
            throw new IllegalArgumentException("[ERROR] 중복된 이름이 존재합니다.");
        }
    }

    public static void deleteById(Long id) {
        if (LineDao.findById(id) == null) {
            throw new NoSuchElementException("[ERROR] 존재하지 않는 노선 입니다.");
        }
        LineDao.deleteById(id);
    }
}
