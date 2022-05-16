package wooteco.subway.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import wooteco.subway.entity.SectionEntity;

@Repository
public class JdbcSectionDao implements SectionDao {
    public static final RowMapper<SectionEntity> SECTION_ENTITY_ROW_MAPPER = (resultSet, rowNum) -> new SectionEntity(
        resultSet.getLong("id"),
        resultSet.getLong("line_id"),
        resultSet.getLong("up_station_id"),
        resultSet.getLong("down_station_id"),
        resultSet.getInt("distance")
    );
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
    public Long save(SectionEntity entity) {
        return jdbcInsert.executeAndReturnKey(Map.of(
            "line_id", entity.getLineId(),
            "up_station_id", entity.getUpStationId(),
            "down_station_id", entity.getDownStationId(),
            "distance", entity.getDistance()
        )).longValue();
    }

    @Override
    public Long delete(Long id) {
        final String sql = "DELETE FROM section WHERE id = ?";
        final int deletedCount = jdbcTemplate.update(sql, id);
        if (!isUpdated(deletedCount)) {
            return null;
        }
        return id;
    }

    public List<SectionEntity> findSectionsByLineId(Long lineId) {
        final String sql = "SELECT * FROM section WHERE line_id = ?";
        return jdbcTemplate.query(sql, SECTION_ENTITY_ROW_MAPPER, lineId);
    }

    public Optional<SectionEntity> findById(Long id) {
        final String sql = "SELECT * FROM section WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, SECTION_ENTITY_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Long update(SectionEntity entity) {
        final String sql = "UPDATE section SET up_station_id = ?, down_station_id = ? WHERE id = ?";
        final int updateCount = jdbcTemplate.update(
            sql,
            entity.getUpStationId(),
            entity.getDownStationId(),
            entity.getId()
        );
        if (!isUpdated(updateCount)) {
            return null;
        }
        return entity.getId();
    }

    private boolean isUpdated(int updatedCount) {
        return updatedCount != 0;
    }
}
