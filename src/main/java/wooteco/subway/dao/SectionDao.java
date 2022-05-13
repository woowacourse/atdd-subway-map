package wooteco.subway.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public SectionDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("SECTION")
            .usingGeneratedKeyColumns("id");
    }

    public Section save(final Section section) {
        final SqlParameterSource parameterSource = new MapSqlParameterSource()
            .addValue("line_id", section.getLineId())
            .addValue("up_station_id", section.getUpStation().getId())
            .addValue("down_station_id", section.getDownStation().getId())
            .addValue("distance", section.getDistance());
        // final SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(section);
        final Long id = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return new Section(id, section.getLineId(), section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    public List<Section> findByLineId(final Long id) {
        final String sql = "select id, line_id, up_station_id, down_station_id, distance, "
            + "(select name from STATION where STATION.id = SECTION.up_station_id) as up_station_name, "
            + "(select name from STATION where STATION.id = SECTION.down_station_id) as down_station_name "
            + "from SECTION "
            + "where line_id = ?";
        return jdbcTemplate.query(sql, rowMapper(), id);
    }

    private RowMapper<Section> rowMapper() {
        return ((rs, rowNum) -> new Section(
            rs.getLong("id"),
            rs.getLong("line_id"),
            new Station(rs.getLong("up_station_id"), rs.getString("up_station_name")),
            new Station(rs.getLong("down_station_id"), rs.getString("down_station_name")),
            rs.getInt("distance")
        ));
    }

    public void deleteByLineId(final Long lineId) {
        final String sql = "delete from SECTION where line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }

    public void deleteById(final Long id) {
        final String sql = "delete from SECTION where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
