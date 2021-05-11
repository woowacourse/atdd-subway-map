package wooteco.subway.line.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcLineDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Line save(Line line) {
        String sql = "INSERT INTO line (name, color) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, line.getName());
            preparedStatement.setString(2, line.getColor());
            return preparedStatement;
        }, keyHolder);
        return Line.of(keyHolder.getKey().longValue(), line.getName(), line.getColor(), line.getSections());
    }

    @Override
    public Optional<Line> findByNameAndColor(String name, String color) {
        String sql = "SELECT * FROM line WHERE (name = ? OR color = ?)";
        return jdbcTemplate.query(sql, getRowMapper(), name, color).stream().findAny();
    }

    @Override
    public Optional<Line> findById(Long lineId) {
        String sql = "SELECT * FROM line WHERE id = ?";
        return jdbcTemplate.query(sql, getRowMapper(), lineId).stream().findAny();
    }

    @Override
    public List<Line> findAll() {
        String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, getRowMapper());
    }

    private RowMapper getRowMapper() {
        RowMapper rowMapper = new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                Long id = rs.getLong("id");
                String name = rs.getString("name");
                String color = rs.getString("color");
                return Line.of(id, name, color);
            }
        };
        return rowMapper;
    }
}
