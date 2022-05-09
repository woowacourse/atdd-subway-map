package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse findLineInfos(Long id) {
        var line = lineDao.findById(id);
        return new LineResponse(line);
    }

    public List<LineResponse> findAll() {
        var allLines = lineDao.findAll();

        return allLines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse createLine(LineRequest lineRequest) {
        checkDuplicateNameAndColor(lineDao.findAll(), lineRequest.getName(), lineRequest.getColor());
        var line = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        return new LineResponse(line);
    }

    private void checkDuplicateNameAndColor(List<Line> lines, String name, String color) {
        checkDuplicateName(lines, name);
        checkDuplicateColor(lines, color);
    }

    private void checkDuplicateName(List<Line> lines, String name) {
        if (isDuplicatedName(lines, name)) {
            throw new IllegalArgumentException("[ERROR] 중복된 이름이 존재합니다.");
        }
    }

    private boolean isDuplicatedName(List<Line> lines, String name) {
        return lines.stream()
                .anyMatch(it -> it.getName().equals(name));
    }

    private void checkDuplicateColor(List<Line> lines, String color) {
        if (isDuplicatedColor(lines, color)) {
            throw new IllegalArgumentException("[ERROR] 중복된 색이 존재합니다.");
        }
    }

    private boolean isDuplicatedColor(List<Line> lines, String color) {
        return lines.stream()
                .anyMatch(it -> it.getColor().equals(color));
    }

    public void updateById(Long id, String name, String color) {
        checkLineId(id);
        var lines = lineDao.findAll().stream()
                .filter(it -> !it.getId().equals(id))
                .collect(Collectors.toList());

        checkDuplicateNameAndColor(lines, name, color);

        lineDao.update(id, name, color);
    }

    private void checkLineId(Long id) {
        lineDao.findById(id);
    }

    public void deleteById(Long id) {
        checkLineId(id);

        lineDao.deleteById(id);
    }
}
