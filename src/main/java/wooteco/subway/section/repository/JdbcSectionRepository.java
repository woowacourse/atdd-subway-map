package wooteco.subway.section.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.section.domain.Distance;
import wooteco.subway.section.domain.Section;

import java.sql.PreparedStatement;
import java.util.Optional;

@Repository
public class JdbcSectionRepository implements SectionRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) ->
        new Section(
                resultSet.getLong("id"),
                resultSet.getLong("line_id"),
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id"),
                new Distance(resultSet.getInt("distance"))
        );

    public JdbcSectionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Section save(Section section) {
        try {
            String query = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES(?,?,?,?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            this.jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
                ps.setLong(1, section.getLineId());
                ps.setLong(2, section.getUpStationId());
                ps.setLong(3, section.getDownStationId());
                ps.setInt(4, section.getDistance().value());
                return ps;
            }, keyHolder);
            return findById(keyHolder.getKey().longValue()).get();
        } catch (DuplicateKeyException e) {
            throw new DuplicatedNameException("이미 존재하는 지하철 역 이름입니다.", e.getCause());
        }
    }

    @Override
    public Optional<Section> findById(Long id) {
        try {
            String query = "SELECT * FROM section WHERE id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, sectionRowMapper, id));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }
}
