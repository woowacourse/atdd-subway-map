package wooteco.subway.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@Service
public class LineService {

    private static final String NAME_DUPLICATE_EXCEPTION_MESSAGE = "이름이 중복된 노선은 만들 수 없습니다.";
    private static final String COLOR_DUPLICATE_EXCEPTION_MESSAGE = "색깔이 중복된 노선은 만들 수 없습니다.";

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse insertLine(LineRequest lineRequest) {
        String name = lineRequest.getName();
        String color = lineRequest.getColor();
        validateDuplicateName(name);
        validateDuplicateColor(color);
        Line line = new Line(name, color);
        Line newLine = lineDao.insert(line);
        return new LineResponse(newLine);
    }

    private void validateDuplicateName(String name) {
        List<String> names = lineDao.findNames();
        if (names.contains(name)) {
            throw new IllegalArgumentException(NAME_DUPLICATE_EXCEPTION_MESSAGE);
        }
    }

    private void validateDuplicateColor(String color) {
        List<String> colors = lineDao.findColors();
        if (colors.contains(color)) {
            throw new IllegalArgumentException(COLOR_DUPLICATE_EXCEPTION_MESSAGE);
        }
    }

    public List<LineResponse> findLines() {
        List<Line> lines = Collections.unmodifiableList(lineDao.findAll());
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = lineDao.findById(id);
        return new LineResponse(line);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        String name = lineRequest.getName();
        String color = lineRequest.getColor();
        validateDuplicateName(name);
        validateDuplicateColor(color);
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }

    public void deleteLine(Long id) {
        lineDao.delete(id);
    }
}
