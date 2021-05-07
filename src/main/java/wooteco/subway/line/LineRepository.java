package wooteco.subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Repository
@Transactional
public class LineRepository {

    private final LineDao lineDao;

    public LineRepository(final JdbcTemplate jdbcTemplate) {
        this.lineDao = new LineDao(jdbcTemplate);
    }

    public Line save(final Line line) {
        validateName(line.getName());

        if (Objects.isNull(line.getId())) {
            return create(line);
        }

        return update(line);
    }

    private void validateName(final String name) {
        if (lineDao.isDuplicatedName(name)) {
            throw new LineException("이미 존재하는 노선 이름입니다.");
        }
    }

    private Line create(final Line line) {
        final Long id = lineDao.save(line.getName(), line.getColor());
        return lineDao.findById(id);
    }

    private Line update(final Line line) {
        lineDao.update(line.getId(), line.getName(), line.getColor());
        return lineDao.findById(line.getId());
    }

    public void delete(final Line line) {
        lineDao.delete(line.getId());
    }

    public Line findById(final Long id) {
        return lineDao.findById(id);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }
}
