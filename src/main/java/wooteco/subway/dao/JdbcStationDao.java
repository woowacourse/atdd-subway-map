package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import wooteco.subway.domain.Station;

@Repository
public class JdbcStationDao implements StationDao {

    private final SimpleJdbcInsert jdbcInsert;
    private final JdbcTemplate jdbcTemplate;

    public JdbcStationDao(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Station save(Station station) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(station);
        try {
            final Long id = jdbcInsert.executeAndReturnKey(param).longValue();
            return createNewObject(station, id);
        } catch (DuplicateKeyException ignored) {
            throw new IllegalStateException("이미 존재하는 역 이름입니다.");
        }
    }

    private Station createNewObject(Station station, Long id) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, id);
        return station;
    }

    @Override
    public List<Station> findAll() {
        final String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
        ));
    }

    @Override
    public Station findById(Long id) {
        final String sql = "SELECT * FROM station WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (resultSet, rowNum) -> new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
        ), id);
    }

    @Override
    public int deleteById(Long id) {
        final String sql = "DELETE FROM station WHERE id = ?";
        final int deletedCount = jdbcTemplate.update(sql, id);
        validateRemoved(deletedCount);
        return deletedCount;
    }

    private void validateRemoved(int deletedCount) {
        if (deletedCount == 0) {
            throw new IllegalStateException("삭제하고자 하는 역이 존재하지 않습니다.");
        }
    }
}
