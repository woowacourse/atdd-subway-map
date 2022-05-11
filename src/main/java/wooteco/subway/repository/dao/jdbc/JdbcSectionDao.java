package wooteco.subway.repository.dao.jdbc;

import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import wooteco.subway.repository.dao.SectionDao;
import wooteco.subway.repository.dao.entity.SectionEntity;

@Component
public class JdbcSectionDao implements SectionDao {

    private static final RowMapper<SectionEntity> ROW_MAPPER =
            (resultSet, rowNum) -> new SectionEntity(
                    resultSet.getLong("id"),
                    resultSet.getLong("line_id"),
                    resultSet.getLong("up_station_id"),
                    resultSet.getLong("down_station_id"),
                    resultSet.getInt("distance")
            );

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public JdbcSectionDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("Section")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Long save(SectionEntity sectionEntity) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(sectionEntity);
        return jdbcInsert.executeAndReturnKey(parameters)
                .longValue();
    }

    @Override
    public List<SectionEntity> findAllByLineId(Long lineId) {
        String query = "SELECT id, line_id, up_station_id, down_station_id, distance from Section"
                + " where line_id=(:lineId)";
        SqlParameterSource parameters = new MapSqlParameterSource("lineId", lineId);
        return jdbcTemplate.query(query, parameters, ROW_MAPPER);
    }

    @Override
    public void remove(Long id) {
        String query = "DELETE FROM Section WHERE id=(:id)";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(query, parameters);
    }
}
