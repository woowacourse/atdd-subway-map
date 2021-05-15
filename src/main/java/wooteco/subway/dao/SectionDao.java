package wooteco.subway.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

import java.util.List;

@Repository
public class SectionDao implements SectionRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SectionDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Section> sectionRowMapper = (rs, rn) -> {
        long sectionId = rs.getLong("id");
        long lineId = rs.getLong("line_id");
        int distance = rs.getInt("distance");

        return new Section(sectionId, lineId, distance);
    };

    @Override
    public Section save(long lineId, Section section) {
        String query = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (:lineId, :upStationId, :downStationId, :distance)";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSourceBuilder()
                .setParam("lineId", lineId)
                .setParam("upStationId", section.getUpStation().getId())
                .setParam("downStationId", section.getDownStation().getId())
                .setParam("distance", section.getDistance())
                .build();

        KeyHolder keyHolder = new GeneratedKeyHolder();

        this.jdbcTemplate.update(query, sqlParameterSource, keyHolder);

        return this.findById(keyHolder.getKey().longValue());
    }

    @Override
    public void saveSections(long lineId, List<Section> sections) {
        String query = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (:lineId, :upStationId, :downStationId, :distance)";

        SqlParameterSource[] sqlParameterSources = sections.stream()
                .map(section -> new MapSqlParameterSourceBuilder()
                        .setParam("lineId", lineId)
                        .setParam("upStationId", section.getUpStation().getId())
                        .setParam("downStationId", section.getDownStation().getId())
                        .setParam("distance", section.getDistance())
                        .build())
                .toArray(MapSqlParameterSource[]::new);

        this.jdbcTemplate.batchUpdate(query, sqlParameterSources);
    }

    @Override
    public void deleteSectionsByLineId(long lineId) {
        String query = "DELETE FROM SECTION WHERE line_id = :lineId";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSourceBuilder()
                .setParam("lineId", lineId)
                .build();

        this.jdbcTemplate.update(query, sqlParameterSource);
    }

    @Override
    public Section findById(long sectionId) {
        String query = "SELECT * FROM SECTION WHERE id = :id";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSourceBuilder()
                .setParam("id", sectionId)
                .build();

        return jdbcTemplate.queryForObject(query, sqlParameterSource, sectionRowMapper);
    }

    @Override
    public List<Section> findAllByLineId(long lineId) {
        String query = "SELECT * FROM SECTION WHERE line_id = :lineId";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSourceBuilder()
                .setParam("lineId", lineId)
                .build();

        return jdbcTemplate.query(query, sqlParameterSource, sectionRowMapper);
    }

    @Override
    public Long getUpStationIdById(long id) {
        String query = "SELECT up_station_id FROM SECTION WHERE id = :id";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSourceBuilder()
                .setParam("id", id)
                .build();

        return jdbcTemplate.queryForObject(query, sqlParameterSource, Long.class);
    }

    @Override
    public Long getDownStationIdById(long id) {
        String query = "SELECT down_station_id FROM SECTION WHERE id = :id";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSourceBuilder()
                .setParam("id", id)
                .build();

        return jdbcTemplate.queryForObject(query, sqlParameterSource, Long.class);
    }
}
