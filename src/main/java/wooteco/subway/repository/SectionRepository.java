package wooteco.subway.repository;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.utils.exception.IdNotFoundException;
import wooteco.subway.utils.exception.SectionCreateException;

@Repository
public class SectionRepository {

    private static final int NO_ROW = 0;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public SectionRepository(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public Long save(final Section section) {
        SqlParameterSource parameters = new MapSqlParameterSource("line_id", section.getLineId())
                .addValue("up_station_id", section.getUpStation().getId())
                .addValue("down_station_id", section.getDownStation().getId())
                .addValue("distance", section.getDistance());
        try {
            return simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        } catch (DataAccessException e) {
            throw new SectionCreateException("Section 생성 불가");
        }
    }

    public Section findById(final Long id) {
        String sql = "SELECT s.id AS section_id, s.line_id, s.up_station_id, s.down_station_id, s.distance,"
                + " us.name AS up_station_name, ds.name AS down_station_name "
                + "FROM section AS s "
                + "LEFT JOIN station AS us ON us.id = s.up_station_id "
                + "LEFT JOIN station AS ds ON ds.id = s.down_station_id "
                + "WHERE s.id = :id";

        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, parameters, rowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new IdNotFoundException(IdNotFoundException.NO_ID_MESSAGE + id);
        }
    }

    public List<Section> findByLineId(final Long id) {
        String sql = "SELECT s.id AS section_id, s.line_id, s.up_station_id, s.down_station_id, s.distance,"
                + " us.name AS up_station_name, ds.name AS down_station_name "
                + "FROM section AS s "
                + "LEFT JOIN station AS us ON us.id = s.up_station_id "
                + "LEFT JOIN station AS ds ON ds.id = s.down_station_id "
                + "WHERE s.line_id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        try {
            return namedParameterJdbcTemplate.query(sql, parameters, rowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new IdNotFoundException(IdNotFoundException.NO_ID_MESSAGE + id);
        }
    }

    private RowMapper<Section> rowMapper() {
        return ((rs, rowNum) -> {
            long id = rs.getLong("section_id");
            long lineId = rs.getLong("line_id");
            long upStationId = rs.getLong("up_station_id");
            long downStationId = rs.getLong("down_station_id");
            int distance = rs.getInt("distance");
            return new Section(id,
                    lineId,
                    new Station(upStationId, rs.getString("up_station_name")),
                    new Station(downStationId, rs.getString("down_station_name")),
                    distance
            );
        });
    }

    public void update(final Section section) {
        String sql = "UPDATE section SET "
                + "up_station_id = :upStationId, down_station_id = :downStationId, distance = :distance "
                + "WHERE id = :id";

        SqlParameterSource parameters = new MapSqlParameterSource("upStationId", section.getUpStation().getId())
                .addValue("downStationId", section.getDownStation().getId())
                .addValue("distance", section.getDistance())
                .addValue("id", section.getId());
        int rowCounts = namedParameterJdbcTemplate.update(sql, parameters);
        if (rowCounts == NO_ROW) {
            throw new IdNotFoundException(IdNotFoundException.NO_ID_MESSAGE + section.getId());
        }
    }

    public void deleteSections(final List<Section> sections) {
        String sql = "DELETE FROM section WHERE id = :id";

        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(sections);
        namedParameterJdbcTemplate.batchUpdate(sql, batch);
    }

    public void deleteByLineId(final Long id) {
        String sql = "DELETE FROM section WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);

        namedParameterJdbcTemplate.update(sql, parameters);
    }

    public boolean isStationExist(final Long id) {
        String sql = "SELECT EXISTS(SELECT id FROM section WHERE up_station_id = :id OR down_station_id = :id)";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);

       return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(sql, parameters, Boolean.class));
    }
}
