package wooteco.subway.dao.line;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import wooteco.subway.domain.line.Line;

@Component
@Primary
public class JdbcLineDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final LineMapper mapper;

    public JdbcLineDao(JdbcTemplate jdbcTemplate, LineMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public Line save(Line line) {
        String query = "insert into LINE (name, color) values (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update((con) -> {
            PreparedStatement ps = con.prepareStatement(query, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);
        return new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    @Override
    public List<Line> findAll() {
        String query = "SELECT * FROM Line";
        return jdbcTemplate.query(query, mapper);
    }

    @Override
    public Line findById(Long id) {
        String query = "select * from LINE where id = ?";
        return jdbcTemplate.queryForObject(query, mapper, id);
    }

    @Override
    public void update(Line updatedLine) {
        String query = "update LINE set name = ?, color = ? where id = ?";
        jdbcTemplate
            .update(query, updatedLine.getName(), updatedLine.getColor(), updatedLine.getId());
    }

    @Override
    public void deleteById(Long id) {
        String query = "delete from LINE where id = ?";
        jdbcTemplate.update(query, id);
    }
}
