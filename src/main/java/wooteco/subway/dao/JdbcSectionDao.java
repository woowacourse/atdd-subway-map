package wooteco.subway.dao;

import java.sql.PreparedStatement;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.entity.SectionEntity;

@Repository
public class JdbcSectionDao implements SectionDao{

    private final JdbcTemplate jdbcTemplate;
    private final LineDao lineDao;
    private final StationDao stationDao;

    private RowMapper<SectionEntity> stationRowMapper = (resultSet, rowNum) -> new SectionEntity(
            resultSet.getLong("lineId"),
            resultSet.getLong("upStationId"),
            resultSet.getLong("downStationId"),
            resultSet.getInt("distance")
    );

    public JdbcSectionDao(JdbcTemplate jdbcTemplate, LineDao lineDao, StationDao stationDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    @Override
    public Section save(Section section) {
        final String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, Long.toString(section.getLine().getId()));
            ps.setString(2, Long.toString(section.getUpStation().getId()));
            ps.setString(3, Long.toString(section.getDownStation().getId()));
            ps.setString(4, Long.toString(section.getDistance()));
            return ps;
        }, keyHolder);

        final Long newSectionId = keyHolder.getKey().longValue();

        return new Section(newSectionId, section.getLine(), section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    @Override
    public void deleteById(Long id) {
        final String sql = "DELETE FROM SECTION WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Section findById(Long id) {
        try {
            final String sql = "SELECT * FROM SECTION WHERE id = ?";
            SectionEntity sectionEntity = jdbcTemplate.queryForObject(sql, stationRowMapper, id);
            return new Section(sectionEntity.getId(),
                    lineDao.findById(sectionEntity.getLineId()),
                    stationDao.findById(sectionEntity.getUpStationId()),
                    stationDao.findById(sectionEntity.getDownStationId()),
                    sectionEntity.getDistance());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
