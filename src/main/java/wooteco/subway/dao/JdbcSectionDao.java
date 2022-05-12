package wooteco.subway.dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import wooteco.subway.entity.SectionEntity;

@Repository
public class JdbcSectionDao implements SectionDao {
    /*
    id              BIGINT AUTO_INCREMENT NOT NULL,
    line_id         BIGINT                NOT NULL,
    up_station_id   BIGINT                NOT NULL,
    down_station_id BIGINT                NOT NULL,
    distance        INT                   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (line_id) REFERENCES line (id),
    FOREIGN KEY (up_station_id) REFERENCES station (id),
    FOREIGN KEY (down_station_id) REFERENCES station (id)
     */

    private final SimpleJdbcInsert jdbcInsert;
    private final JdbcTemplate jdbcTemplate;

    public JdbcSectionDao(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("section")
            .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SectionEntity save(SectionEntity section) {
        final long sectionId = jdbcInsert.executeAndReturnKey(Map.of(
            "line_id", section.getLineId(),
            "up_station_id", section.getUpStationId(),
            "down_station_id", section.getDownStationId(),
            "distance", section.getDistance()
        )).longValue();
        return new SectionEntity(
            sectionId,
            section.getLineId(),
            section.getUpStationId(),
            section.getDownStationId(),
            section.getDistance());
    }

    @Override
    public void deleteById(Long id) {
        final String sql = "DELETE FROM section WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<SectionEntity> readSectionsByLineId(Long lineId) {
        final String sql = "SELECT * FROM section WHERE line_id = ?";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> new SectionEntity(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getInt("distance")
        ), lineId);
    }

    @Override
    public void update(SectionEntity entity) {
        final String sql = "UPDATE section SET up_station_id = ?, down_station_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, entity.getUpStationId(), entity.getDownStationId(), entity.getId());
    }

}
