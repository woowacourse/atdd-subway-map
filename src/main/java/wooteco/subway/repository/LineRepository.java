package wooteco.subway.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

import java.util.ArrayList;
import java.util.List;

@Repository
public class LineRepository {
    private final JdbcTemplate jdbcTemplate;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineRepository(JdbcTemplate jdbcTemplate, LineDao lineDao, SectionDao sectionDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public Line saveLineWithSection(String name, String color, Long upStationId, Long downStationId, int distance) {
        Long createdLineId = lineDao.create(name, color);
        Long createdSectionId = sectionDao.create(createdLineId, upStationId, downStationId, distance);
        List<Section> sections = new ArrayList<>();
        Section section = new Section(createdSectionId, createdLineId, upStationId, downStationId, distance);
        sections.add(section);
        return new Line(createdLineId, name, color, sections);
    }

    public List<Line> findAllLine() {
        return lineDao.findAll();
    }

//    public Optional<Line> findById(Long lineId) {
//        String query = "SELECT * FROM LINE WHERE id = ?";
//        Line result = DataAccessUtils.singleResult(
//                jdbcTemplate.query(query, lineRowMapper, lineId)
//        );
//        return Optional.ofNullable(result);
//    }
//
//    public Optional<Line> findByName(String name) {
//        String query = "SELECT * FROM LINE WHERE name = ?";
//        Line result = DataAccessUtils.singleResult(
//                jdbcTemplate.query(query, lineRowMapper, name)
//        );
//        return Optional.ofNullable(result);
//    }
//
//    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) ->
//            new Line(
//                    resultSet.getLong("id"),
//                    resultSet.getString("name"),
//                    resultSet.getString("color"));
//
//    public Long edit(Long lineId, String color, String name) {
//        String query = "UPDATE LINE SET color = ?, name = ? WHERE id = ?";
//        return (long) jdbcTemplate.update(query, color, name, lineId);
//    }
//
    public Long deleteLineWithSectionByLineId(Long lineId) {
        String selectSectionQuery = "SELECT * FROM section WHERE line_id = ?";
        List<Long> sectionIds = jdbcTemplate.query(
                selectSectionQuery,
                (resultSet, rowNum) -> resultSet.getLong("id"),
                lineId
        );

        for (Long sectionId : sectionIds) {
            String deleteSectionQuery = "DELETE FROM section WHERE id = ?";
            jdbcTemplate.update(deleteSectionQuery, sectionId);
        }

        String deleteLineQuery = "DELETE FROM LINE WHERE id = ?";
        return (long) jdbcTemplate.update(deleteLineQuery, lineId);
    }
}
