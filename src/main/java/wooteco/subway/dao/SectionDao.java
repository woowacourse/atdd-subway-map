package wooteco.subway.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.LineRequest;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class SectionDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) ->
            new Section(
                    resultSet.getLong("id"),
                    resultSet.getLong("line_id"),
                    resultSet.getLong("up_station_id"),
                    resultSet.getLong("down_station_id"),
                    resultSet.getInt("distance"));

    public SectionDao(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public Section save(Section section) {
        Long lineId = section.getLineId();
        Long upStationId = section.getUpStationId();
        Long downStationId = section.getDownStationId();
        int distance = section.getDistance();

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("line_id", lineId)
                .addValue("up_station_id", upStationId)
                .addValue("down_station_id", downStationId)
                .addValue("distance", distance);

        Long sectionId = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Section(sectionId, lineId, upStationId, downStationId, distance);
    }

    public Section saveInitialSection(LineRequest lineRequest, Long id) {
        Long upStationId = lineRequest.getUpStationId();
        Long downStationId = lineRequest.getDownStationId();
        int distance = lineRequest.getDistance();

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("line_id", id)
                .addValue("up_station_id", upStationId)
                .addValue("down_station_id", downStationId)
                .addValue("distance", distance);

        long sectionId = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Section(sectionId, id, upStationId, downStationId, distance);
    }

    public Optional<Section> findBySameUpOrDownStation(Section section) {
        try {
            String sql = "SELECT * FROM section WHERE (line_id = :lineId AND down_station_id= :downStationId) OR (line_id = :lineId AND up_station_id = :upStationId) ";
            MapSqlParameterSource parameters = new MapSqlParameterSource("lineId", section.getLineId())
                    .addValue("upStationId", section.getUpStationId())
                    .addValue("downStationId", section.getDownStationId());
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, parameters, sectionRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<List<Section>> findByLine(long lineId) {
        try {
            String sql = "SELECT * FROM section WHERE line_id = :lineId";
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("lineId", lineId);
            return Optional.of(namedParameterJdbcTemplate.query(sql, parameters, sectionRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void deleteAll() {
        String sql = "TRUNCATE TABLE section";
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource());
        String resetIdSql = "ALTER TABLE section ALTER COLUMN id RESTART WITH 1";
        namedParameterJdbcTemplate.update(resetIdSql, new MapSqlParameterSource());
    }

    public Optional<Section> findByUpStationId(long upStationId, long lineId) {
        try {
            String sql = "SELECT * FROM section WHERE line_id = :lineId AND up_station_id = :id";
            MapSqlParameterSource parameters = new MapSqlParameterSource("id", upStationId)
                    .addValue("lineId", lineId);
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, parameters, sectionRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Section> findByDownStationId(long downStationId, long lineId) {
        try {
            String sql = "SELECT * FROM section WHERE line_id = :lineId AND down_station_Id = :id";
            MapSqlParameterSource parameters = new MapSqlParameterSource("id", downStationId)
                    .addValue("lineId", lineId);
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, parameters, sectionRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void updateDownStation(Section findSection, Section newSection) {
        String sql = "UPDATE section SET down_station_id = :downStationId WHERE id = :id";
        MapSqlParameterSource parameters = new MapSqlParameterSource("id", findSection.getId())
                .addValue("downStationId", newSection.getUpStationId());
        namedParameterJdbcTemplate.update(sql, parameters);
    }

    public void updateUpStation(Section findSection, Section newSection) {
        String sql = "UPDATE section SET up_station_id = :upStationId WHERE id = :id";
        MapSqlParameterSource parameters = new MapSqlParameterSource("id", findSection.getId())
                .addValue("upStationId", newSection.getDownStationId());
        namedParameterJdbcTemplate.update(sql, parameters);
    }

    public void updateDistance(Section findSection, Section newSection) {
        String sql = "UPDATE section SET distance = :distance WHERE id = :id";
        MapSqlParameterSource parameters = new MapSqlParameterSource("id", findSection.getId())
                .addValue("distance", findSection.getDistance() - newSection.getDistance());
        namedParameterJdbcTemplate.update(sql, parameters);
    }

    public Optional<Section> findById(Long id) {
        try {
            String sql = "SELECT * FROM section WHERE id = :id";
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("id", id);
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, parameters, sectionRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void deleteById(long id, long lineId) {
        String sql = "DELETE FROM section WHERE id = :id AND line_id = :lineId";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("lineId", lineId);
        namedParameterJdbcTemplate.update(sql, parameters);
    }
}
