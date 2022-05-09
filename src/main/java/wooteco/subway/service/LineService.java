package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DuplicateLineNameException;
import wooteco.subway.exception.NoSuchLineException;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.entity.LineEntity;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line register(final String name, final String color) {
        final Line line = new Line(name, color);
        try {
            final LineEntity savedLineEntity = lineDao.save(new LineEntity(line));
            return savedLineEntity.generateLine();
        } catch (DuplicateKeyException exception) {
            throw new DuplicateLineNameException();
        }
    }

    public Line searchById(final Long id) {
        final LineEntity lineEntity = lineDao.findById(id).orElseThrow(() -> new NoSuchLineException());
        return lineEntity.generateLine();
    }

    public List<Line> searchAll() {
        return lineDao.findAll()
                .stream()
                .map(lineEntity -> new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor()))
                .collect(Collectors.toList());
    }

    public void modify(final Long id, final String name, final String color) {
        lineDao.update(new LineEntity(id, name, color));
    }

    public void remove(final Long id) {
        lineDao.deleteById(id);
    }
}
