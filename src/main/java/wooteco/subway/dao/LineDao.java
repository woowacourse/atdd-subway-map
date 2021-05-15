package wooteco.subway.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

import java.util.List;
import java.util.Optional;

@Repository
public class LineDao implements LineRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public LineDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Line> lineRowMapper = (rs, rn) -> {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String color = rs.getString("color");
        return new Line(id, name, color);
    };

    @Override
    public Line save(Line line) {
        String query = "INSERT INTO LINE (name, color) VALUES (:name, :color)";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSourceBuilder()
                .setParam("name", line.getName())
                .setParam("color", line.getColor())
                .build();

        KeyHolder keyHolder = new GeneratedKeyHolder();

        this.jdbcTemplate.update(query, sqlParameterSource, keyHolder);

        return this.findById(keyHolder.getKey().longValue());
    }

    @Override
    public List<Line> findAll() {
        String query = "SELECT * FROM LINE";
        return jdbcTemplate.query(query, lineRowMapper);
    }

    @Override
    public Line findById(Long id) {
        String query = "SELECT * FROM LINE WHERE id = :id";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSourceBuilder()
                .setParam("id", id)
                .build();

        return jdbcTemplate.queryForObject(query, sqlParameterSource, lineRowMapper);
    }

    @Override
    public Optional<Line> findByName(String name) {
        String query = "SELECT * FROM LINE WHERE name = :name";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSourceBuilder()
                .setParam("name", name)
                .build();

        return this.jdbcTemplate.query(query, sqlParameterSource, (rs) -> {
            if (rs.next()) {
                long id = rs.getLong("id");
                String lineName = rs.getString("name");
                String color = rs.getString("color");
                return Optional.of(new Line(id, lineName, color));
            }
            return Optional.empty();
        });
    }

    @Override
    public Line update(Long id, Line newLine) {
        String query = "UPDATE LINE SET name = :name, color = :color WHERE id = :id";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSourceBuilder()
                .setParam("name", newLine.getName())
                .setParam("color", newLine.getColor())
                .setParam("id", id)
                .build();

        this.jdbcTemplate.update(query, sqlParameterSource);
        return this.findById(id);
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM LINE WHERE id = :id";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSourceBuilder()
                .setParam("id", id)
                .build();

        jdbcTemplate.update(query, sqlParameterSource);
    }
}
