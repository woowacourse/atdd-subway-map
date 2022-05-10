package wooteco.subway.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class DbLineDao implements LineDao {

    private static final RowMapper<Line> ROW_MAPPER = (rs, rn) -> {
        long newId = rs.getLong("id");
        String name = rs.getString("name");
        String color = rs.getString("color");
        return new Line(newId, name, color);
    };
    private final JdbcTemplate jdbcTemplate;

    public DbLineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long save(Line line) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement("INSERT INTO LINE(name, color) VALUES(?, ?)", new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        long savedId = keyHolder.getKey().longValue();
        return savedId;
    }

    @Override
    public Optional<Line> findById(Long id) {
        try {
            Line line = jdbcTemplate
                    .queryForObject("SELECT id, name, color FROM LINE WHERE id = ? ", ROW_MAPPER, id);
            return Optional.of(line);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Line> findAll() {
        return jdbcTemplate.query("SELECT id, name, color FROM LINE", ROW_MAPPER);
    }

    @Override
    public boolean existById(Long id) {
        return findById(id).isPresent();
    }

    @Override
    public boolean existByName(String name) {
        return jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT id FROM LINE WHERE name = ? LIMIT 1 ) AS `exists`",
                Boolean.class, name);
    }

    @Override
    public boolean existByColor(String color) {
        return jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT color FROM LINE WHERE name = ? LIMIT 1 ) AS `exists`",
                Boolean.class, color);
    }

    @Override
    public void update(Line line) {
        jdbcTemplate.update("UPDATE LINE SET name = ?, color = ? WHERE id = ?",
                line.getName(),
                line.getColor(),
                line.getId());
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM LINE WHERE id = ?", id);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM LINE");
    }
}
