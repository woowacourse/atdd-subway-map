package wooteco.subway.line.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.Line;

import java.util.List;
import java.util.Optional;

@Repository
public class LineH2Dao implements LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineH2Dao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Line save(Line line) {
        return null;
    }

    @Override
    public Optional<Line> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Line> findByName(String lineName) {
        return Optional.empty();
    }

    @Override
    public List<Line> findAll() {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void update(Line newLine) {

    }
}
