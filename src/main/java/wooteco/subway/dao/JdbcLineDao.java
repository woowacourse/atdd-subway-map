package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class JdbcLineDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcLineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> {
        Line line = new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color")
        );
        return line;
    };

    public Long save(Line line) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into LINE (name, color) values (?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, line.getName());
            preparedStatement.setString(2, line.getColor());
            return preparedStatement;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public List<Line> findAll() {
        String sql = "select * from LINE";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public void deleteById(Long lineId) {
        String sql = "delete from LINE where id = (?)";
        jdbcTemplate.update(sql, lineId);
    }

    public Line findById(Long lineId) {
        String sql = "select * from LINE where id = (?)";

        return jdbcTemplate.queryForObject(sql, lineRowMapper, lineId);
    }

    public void update(Long lineId, Line line) {
        String sql = "update LINE set name = (?), color = (?) where id = (?)";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), lineId);
    }
}
