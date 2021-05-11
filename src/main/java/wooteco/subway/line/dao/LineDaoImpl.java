package wooteco.subway.line.dao;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.Line;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class LineDaoImpl implements LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> lineRowMapper;

    public LineDaoImpl(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        lineRowMapper = (rs, rowNum) ->
                new Line(rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("color"));
    }

    @Override
    public Line save(final Line line) {
        String sql = "INSERT INTO LINE(name, color) VALUES(?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.name());
            ps.setString(2, line.color());
            return ps;
        }, keyHolder);
        long newId = keyHolder.getKey().longValue();
        return new Line(newId, line.name(), line.color());
    }

    @Override
    public List<Line> findAll() {
        String sql = "SELECT * FROM LINE";

        return jdbcTemplate.query(sql, lineRowMapper);
    }

    @Override
    public Optional<Line> findById(final Long id) {
        String sql = "SELECT * FROM LINE WHERE id = ?";

        List<Line> line = jdbcTemplate.query(sql, lineRowMapper, id);
        return Optional.ofNullable(DataAccessUtils.singleResult(line));
    }

    @Override
    public Optional<Line> findByName(final String name) {
        String sql = "SELECT * FROM LINE WHERE name = ?";

        List<Line> line = jdbcTemplate.query(sql, lineRowMapper, name);
        return Optional.ofNullable(DataAccessUtils.singleResult(line));
    }

    @Override
    public void update(final Line line) {
        String sql = "UPDATE LINE SET name = ?, color = ? WHERE id = ? ";

        jdbcTemplate.update(sql, line.name(), line.color(), line.id());
    }

    @Override
    public void delete(final Long id) {
        String sql = "DELETE FROM LINE WHERE id = ?";

        int rowCount = jdbcTemplate.update(sql, id);
        if (rowCount == 0) {
            throw new IllegalStateException("[ERROR] 존재하지 않는 line_id입니다.");
        }
    }
}
