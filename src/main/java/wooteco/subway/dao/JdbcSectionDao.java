package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.entity.SectionEntity;

@Repository
public class JdbcSectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleInserter;

    public JdbcSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleInserter = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public SectionEntity save(SectionEntity section) {
        Map<String, ?> params = Map.of(
                "line_id", section.getLine_id(),
                "up_station_id", section.getUpStationId(),
                "down_station_id", section.getDownStationId(),
                "distance", section.getDistance());
        long savedId = simpleInserter.executeAndReturnKey(params).longValue();
        return new SectionEntity(savedId, section.getLine_id(), section.getUpStationId(), section.getDownStationId(),
                section.getDistance());
    }

    @Override
    public List<SectionEntity> findByLineId(Long lineId) {
        final String sql = "SELECT * FROM section WHERE line_id = ?";
        return jdbcTemplate.query(sql, getRowMapper(), lineId);
    }

    private RowMapper<SectionEntity> getRowMapper() {
        return (resultSet, rowNumber) -> {
            long id = resultSet.getLong("id");
            long lineId = resultSet.getLong("line_id");
            long upStationId = resultSet.getLong("up_station_id");
            long downStationId = resultSet.getLong("down_station_id");
            int distance = resultSet.getInt("distance");
            return new SectionEntity(id, lineId, upStationId, downStationId, distance);
        };
    }

    @Override
    public int deleteById(Long id) {
        final String sql = "DELETE FROM section WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public int deleteByLineId(Long lineId) {
        final String sql = "DELETE FROM section WHERE line_id = ?";
        return jdbcTemplate.update(sql, lineId);
    }

    @Override
    public int update(SectionEntity sections) {
        final String sql = "UPDATE section SET up_station_id = ?, down_station_id = ?, distance = ? WHERE id = ?";
        return jdbcTemplate.update(sql, sections.getUpStationId(), sections.getDownStationId(),
                sections.getDistance(), sections.getId());
    }

    @Override
    public int saveAll(List<SectionEntity> entities) {
        final String sql = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        int[] affectedRows = jdbcTemplate.batchUpdate(sql, getBatchStatementSetter(entities));
        return Arrays.stream(affectedRows).sum();
    }

    private BatchPreparedStatementSetter getBatchStatementSetter(List<SectionEntity> entities) {
        return new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                SectionEntity entity = entities.get(i);
                ps.setLong(1, entity.getLine_id());
                ps.setLong(2, entity.getUpStationId());
                ps.setLong(3, entity.getDownStationId());
                ps.setInt(4, entity.getDistance());
            }

            @Override
            public int getBatchSize() {
                return entities.size();
            }};
    }
}
