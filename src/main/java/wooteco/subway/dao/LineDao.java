package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

@Repository
public class LineDao {

    private static final RowMapper<Line> LINE_ROW_MAPPER = (resultSet, rowNum) -> {
        return new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color")
        );
    };

    private static final RowMapper<Station> STATION_ROW_MAPPER = (resultSet, rowNum) -> {
        return new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
    };

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(final Line line) {
        final String sql = "insert into LINE (name, color) values (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, line.getName());
            preparedStatement.setString(2, line.getColor());
            return preparedStatement;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public boolean existLineById(final Long id) {
        final String sql = "select exists (select * from LINE where id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    public boolean existLineByName(final String name) {
        final String sql = "select exists (select * from LINE where name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name);
    }

    public boolean existLineByColor(final String color) {
        final String sql = "select exists (select * from LINE where color = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, color);
    }

    public List<Line> findAll() {
        final String sql = "select id, name, color from LINE";
        return jdbcTemplate.query(sql, LINE_ROW_MAPPER);
    }

    public List<Station> findStations(final Long id) {
        final String sql = "SELECT STATION.id, name FROM STATION " +
                "JOIN " +
                "( " +
                "(SELECT up_station_id as id FROM SECTION WHERE line_id = ?) " +
                "UNION " +
                "(SELECT down_station_id as id FROM SECTION WHERE line_id = ?) " +
                ") " +
                "AS STATION_IN_LINE " +
                "ON STATION.id = STATION_IN_LINE.id";
        return jdbcTemplate.query(sql, STATION_ROW_MAPPER, id, id);
    }

    public Optional<Line> find(final Long id) {
        final String sql = "select id, name, color from LINE where id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, LINE_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public void update(final long id, final Line line) {
        final String sql = "update LINE set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
    }

    public void delete(final Long id) {
        final String sql = "delete from LINE where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
