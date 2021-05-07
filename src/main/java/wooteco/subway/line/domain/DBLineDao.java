package wooteco.subway.line.domain;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class DBLineDao implements LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> lineRowMapper = (rs, rowNum) ->
            new Line(rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("color"));

    public DBLineDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Line save(final Line line) {
        validateDuplicate(line);
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
        if (line.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(line.get(0));
    }

    @Override
    public Optional<Line> findByName(final String name) {
        String sql = "SELECT * FROM LINE WHERE name = ?";

        List<Line> line = jdbcTemplate.query(sql, lineRowMapper, name);
        if (line.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(line.get(0));
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("DB 전체 삭제는 불가능!");
    }

    @Override
    public void update(final Long id, final String name, final String color) {
        String sql = "UPDATE LINE SET name = ?, color = ? WHERE id = ? ";

        jdbcTemplate.update(sql, name, color, id);
    }

    @Override
    public void delete(final Long id) {
        String sql = "DELETE FROM LINE WHERE id = ?";
        int rowCount = jdbcTemplate.update(sql, id);

        if(rowCount == 0) {
            throw new IllegalStateException("존재하지 않는 id임");
        }
    }

    private void validateDuplicate(final Line line) {
        if (findByName(line.name()).isPresent()) {
            throw new IllegalStateException("이미 있는 역임!");
        }
    }
}
