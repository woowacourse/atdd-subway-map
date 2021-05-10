package wooteco.subway.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class LineJdbcDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Line save(final Line line) {
        String sql = "INSERT INTO LINE (name, color, top_station_id, bottom_station_id) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            ps.setLong(3, line.getTopStationId());
            ps.setLong(4, line.getBottomStationId());
            return ps;
        }, keyHolder);

        Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Line(generatedId, line);
    }

    @Override
    public List<Line> findAll() {
        String sql = "SELECT l.id, l.name, l.color, l.top_station_id, l.bottom_station_id FROM LINE l";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            final long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String color = rs.getString("color");
            final Long topStationId = rs.getLong("top_station_id");
            final Long bottomStationId = rs.getLong("bottom_station_id");
            return new Line(id, name, color, topStationId, bottomStationId);
        });
    }

    @Override
    public Optional<Line> findByName(String name) {
        String sql = "SELECT l.id, l.name, l.color, l.top_station_id, l.bottom_station_id FROM LINE l WHERE l.name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                final long id = rs.getLong("id");
                final String color = rs.getString("color");
                final Long topStationId = rs.getLong("top_station_id");
                final Long bottomStationId = rs.getLong("bottom_station_id");
                return Optional.of(new Line(id, name, color, topStationId, bottomStationId));
            }, name);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Line> findById(Long id) {
        String sql = "SELECT l.name, l.color, l.top_station_id, l.bottom_station_id FROM LINE l WHERE l.id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                final String name = rs.getString("name");
                final String color = rs.getString("color");
                final Long topStationId = rs.getLong("top_station_id");
                final Long bottomStationId = rs.getLong("bottom_station_id");
                return Optional.of(new Line(id, name, color, topStationId, bottomStationId));
            }, id);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void update(final Long id, final Line line) {
        String sql = "UPDATE LINE l SET l.name = ?, l.color = ? WHERE l.id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
    }

    @Override
    public void updateTopStationId(final Section section) {
        String sql = "UPDATE LINE l SET l.top_station_id = ? WHERE l.id = ?";
        jdbcTemplate.update(sql, section.getUpStationId(), section.getLineId());
    }

    @Override
    public void updateBottomStationId(final Section section) {
        String sql = "UPDATE LINE l SET l.bottom_station_id = ? WHERE l.id = ?";
        jdbcTemplate.update(sql, section.getDownStationId(), section.getLineId());
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM LINE l WHERE l.id = ?";
        jdbcTemplate.update(sql, id);
    }
}
