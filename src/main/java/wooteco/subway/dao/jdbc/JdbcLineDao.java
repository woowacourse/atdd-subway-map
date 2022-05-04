package wooteco.subway.dao.jdbc;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

@Repository
public class JdbcLineDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcLineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long save(Line line) {
        String query = "INSERT INTO Line (name, color) values (?,?)";
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(query, new String[]{"id"});
            preparedStatement.setString(1, line.getName());
            preparedStatement.setString(2, line.getColor());
            return preparedStatement;
        }, generatedKeyHolder);
        return Objects.requireNonNull(generatedKeyHolder.getKey()).longValue();
    }

    @Override
    public List<Line> findAll() {
        String query = "SELECT id, name, color from Line";
        return jdbcTemplate.query(query,
                (resultSet, rowNum) -> new Line(
                        resultSet.getLong(1),
                        resultSet.getString(2),
                        resultSet.getString(3)
                ));
    }

    @Override
    public Line findById(Long id) {
        String query = "SELECT id, name, color from Line WHERE id=?";
        try {
            return jdbcTemplate.queryForObject(query,
                    (resultSet, rowNum) -> new Line(
                            resultSet.getLong(1),
                            resultSet.getString(2),
                            resultSet.getString(3)
                    ), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("해당 id에 맞는 지하철 노선이 없습니다.");
        }
    }

    @Override
    public Boolean existsByName(String name) {
        String query = "SELECT COUNT(*) FROM Line WHERE name=?";
        int count = jdbcTemplate.queryForObject(query,
                (resultSet, rowNum) -> resultSet.getInt(1),
                name);
        return count != 0;
    }

    @Override
    public Boolean existsByColor(String color) {
        String query = "SELECT COUNT(*) FROM Line WHERE color=?";
        int count = jdbcTemplate.queryForObject(query,
                (resultSet, rowNum) -> resultSet.getInt(1),
                color);
        return count != 0;
    }

    @Override
    public void update(Long id, String name, String color) {
        String query = "UPDATE Line SET name=?, color=? WHERE id=?";
        jdbcTemplate.update(query, name, color, id);
    }

    @Override
    public void remove(Long id) {
        String query = "DELETE FROM Line WHERE id=?";
        jdbcTemplate.update(query, id);
    }
}
