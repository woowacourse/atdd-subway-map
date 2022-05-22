package wooteco.subway.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color"),
            findLineStationsById(resultSet.getLong("id")));

    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name"));

    public List<Station> findLineStationsById(Long id) {
        Set<Station> stations = new HashSet<>();
        stations.addAll(findUpStationsById(id));
        stations.addAll(findDownStationsById(id));
        return new ArrayList<>(stations);
    }

    private List<Station> findUpStationsById(Long id) {
        final String sql = "SELECT STATION.id AS id, STATION.name AS name "
                + "FROM SECTION JOIN STATION ON SECTION.up_station_id = STATION.id WHERE line_id = ?";
        return jdbcTemplate.query(sql, stationRowMapper, id);
    }

    private List<Station> findDownStationsById(Long id) {
        final String sql = "SELECT STATION.id AS id, STATION.name AS name "
                + "FROM SECTION JOIN STATION ON SECTION.down_station_id = STATION.id WHERE line_id = ?";
        return jdbcTemplate.query(sql, stationRowMapper, id);
    }

    public Long save(Line line) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO LINE(name, color) VALUES(?, ?)";
        String name = line.getName();
        String color = line.getColor();
        jdbcTemplate.update((Connection conn) -> {
            PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"id"});
            pstmt.setString(1, name);
            pstmt.setString(2, color);
            return pstmt;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public boolean existByName(String name) {
        String sql = "SELECT EXISTS(SELECT id FROM LINE WHERE name = ?) AS SUCCESS";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name);
    }

    public List<Line> findAll() {
        String sql = "SELECT id, name, color FROM LINE";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Line findById(Long id) {
        String sql = "SELECT id, name, color FROM LINE WHERE id =?";
        return jdbcTemplate.queryForObject(sql, lineRowMapper, id);
    }

    public boolean existById(Long id) {
        String sql = "SELECT EXISTS(SELECT * FROM LINE WHERE id = ?) AS SUCCESS";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    public void update(Line line) {
        String sql = "UPDATE LINE SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
    }

    public void delete(Long id) {
        String sql = "DELETE FROM LINE WHERE id =?";
        jdbcTemplate.update(sql, id);
    }
}
