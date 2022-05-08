package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getString("name"),
            resultSet.getString("color"),
            resultSet.getLong("distance"));

    public Line save(Line line) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into line (up_station_id, down_station_id, name, color, distance) values (?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, line.getUpStationId());
            ps.setLong(2, line.getDownStationId());
            ps.setString(3, line.getName());
            ps.setString(4, line.getColor());
            ps.setLong(5, line.getDistance());
            return ps;
        }, keyHolder);
        long insertedId = keyHolder.getKey().longValue();

        return new Line(insertedId, line.getUpStationId(), line.getDownStationId(), line.getName(), line.getColor(), line.getDistance());
    }

    public List<Line> findAll() {
        String sql = "select id, up_station_id, down_station_id, name, color, distance from line";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Long deleteById(Long id) {
        String sql = "delete from line where id = (?)";
        this.jdbcTemplate.update(sql,id);
        return id;
    }

    public Line findById(Long id) {
        String sql = "select id, up_station_id, down_station_id, name, color, distance from line where id = (?)";
        return jdbcTemplate.queryForObject(sql, lineRowMapper, id);
    }

    public void changeLineName(Long id, String newName) {
        String sql = "update line set name = (?) where id = (?)";
        jdbcTemplate.update(sql, newName, id);
    }
}
