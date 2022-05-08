package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

@Service
public class LineService {

    private static final String ALREADY_IN_LINE_ERROR_MESSAGE = "이미 해당 이름의 노선이 있습니다.";
    private static final String NOT_EXIST_LINE_ID_ERROR_MESSAGE = "해당 아이디의 노선이 없습니다.";

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(Line line) {
        validateDuplicatedName(line.getName());
        return lineDao.save(line);
    }

    private void validateDuplicatedName(String name) {
        lineDao.findByName(name)
            .ifPresent(line -> {
                throw new IllegalStateException(ALREADY_IN_LINE_ERROR_MESSAGE);
            });
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public void update(Long id, Line line) {
        validateExistId(id);
        lineDao.update(id, line);
    }

    public void delete(Long id) {
        validateExistId(id);
        lineDao.delete(id);
    }

    private void validateExistId(Long id) {
        lineDao.findById(id)
            .orElseThrow(() -> new NoSuchElementException(NOT_EXIST_LINE_ID_ERROR_MESSAGE));
    }
}
