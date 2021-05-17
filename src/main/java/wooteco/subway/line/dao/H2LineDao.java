package wooteco.subway.line.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineColor;
import wooteco.subway.line.domain.LineName;
import wooteco.subway.line.exception.WrongLineIdException;
import wooteco.subway.section.domain.OrderedSections;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class H2LineDao implements LineDao {
    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Line> LINE_ROW_MAPPER = (rs, rowNum) -> {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String color = rs.getString("color");
        return new Line(id, name, color, OrderedSections.emptySections());
    };

    public H2LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Line save(Line line) {
        String sql = "INSERT INTO LINE (NAME, COLOR) VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName().text());
            ps.setString(2, line.getColor().text());
            return ps;
        }, keyHolder);
        long lineId = keyHolder.getKey().longValue();
        return new Line(lineId, line.getName(), line.getColor(), OrderedSections.emptySections());
    }

    @Override
    public List<Line> findAll() {
        String sql = "SELECT * " +
                "FROM LINE";

        return jdbcTemplate.query(sql,
                LINE_ROW_MAPPER
        );
    }

    @Override
    public Line findById(Long id) {
        String sql = "SELECT * " +
                "FROM LINE " +
                "WHERE ID = ?";

        List<Line> queryResult = jdbcTemplate.query(sql,
                LINE_ROW_MAPPER,
                id
        );

        if (queryResult.isEmpty()) {
            throw new WrongLineIdException(String.format("데이터베이스에 해당 ID의 노선이 없습니다. ID : %d", id));
        }

        return queryResult.get(0);
    }

    @Override
    public boolean checkExistName(LineName name) {
        String sql = "SELECT COUNT(*) " +
                "FROM LINE " +
                "WHERE NAME = ?";

        int countOfName = jdbcTemplate.queryForObject(sql, Integer.class, name.text());
        return countOfName > 0;
    }

    @Override
    public boolean checkExistColor(LineColor color) {
        String sql = "SELECT COUNT(*) " +
                "FROM LINE " +
                "WHERE COLOR = ?";

        int countOfColor = jdbcTemplate.queryForObject(sql, Integer.class, color.text());
        return countOfColor > 0;
    }

    @Override
    public boolean checkExistId(Long id) {
        String sql = "SELECT COUNT(*) " +
                "FROM LINE " +
                "WHERE ID = ?";

        int countOfColor = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return countOfColor > 0;
    }

    @Override
    public void update(Line line) {
        String sql = "UPDATE LINE " +
                "SET NAME = ?, COLOR = ? " +
                "WHERE ID = ?";

        jdbcTemplate.update(sql, line.getName().text(), line.getColor().text(), line.getId());
    }

    @Override
    public void delete(Long lineId) {
        String sql = "DELETE " +
                "FROM LINE " +
                "WHERE ID = ?";

        jdbcTemplate.update(sql, lineId);
    }
}
