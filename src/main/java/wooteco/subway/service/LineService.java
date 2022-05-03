package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import wooteco.subway.domain.Line;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.entity.LineEntity;

public class LineService {

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line register(final String name, final String color) {
        validateDuplicateName(name);
        final LineEntity lineEntity = new LineEntity(new Line(name, color));
        final LineEntity savedEntity = lineDao.save(lineEntity);
        return new Line(savedEntity.getId(), savedEntity.getName(), savedEntity.getColor());
    }

    private void validateDuplicateName(final String name) {
        final Optional<LineEntity> lineEntity = lineDao.findByName(name);
        if (lineEntity.isPresent()) {
            throw new IllegalArgumentException("[ERROR] 이미 존재하는 노선 이름입니다.");
        }
    }

    public Line searchById(final Long id) {
        LineEntity lineEntity = lineDao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 노선이 존재하지 않습니다"));
        return new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor());
    }

    public List<Line> searchAll() {
        return lineDao.findAll()
                .stream()
                .map(lineEntity -> new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor()))
                .collect(Collectors.toList());
    }

    public void modify(final Long id, final String name, final String color) {
        if (lineDao.findById(id).isEmpty()) {
            throw new NoSuchElementException("[ERROR] 노선이 존재하지 않습니다");
        }
        Line newLine = new Line(id, name, color);
        lineDao.update(new LineEntity(newLine));
    }

    public void remove(final Long id) {
        lineDao.deleteById(id);
    }
}
