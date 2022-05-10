package wooteco.subway.repository.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.repository.entity.LineEntity;
import wooteco.subway.repository.entity.SectionEntity;
import wooteco.subway.repository.entity.StationEntity;

@Repository
public class JdbcSectionDao implements SectionDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<SectionEntity> rowMapper = (resultSet, rowNum) -> new SectionEntity(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getInt("distance")
    );

    public JdbcSectionDao(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SectionEntity save(final SectionEntity sectionEntity) {
        final String sql = "INSERT INTO SECTION(line_id, up_station_id, down_station_id, distance) "
                + "VALUES(:lineId, :upStationId, :downStationId, :distance)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final SqlParameterSource source = new BeanPropertySqlParameterSource(sectionEntity);
        jdbcTemplate.update(sql, source, keyHolder);
        if (keyHolder.getKey() == null) {
            throw new RuntimeException("[ERROR] SECTION 테이블에 레코드 추가 후에 key가 반환되지 않았습니다.");
        }
        return new SectionEntity(
                keyHolder.getKey().longValue(),
                sectionEntity.getLineId(),
                sectionEntity.getUpStationId(),
                sectionEntity.getDownStationId(),
                sectionEntity.getDistance()
        );
    }

    @Override
    public Optional<SectionEntity> findById(final Long id) {
        final String sql = "SELECT id, line_id, up_station_id, down_station_id, distance "
                + "FROM SECTION WHERE id = :id";
        final Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        final SqlParameterSource source = new MapSqlParameterSource(params);
        try {
            final SectionEntity sectionEntity = jdbcTemplate.queryForObject(sql, source, rowMapper);
            return Optional.of(sectionEntity);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }
}
