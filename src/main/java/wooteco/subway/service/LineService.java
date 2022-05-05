package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse findLineInfos(Long id) {
        findAll().stream()
                .filter(it -> it.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 해당 노선이 존재하지 않습니다."));

        Line line = lineDao.findById(id);
        return new LineResponse(line);
    }

    @Transactional(readOnly = true)
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
        boolean isDuplicated = isDuplicatedName(name);
        if (name == null || isDuplicated) {
            throw new IllegalArgumentException("[ERROR] 중복된 이름이 존재합니다.");
        }
    }

    private boolean isDuplicatedName(String name) {
        return lineDao.findAll().stream()
                .anyMatch(it -> it.getName().equals(name));
    }

    private void validateDuplicateColor(String color) {
        if (color == null || isDuplicatedColor(color)) {
            throw new IllegalArgumentException("[ERROR] 중복된 색이 존재합니다.");
        }
    }

    private boolean isDuplicatedColor(String color) {
        return lineDao.findAll().stream()
                .anyMatch(it -> it.getColor().equals(color));
    }

    @Transactional
    public void updateById(Long id, String name, String color) {
        validateNonFoundId(id);
        validateExistName(id, name);
        validateExistColor(id, color);

        lineDao.update(id, name, color);
    }

    @Transactional
    public void deleteById(Long id) {
        validateNonFoundId(id);

        lineDao.deleteById(id);
    }

    private void validateNonFoundId(Long id) {
        lineDao.findAll().stream()
                .filter(it -> it.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 노선 입니다."));
    }

    private void validateExistName(Long id, String name) {
        lineDao.findAll().stream()
                .filter(it -> !it.getId().equals(id))
                .filter(it -> it.getName().equals(name))
                .findAny()
                .ifPresent(s -> {
                    throw new NoSuchElementException("[ERROR] 이미 존재하는 이름입니다.");
                });
    }

    private void validateExistColor(Long id, String color) {
        lineDao.findAll().stream()
                .filter(it -> !it.getId().equals(id))
                .filter(it -> it.getName().equals(color))
                .findAny()
                .ifPresent(s -> {
                    throw new NoSuchElementException("[ERROR] 이미 존재하는 색상입니다.");
                });
    }
}
