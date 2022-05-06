package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.entity.LineEntity;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Line register(final String name, final String color) {
        validateDuplicateName(name);
        final LineEntity lineEntity = new LineEntity(Line.createWithoutId(name, color));
        final LineEntity savedLineEntity = lineDao.save(lineEntity);
        return new Line(savedLineEntity.getId(), savedLineEntity.getName(), savedLineEntity.getColor());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Line searchById(final Long id) {
        LineEntity lineEntity = lineDao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 노선이 존재하지 않습니다"));
        return new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Line> searchAll() {
        return lineDao.findAll()
                .stream()
                .map(lineEntity -> new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor()))
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void modify(final Long id, final String name, final String color) {
        final Optional<LineEntity> lineEntityToBeModified = lineDao.findById(id);
        lineEntityToBeModified.ifPresentOrElse(
                lineEntity -> {
                    validateDuplicateName(name);
                    Line newLine = new Line(id, name, color);
                    lineDao.update(new LineEntity(newLine));
                },
                () -> {
                    throw new NoSuchElementException("[ERROR] 노선이 존재하지 않습니다");
                }
        );
    }

    private void validateDuplicateName(final String name) {
        if (lineDao.findByName(name).isPresent()) {
            throw new DuplicateNameException("[ERROR] 이미 존재하는 노선 이름입니다.");
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void remove(final Long id) {
        lineDao.deleteById(id);
    }
}
