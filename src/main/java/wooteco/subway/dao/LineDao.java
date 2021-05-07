package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineName;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line insert(String color, LineName name) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO line (color, name) VALUES (?,?)";
        jdbcTemplate.update((Connection con) -> {
            PreparedStatement pstmt = con.prepareStatement(
                    query,
                    new String[]{"id"});
            pstmt.setString(1, color);
            pstmt.setString(2, name.getName());
            return pstmt;
        }, keyHolder);

        final long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Line(id, color, name);
    }

    public List<Line> findAll() {
        String query = "SELECT * FROM line";
        return jdbcTemplate.query(query,
                (resultSet, rowNum) -> new Line(
                        resultSet.getLong("id"),
                        resultSet.getString("color"),
                        resultSet.getString("name")
                )
        );
    }

    public Optional<Line> findById(Long id) {
        String query = "SELECT * FROM line WHERE id = ?";
        return jdbcTemplate.query(query,
                (resultSet, rowNum) -> new Line(
                        resultSet.getLong("id"),
                        resultSet.getString("color"),
                        resultSet.getString("name")
                ), id)
                .stream()
                .findAny();
    }

    public void update(Long id, String color, String name) {
        String query = "UPDATE line SET color = ?, name = ? WHERE id = ?";
        jdbcTemplate.update(query, color, name, id);
    }

    public void delete(Long id) {
        String query = "DELETE FROM line WHERE id = ?";
        jdbcTemplate.update(query, id);
    }
}
