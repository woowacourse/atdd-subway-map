package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
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
        validateDuplicate(lineRequest.getName(), lineRequest.getColor());
        Line line = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        return new LineResponse(line);
    }

    private void validateDuplicate(String name, String color) {
        validateDuplicateName(name);
        validateDuplicateColor(color);
    }

    private void validateDuplicateName(String name) {
        if (isDuplicatedName(name)) {
            throw new IllegalArgumentException("[ERROR] 중복된 이름이 존재합니다.");
        }
    }

    private boolean isDuplicatedName(String name) {
        return lineDao.findAll().stream()
                .anyMatch(it -> it.getName().equals(name));
    }

    private void validateDuplicateColor(String color) {
        if (isDuplicatedColor(color)) {
            throw new IllegalArgumentException("[ERROR] 중복된 색이 존재합니다.");
        }
    }

    private boolean isDuplicatedColor(String color) {
        return lineDao.findAll().stream()
                .anyMatch(it -> it.getColor().equals(color));
    }

    public void updateById(Long id, String name, String color) {
        checkLineId(id);

        lineDao.findAll().stream()
                .filter(it -> !it.getId().equals(id))
                .filter(it -> it.getName().equals(name) || it.getColor().equals(color))
                .findAny()
                .ifPresent(s -> {
                    throw new NoSuchElementException("[ERROR] 이미 존재하는 이름, 색상입니다.");
                });

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
