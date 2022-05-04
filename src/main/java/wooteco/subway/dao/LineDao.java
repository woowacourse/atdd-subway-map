package wooteco.subway.dao;

import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Line;

@Repository
public class LineDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) ->
            new Line(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color"));;

    public LineDao(final DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
    }

    public Line save(final String name, final String color) {
        checkDuplicateName(name);
        SqlParameterSource parameters = new MapSqlParameterSource("name", name)
                .addValue("color", color);
        Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Line(id, name, color);
    }

    private void checkDuplicateName(String name) {
        if (findByName(name).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 노선입니다.");
        }
    }

    private Optional<Line> findByName(String name) {
        String sql = "SELECT * FROM line WHERE name = :name";
        MapSqlParameterSource parameters = new MapSqlParameterSource("name", name);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, parameters, lineRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void deleteAll() {
        String sql = "truncate table line";
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource());
    }

    //
    // public static List<Line> findAll() {
    //     return lines;
    // }
    //
    // public static Line findById(final Long id) {
    //      return lines.stream()
    //             .filter(it -> it.getId() == id)
    //             .findFirst()
    //             .orElseThrow(()-> new IllegalArgumentException("해당 노선이 존재하지 않습니다."));
    // }
    //
    // public static void deleteById(Long id) {
    //     lines.remove(findById(id));
    // }
    //
    // public static void update(Long id, LineRequest lineRequest) {
    //     Line targetLine = LineDao.findById(id);
    //     targetLine.update(lineRequest);
    // }
}
