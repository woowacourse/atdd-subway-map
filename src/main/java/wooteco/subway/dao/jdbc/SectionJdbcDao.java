package wooteco.subway.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Repository
public class SectionJdbcDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionJdbcDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Section save(final Long lineId, final Section section) {
        if (ObjectUtils.isEmpty(section)) {
            throw new IllegalArgumentException("passed section is null");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setLong(1, lineId);
            preparedStatement.setLong(2, section.getUpStation().getId());
            preparedStatement.setLong(3, section.getDownStation().getId());
            preparedStatement.setInt(4, section.getDistance());
            return preparedStatement;
        }, keyHolder);

        return new Section(keyHolder.getKey().longValue(), section.getUpStation(), section.getDownStation(),
                section.getDistance());
    }

    @Override
    public List<Section> findByLineId(final long lineId) {
        final String sql = "SELECT s.id, s.line_id, s.up_station_id, s.down_station_id, s.distance, "
                + "us.name as up_station_name, ds.name as down_station_name "
                + "FROM SECTION AS s "
                + "INNER JOIN STATION AS us ON us.id = s.up_station_id "
                + "INNER JOIN STATION AS ds ON ds.id = s.down_station_id "
                + "WHERE s.line_id = ?";

        return jdbcTemplate.query(sql, (resultSet, rowNumber) -> new Section(
                resultSet.getLong("id"),
                new Station(resultSet.getLong("up_station_id"), resultSet.getString("up_station_name")),
                new Station(resultSet.getLong("down_station_id"), resultSet.getString("down_station_name")),
                resultSet.getInt("distance")
        ), lineId);
    }

    @Override
    public void deleteById(final Long sectionId, final Long lineId) {
        final String sql = "DELETE FROM SECTION WHERE id = ? and line_id = ?";
        jdbcTemplate.update(sql, sectionId, lineId);
    }

    @Override
    public void deleteSectionsByLineId(final Long id) {
        final String sql = "DELETE FROM SECTION WHERE line_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void batchUpdate(final Long lineId, final List<Section> sections) {
        final String sql = "UPDATE SECTION SET up_station_id = ?, down_station_id = ?, distance = ? WHERE id = ?";
        jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                        ps.setLong(1, sections.get(i).getUpStation().getId());
                        ps.setLong(2, sections.get(i).getDownStation().getId());
                        ps.setInt(3, sections.get(i).getDistance());
                        ps.setLong(4, sections.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return sections.size();
                    }
                });
    }
}
