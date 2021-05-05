package wooteco.subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
public class H2LineDao implements LineDao {
    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Line> lineRowMapper = (rs, rowNum) -> {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String color = rs.getString("color");
        return new Line(id, name, color);
    };


    public H2LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Line save(Line line) {
        String sql = "insert into LINE (name, color) values (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName().toString());
            ps.setString(2, line.getColor().toString());
            return ps;
        }, keyHolder);
        long lineId = keyHolder.getKey().longValue();
        return new Line(lineId, line.getName(), line.getColor());
    }

    @Override
    public List<Line> findAll() {
        String sql = "SELECT * " +
                "FROM LINE";

        return jdbcTemplate.query(sql,
                lineRowMapper
        );
    }

    @Override
    public Line findById(Long id) {
        String sql = "SELECT * " +
                "FROM LINE " +
                "WHERE id = ?";

        List<Line> queryResult = jdbcTemplate.query(sql,
                lineRowMapper,
                id
        );

        if (queryResult.isEmpty()) {
            throw new NoSuchElementException(String.format("데이터베이스에 해당 ID의 노선이 없습니다. ID : %d", id));
        }

        return queryResult.get(0);
    }

    @Override
    public boolean checkExistName(LineName name) {
        String sql = "SELECT count(*) " +
                "FROM LINE " +
                "WHERE name = ?";

        int countOfName = jdbcTemplate.queryForObject(sql, Integer.class, name.toString());
        return countOfName > 0;
    }

    @Override
    public boolean checkExistColor(LineColor color) {
        String sql = "SELECT count(*) " +
                "FROM LINE " +
                "WHERE color = ?";

        int countOfColor = jdbcTemplate.queryForObject(sql, Integer.class, color.toString());
        return countOfColor > 0;
    }

    @Override
    public boolean checkExistId(Long id) {
        String sql = "SELECT count(*) " +
                "FROM LINE " +
                "WHERE id = ?";

        int countOfColor = jdbcTemplate.queryForObject(sql, Integer.class, id.toString());
        return countOfColor > 0;
    }

    @Override
    public void update(Line line) {
        String sql = "UPDATE LINE " +
                "SET name = ?, color = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(sql, line.getName().toString(), line.getColor().toString(), line.getId());
    }

    @Override
    public void delete(Line line) {
        String sql = "DELETE " +
                "FROM LINE " +
                "WHERE id = ?";

        jdbcTemplate.update(sql, line.getId());
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE " +
                "FROM LINE";

        jdbcTemplate.update(sql);
    }
}
