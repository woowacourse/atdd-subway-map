package wooteco.subway.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.exception.SubwayException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class LineRepository {
    private static final int ONE_SECTION = 1;
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

    public Line findLineWithSectionsById(Long lineId) {
        List<Section> sections = sectionDao.findByLineId(lineId);
        Optional<Line> line = lineDao.findById(lineId);
        return new Line(line.get(), sections);
    }
    public Optional<Line> findLineByName(String name) {
        return lineDao.findByName(name);
    }


    public int edit(Long lineId, String name, String color) {
        return lineDao.edit(lineId, name, color);
    }

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

    public Long createSectionInLine(Long lineId, Long upStationId, Long downStationId, int distance) {
        return sectionDao.create(lineId, upStationId, downStationId, distance);
    }

    public void deleteSectionInLine(Long lineId, Long stationId) {
        List<Section> sections = sectionDao.findByLineId(lineId);
        if (sections.size() == ONE_SECTION) {
            throw new SubwayException("구간이 하나인 노선에서는 역을 삭제할 수 없습니다.");
        }

        Optional<Section> upSectionOptional = sectionDao.findByDownStationIdAndLineId(stationId, lineId);
        Optional<Section> downSectionOptional = sectionDao.findByUpStationIdAndLineId(stationId, lineId);

        // 가장 끝에 있는 섹션을 삭제할 떄
        if (isEndStation(lineId, stationId)) {
            sectionDao.deleteById(upSectionOptional.get().getId());
            return;
        }
        if (isStartStation(lineId, stationId)) {
            sectionDao.deleteById(downSectionOptional.get().getId());
            return;
        }

        // 중간에 있는 섹션을 삭제할 때
        Section upSection = upSectionOptional.get();
        Section downSection = downSectionOptional.get();
        sectionDao.deleteById(upSection.getId());
        sectionDao.deleteById(downSection.getId());
        sectionDao.create(
                lineId,
                upSection.getUpStationId(),
                downSection.getDownStationId(),
                upSection.getDistance() + downSection.getDistance()
        );
    }

    private boolean isEndStation(Long lineId, Long stationId) {
        Optional<Section> downSectionOptional = sectionDao.findByUpStationIdAndLineId(stationId, lineId);
        return !downSectionOptional.isPresent();
    }

    private boolean isStartStation(Long lineId, Long stationId) {
        Optional<Section> upSectionOptional = sectionDao.findByDownStationIdAndLineId(stationId, lineId);
        return !upSectionOptional.isPresent();
    }
}
