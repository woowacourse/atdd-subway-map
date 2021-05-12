package wooteco.subway.dao;

import org.springframework.beans.factory.annotation.Autowired;
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
public class LineH2Dao implements LineRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> lineRowMapper = (rs, rn) -> {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String color = rs.getString("color");
        return new Line(id, name, color);
    };

    public LineH2Dao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Line save(Line line) {
        String query = "INSERT INTO LINE (name, color) VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        this.jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        return this.findById(keyHolder.getKey().longValue());
    }

    @Override
    public List<Line> findAll() {
        String query = "SELECT * FROM LINE";
        return jdbcTemplate.query(query, lineRowMapper);
    }

    @Override
    public Line findById(Long id) {
        String query = "SELECT * FROM LINE WHERE id = ?";
        return jdbcTemplate.queryForObject(query, lineRowMapper, id);
    }

    @Override
    public Optional<Line> findByName(String name) {
        String query = "SELECT * FROM LINE WHERE name = ?";
        return this.jdbcTemplate.query(query, (rs) -> {
            if (rs.next()) {
                long id = rs.getLong("id");
                String lineName = rs.getString("name");
                String color = rs.getString("color");
                return Optional.of(new Line(id, lineName, color));
            }
            return Optional.empty();
        }, name);
    }

    @Override
    public Line update(Long id, Line newLine) {
        String query = "UPDATE LINE SET name = ?, color = ? WHERE id = ?";
        this.jdbcTemplate.update(query, newLine.getName(), newLine.getColor(), id);
        return this.findById(id);
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM LINE WHERE id = ?";
        jdbcTemplate.update(query, id);
    }
}
