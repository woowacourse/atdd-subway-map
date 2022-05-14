package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Repository
public class JdbcLineDao implements LineDao {
    private final SimpleJdbcInsert jdbcInsert;
    private final JdbcTemplate jdbcTemplate;

    public JdbcLineDao(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Line save(Line line) {
        try {
            SqlParameterSource param = new BeanPropertySqlParameterSource(line);
            final Long id = jdbcInsert.executeAndReturnKey(param).longValue();
            return createNewObject(line, id);
        } catch (DuplicateKeyException ignored) {
            throw new IllegalStateException("이미 존재하는 노선 이름입니다.");
        }
    }

    private Line createNewObject(Line line, Long id) {
        final Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, id);
        return line;
    }

    @Override
    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, this::mapToLine);
    }

    @Override
    public Line findById(Long id) {
        final String sql = "SELECT * FROM line WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapToLine, id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalStateException("조회하고자 하는 노선이 존재하지 않습니다.");
        }
    }

    private Line mapToLine(ResultSet resultSet, int rowNum) throws SQLException {
        return new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color"),
                findSectionsById(resultSet.getLong("id"))
        );
    }

    private List<Section> findSectionsById(Long id) {
        String sql = "SELECT "
                + "sec.id, sec.distance, "
                + "sec.up_station_id, us.name up_station_name,"
                + "sec.down_station_id, ds.name down_station_name "
                + "FROM section AS sec "
                + "JOIN station AS us ON sec.up_station_id = us.id "
                + "JOIN station AS ds ON sec.down_station_id = ds.id "
                + "WHERE line_id = ? ORDER BY index_num";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> mapToSection(resultSet), id);
    }

    private Section mapToSection(ResultSet resultSet) throws SQLException {
        Long upStationId = resultSet.getLong("up_station_id");
        String upStationName = resultSet.getString("up_station_name");

        Long downStationId = resultSet.getLong("down_station_id");
        String downStationName = resultSet.getString("down_station_name");

        return new Section(
                resultSet.getLong("id"),
                new Station(upStationId, upStationName),
                new Station(downStationId, downStationName),
                resultSet.getInt("distance")
        );
    }

    @Override
    public int update(Line line) {
        final String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        final int updatedCount = jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
        validateUpdated(updatedCount);
        return updatedCount;
    }

    private void validateUpdated(int updatedCount) {
        if (updatedCount == 0) {
            throw new IllegalStateException("수정하고자 하는 노선이 존재하지 않습니다.");
        }
    }

    @Override
    public int delete(Long id) {
        final String sql = "DELETE FROM line WHERE id = ?";
        final int deletedCount = jdbcTemplate.update(sql, id);
        validateDeleted(deletedCount);
        return deletedCount;
    }

    private void validateDeleted(int deletedCount) {
        if (deletedCount == 0) {
            throw new IllegalStateException("삭제하고자 하는 노선이 존재하지 않습니다.");
        }
    }
}
