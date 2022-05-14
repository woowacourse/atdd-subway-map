package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Name;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@Repository
public class JdbcSectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Section> rowMapper = (resultSet, rowNumber) -> new Section(
            resultSet.getLong("id"),
            new Line(resultSet.getLong("line_id"), new Name(resultSet.getString("line_name")), resultSet.getString("line_color")),
            new Station(resultSet.getLong("up_station_id"), resultSet.getString("up_station_name")),
            new Station(resultSet.getLong("down_station_id"), resultSet.getString("down_station_name")),
            new Distance(resultSet.getInt("distance"))
    );

    public JdbcSectionDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long insert(final Section section) {
        try {
            final String sql = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});

                ps.setLong(1, section.getLineId());
                ps.setLong(2, section.getUpStationId());
                ps.setLong(3, section.getDownStationId());
                ps.setInt(4 , section.getDistance());

                return ps;
            }, keyHolder);
            return keyHolder.getKey().longValue();
        } catch (final DuplicateKeyException e) {
            return null;
        }
    }

    public boolean existStation(final long stationId) {
        final String sql = "SELECT EXISTS(SELECT * FROM section WHERE up_station_id = ? OR down_station_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, stationId, stationId));
    }

    public Sections findAllByLineId(final Long lineId) {
        final String sql =
                "SELECT s.id AS id, s.distance AS distance, "
                        + "l.id AS line_id, l.name AS line_name, l.color AS line_color, "
                        + "us.id AS up_station_id, us.name AS up_station_name, "
                        + "ds.id AS down_station_id, ds.name AS down_station_name "
                        + "FROM section AS s "
                        + "INNER JOIN line AS l ON s.line_id = l.id "
                        + "INNER JOIN station AS us ON s.up_station_id = us.id "
                        + "INNER JOIN station AS ds ON s.down_station_id = ds.id "
                        + "WHERE s.line_id = ? ";
        final List<Section> query = jdbcTemplate.query(sql, rowMapper, lineId);
        System.out.println("query = " + query);
        return new Sections(query);
    }

    public Optional<Section> findBy(final Long lineId, final Long upStationId, final Long downStationId) {
        try {
            final String sql =
                    "SELECT s.*, l.name AS line_name, l.color AS line_color, us.name AS up_station_name, ds.name AS down_station_name "
                            + "FROM section AS s "
                            + "INNER JOIN line AS l ON s.line_id = l.id "
                            + "INNER JOIN station AS us ON s.up_station_id = us.id "
                            + "INNER JOIN station AS ds ON s.down_station_id = ds.id "
                            + "WHERE s.line_id = ? "
                            + "AND (s.up_station_id = ? OR s.down_station_id = ?)";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, lineId, upStationId, downStationId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Section> findByLineIdAndUpStationId(final Long lineId, final Long upStationId) {
        try {
            final String sql =
                    "SELECT s.*, l.name AS line_name, l.color AS line_color, us.name AS up_station_name, ds.name AS down_station_name "
                            + "FROM section AS s "
                            + "INNER JOIN line AS l ON s.line_id = l.id "
                            + "INNER JOIN station AS us ON s.up_station_id = us.id "
                            + "INNER JOIN station AS ds ON s.down_station_id = ds.id "
                            + "WHERE s.line_id = ? "
                            + "AND  s.up_station_id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, lineId, upStationId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Section> findByLineIdAndDownStationId(final Long lineId, final Long downStationId) {
        try {
            final String sql =
                    "SELECT s.*, l.name AS line_name, l.color AS line_color, us.name AS up_station_name, ds.name AS down_station_name "
                            + "FROM section AS s "
                            + "INNER JOIN line AS l ON s.line_id = l.id "
                            + "INNER JOIN station AS us ON s.up_station_id = us.id "
                            + "INNER JOIN station AS ds ON s.down_station_id = ds.id "
                            + "WHERE s.line_id = ? "
                            + "AND  s.down_station_id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, lineId, downStationId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Integer deleteById(final Long id) {
        final String sql = "DELETE FROM section WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

}
