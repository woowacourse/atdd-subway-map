package wooteco.subway.dao;


import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.entity.LineEntity;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<LineEntity> lineRowMapper = (resultSet, rowNum) -> new LineEntity(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getLong("distance")
    );

    public LineEntity save(Line line) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into line (name, color, up_station_id, down_station_id, distance) values (?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            ps.setLong(3, line.getUpStation().getId());
            ps.setLong(4, line.getDownStation().getId());
            ps.setLong(5, line.getDistance());
            return ps;
        }, keyHolder);
        long insertedId = keyHolder.getKey().longValue();

        return new LineEntity(insertedId, line.getName(), line.getColor(), line.getUpStation().getId(),
                line.getDownStation().getId(), line.getDistance());
    }

    public List<LineEntity> findAll() {
        String sql = "select id, name, color, down_station_id, up_station_id, distance from line";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Long deleteById(Long id) {
        String sql = "delete from line where id = (?)";
        this.jdbcTemplate.update(sql, id);
        return id;
    }

    public Optional<LineEntity> findById(Long id) {
        String sql = "select id, name, color, down_station_id, up_station_id, distance from line where id = (?)";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, lineRowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void changeLineName(Long id, String newName) {
        String sql = "update line set name = (?) where id = (?)";
        jdbcTemplate.update(sql, newName, id);
    }
}
