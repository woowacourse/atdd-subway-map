package wooteco.subway.dao.jdbc;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.exception.NoSuchSectionException;

@Repository
public class SectionJdbcDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionJdbcDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long save(Section section) {
        final String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setLong(1, section.getLineId());
            preparedStatement.setLong(2, section.getUpStationId());
            preparedStatement.setLong(3, section.getDownStationId());
            preparedStatement.setInt(4, section.getDistance());
            return preparedStatement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public Long update(Long id, Section section) {
        final String sql = "UPDATE SECTION SET line_id = ?, up_station_id = ?, down_station_id = ?, distance = ? WHERE id = (?)";

        int affectedRow = jdbcTemplate.update(
                sql,
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance(),
                id);
        if (isNoUpdateOccurred(affectedRow)) {
            throw new NoSuchSectionException();
        }
        return id;
    }

    @Override
    public Section findById(Long id) {
        final String sql = "SELECT * FROM SECTION WHERE id = (?)";

        try {
            return jdbcTemplate.queryForObject(sql, (resultSet, rowNum) -> new Section(
                    resultSet.getLong("id"),
                    resultSet.getLong("line_id"),
                    resultSet.getLong("up_station_id"),
                    resultSet.getLong("down_station_id"),
                    resultSet.getInt("distance")
            ), id);
        } catch (EmptyResultDataAccessException exception) {
            throw new NoSuchSectionException();
        }
    }

    private boolean isNoUpdateOccurred(final int affectedRow) {
        return affectedRow == 0;
    }

    @Override
    public List<Section> findAllByLineId(Long lineId) {
        final String sql = "SELECT * FROM SECTION WHERE line_id = (?)";

        return jdbcTemplate.query(sql, (resultSet, rowNum) -> new Section(
                resultSet.getLong("id"),
                resultSet.getLong("line_id"),
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id"),
                resultSet.getInt("distance")
        ), lineId);
    }

    @Override
    public Optional<Section> findByLineIdAndUpStationId(Long lineId, Long upStationId) {
        final String sql = "SELECT * FROM SECTION WHERE line_id = (?) AND up_station_id = (?)";

        try {
            Section section = jdbcTemplate.queryForObject(sql, (resultSet, rowNum) -> new Section(
                    resultSet.getLong("id"),
                    resultSet.getLong("line_id"),
                    resultSet.getLong("up_station_id"),
                    resultSet.getLong("down_station_id"),
                    resultSet.getInt("distance")
            ), lineId, upStationId);
            return Optional.ofNullable(section);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Section> findByLineIdAndDownStationId(Long lineId, Long downStationId) {
        final String sql = "SELECT * FROM SECTION WHERE line_id = (?) AND down_station_id = (?)";

        try {
            Section section = jdbcTemplate.queryForObject(sql, (resultSet, rowNum) -> new Section(
                    resultSet.getLong("id"),
                    resultSet.getLong("line_id"),
                    resultSet.getLong("up_station_id"),
                    resultSet.getLong("down_station_id"),
                    resultSet.getInt("distance")
            ), lineId, downStationId);
            return Optional.ofNullable(section);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(Long id) {
        final String sql = "DELETE FROM SECTION WHERE id = (?)";

        jdbcTemplate.update(sql, id);
    }

    @Override
    public void deleteAllByLineId(Long lineId) {
        final String sql = "DELETE FROM SECTION WHERE line_id = (?)";

        jdbcTemplate.update(sql, lineId);
    }

    @Override
    public void deleteByLineIdAndStationId(Long lineId, Long stationId) {
        final String sql = "DELETE FROM SECTION WHERE line_id = (?) AND (up_station_id = (?) OR down_station_id = (?))";

        jdbcTemplate.update(sql, lineId, stationId, stationId);
    }
}
