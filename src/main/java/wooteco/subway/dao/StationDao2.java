package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

@Repository
public class StationDao2 {

    private final JdbcTemplate jdbcTemplate;

    public StationDao2(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        final String sql = "INSERT INTO station SET name = ?";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement prepareStatement = con.prepareStatement(sql, new String[]{"id"});
            prepareStatement.setString(1, station.getName());
            return prepareStatement;
        }, keyHolder);
        final long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return setId(station, id);
    }

    private Station setId(Station station, long id) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        Objects.requireNonNull(field).setAccessible(true);
        ReflectionUtils.setField(field, station, id);
        return station;
    }

    public List<Station> findAll() {
        return null;
    }

    public Integer deleteById(Long id) {
        return null;
    }
}
