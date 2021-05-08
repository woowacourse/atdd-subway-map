package wooteco.subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(final Line line) {
        validateName(line.getName());

        if (Objects.isNull(line.getId())) {
            return create(line);
        }

        return update(line);
    }

    private Line create(final Line line) {
        final Long id = lineDao.save(line.getName(), line.getColor());
        return findById(id);
    }

    private Line update(final Line line) {
        lineDao.update(line.getId(), line.getName(), line.getColor());
        return findById(line.getId());
    }

    private void validateName(final String name) {
        if (lineDao.isExistingName(name)) {
            throw new LineException("이미 존재하는 노선 이름입니다.");
        }
    }

    public void delete(final Long id) {
        lineDao.delete(id);
    }

    public Line findById(final Long id) {
        final Optional<Line> line = lineDao.findById(id);
        return line.orElseThrow(() -> new LineException("존재하지 않는 노선입니다."));
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }
}
