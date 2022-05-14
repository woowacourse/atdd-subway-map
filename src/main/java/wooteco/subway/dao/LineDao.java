package wooteco.subway.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Repository
public class LineDao {

    private static final String NON_EXISTENT_ID_EXCEPTION = "존재하지 않는 id입니다.";

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("line").usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", line.getName());
        parameters.put("color", line.getColor());

        final Number number = simpleJdbcInsert.executeAndReturnKey(parameters);
        return Line.createWithoutSection(number.longValue(), line.getName(), line.getColor());
    }

    public boolean existsByName(String name) {
        final String sql = "SELECT COUNT(*) FROM line WHERE name = ?";
        final Integer numOfLine = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return !numOfLine.equals(0);
    }

    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, this::lineMapper);
    }

    public Line findById(Long id) {
        final String sql = "SELECT * FROM line WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, this::lineMapper, id);
    }

    private Line lineMapper(ResultSet rs, int rowNum) throws SQLException {
        return Line.createWithId(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("color"),
                findSectionsByLineId(rs.getLong("id"))
        );
    }

    private List<Section> findSectionsByLineId(Long lineId) {
        final String sql = "select s.id sid, s.distance sdistance, us.id usid, us.name usname, ds.id dsid, ds.name dsname " +
                "from sections s " +
                "join station us on s.up_station_id = us.id " +
                "join station ds on s.down_station_id = ds.id " +
                "where line_id = ?";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> {
            return Section.createWithId(rs.getLong("sid"), new Station(rs.getLong("usid"), rs.getString("usname")),
                    new Station(rs.getLong("dsid"), rs.getString("dsname")), rs.getInt("sdistance"));
        }), lineId);
    }

    public void updateLineById(Long id, String name, String color) {
        final String sql = "UPDATE line SET name=?, color=? WHERE id=?";
        validateResult(jdbcTemplate.update(sql, name, color, id));
    }

    public void deleteById(Long id) {
        final String sql = "DELETE FROM line WHERE id = ?";
        validateResult(jdbcTemplate.update(sql, id));
    }

    private void validateResult(int result) {
        if (result == 0) {
            throw new IllegalArgumentException(NON_EXISTENT_ID_EXCEPTION);
        }
    }
}
