package wooteco.subway.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class DbSectionDao implements SectionDao {

    private static final RowMapper<Section> ROW_MAPPER = (rs, rn) -> {
        long id = rs.getLong("id");
        long upStationId = rs.getLong("up_station_id");
        long downStationId = rs.getLong("down_station_id");
        int distance = rs.getInt("distance");
        long lineId = rs.getLong("line_Id");

        return new Section(id, upStationId, downStationId, distance, lineId);
    };

    private final SimpleJdbcInsert jdbcInsert;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public DbSectionDao(DataSource dataSource) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public long save(Section section) {
        Map<String, Object> params = getSectionParams(section);

        long savedSectionId = jdbcInsert.executeAndReturnKey(params).longValue();
        return savedSectionId;
    }

    @Override
    public Optional<Section> findById(Long id) {
        String sql = "select * from section where id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        try {
            Section section = jdbcTemplate.queryForObject(sql, namedParameters, ROW_MAPPER);
            return Optional.ofNullable(section);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public List<Section> findByLineId(Long lineId) {
        String sql = "select * from section where line_id = :line_id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("line_id", lineId);
        return jdbcTemplate.query(sql, namedParameters , ROW_MAPPER);
    }

    @Override
    public long update(Section section) {
        String sql = "update section " +
                "set up_station_id = :up_station_id, " +
                "down_station_id = :down_station_id, " +
                "distance = :distance, " +
                "line_id = :line_id " +
                "where id = :id";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("up_station_id", section.getUpStationId())
                .addValue("down_station_id", section.getDownStationId())
                .addValue("distance", section.getDistance())
                .addValue("line_id", section.getLineId())
                .addValue("id", section.getId());

        jdbcTemplate.update(sql, namedParameters);
        return section.getId();
    }

    @Override
    public void deleteSection(Section section) {
        String sql = "delete from section where id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", section.getId());
        jdbcTemplate.update(sql, namedParameters);
    }

    private Map<String, Object> getSectionParams(Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("up_station_id", section.getUpStationId());
        params.put("down_station_id", section.getDownStationId());
        params.put("distance", section.getDistance());
        params.put("line_id", section.getLineId());
        return params;
    }
}
