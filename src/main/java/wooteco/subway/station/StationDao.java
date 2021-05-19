package wooteco.subway.station;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicatedFieldException;

@Repository
public class StationDao {

    private static final int SUCCESSFUL_AFFECTED_COUNT = 1;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> new Station(
        resultSet.getLong("id"),
        resultSet.getString("name")
    );

    public StationDao(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Station save(final Station station) {
        try {
            final String sql = "INSERT INTO station (name) VALUES (:name)";
            final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            final SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(station);
            namedParameterJdbcTemplate.update(sql, sqlParameterSource, keyHolder);
            final long id = keyHolder.getKey().longValue();
            return findById(id).orElseThrow(() -> new DataNotFoundException("해당 역을 찾을 수 없습니다."));
        } catch (DuplicateKeyException e) {
            throw new DuplicatedFieldException("중복된 이름의 지하철역입니다.");
        }
    }

    public void deleteById(final long id) {
        final String sql = "DELETE FROM station WHERE id = :id";
        int deletedCount = namedParameterJdbcTemplate.update(sql, Collections.singletonMap("id", id));

        if (deletedCount < SUCCESSFUL_AFFECTED_COUNT) {
            throw new DataNotFoundException("해당 Id의 지하철역이 없습니다.");
        }
    }

    public List<Station> findAll() {
        final String sql = "SELECT * FROM station";
        return namedParameterJdbcTemplate.query(sql, stationRowMapper);
    }

    public Optional<Station> findById(final Long id) {
        final String sql = "SELECT * FROM station WHERE id = :id";
        final List<Station> stations = namedParameterJdbcTemplate.query(
            sql, Collections.singletonMap("id", id), stationRowMapper
        );
        return Optional.ofNullable(DataAccessUtils.singleResult(stations));
    }

    public Optional<Station> findByName(final String name) {
        final String sql = "SELECT * FROM station WHERE name = :name";
        final List<Station> stations = namedParameterJdbcTemplate.query(
            sql, Collections.singletonMap("name", name), stationRowMapper
        );
        return Optional.ofNullable(DataAccessUtils.singleResult(stations));
    }

    public List<Station> findByIds(final List<Long> ids) {
        final List<Long> idsToDecode = IntStream.range(0, ids.size())
            .mapToObj(i -> Arrays.asList(ids.get(i), i))
            .flatMap(List::stream)
            .map(Number::longValue)
            .collect(Collectors.toList());

        final HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("ids", ids);
        paramMap.put("idsToDecode", idsToDecode);

        final String sql = "SELECT * FROM station WHERE id IN (:ids) ORDER BY DECODE(id, :idsToDecode)";
        return namedParameterJdbcTemplate.query(sql, paramMap, stationRowMapper);
    }
}
