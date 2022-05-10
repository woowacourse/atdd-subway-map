package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

@Service
public class LineService {

    private static final String ALREADY_IN_LINE_ERROR_MESSAGE = "이미 해당 이름의 노선이 있습니다.";
    private static final String NOT_EXIST_LINE_ID_ERROR_MESSAGE = "해당 아이디의 노선이 없습니다.";

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public Line save(Line line) {
        validateDuplicatedName(line.getName());
        Line savedLine = lineDao.save(line);
        Section section = new Section(savedLine.getId(), line.getUpStationId(), line.getDownStationId(),
            line.getDistance());
        sectionDao.save(section);
        return savedLine;
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

    public Line findById(Long id) {
        Optional<Line> line = lineDao.findById(id);
        return line.orElseThrow(() -> new NoSuchElementException(NOT_EXIST_LINE_ID_ERROR_MESSAGE));
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
