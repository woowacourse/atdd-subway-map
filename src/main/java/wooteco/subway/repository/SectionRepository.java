package wooteco.subway.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.dao.JdbcStationDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionDto;
import wooteco.subway.dto.SectionRequest;

@Repository
public class SectionRepository {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionRepository(JdbcTemplate jdbcTemplate) {
        this.sectionDao = new JdbcSectionDao(jdbcTemplate);
        this.stationDao = new JdbcStationDao(jdbcTemplate);
    }

    public Section create(Long lineId, SectionRequest request) {
        Station up = stationDao.findById(request.getUpStationId());
        Station down = stationDao.findById(request.getDownStationId());
        Section section = new Section(up, down, request.getDistance());
        SectionDto saved = sectionDao.save(SectionDto.of(section, lineId));
        return new Section(saved.getId(), section.getUp(), section.getDown(), section.getDistance());
    }

    public Section findById(Long id) {
        SectionDto sectionDto = sectionDao.findById(id);
        Station up = stationDao.findById(sectionDto.getUpStationId());
        Station down = stationDao.findById(sectionDto.getDownStationId());
        return new Section(sectionDto.getId(), up, down, sectionDto.getDistance());
    }

    public void delete(Long id) {
        sectionDao.delete(id);
    }

    public void update(SectionDto sectionDto) {
        sectionDao.update(sectionDto);
    }
}
