package wooteco.subway.domain.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.domain.station.Stations;

@Repository
public class SectionRepository {
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionRepository(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Section createSection(Long upStationId, Long downStationId, int distance) {
        Station upwardStation = stationDao.select(upStationId);
        Station downwardStation = stationDao.select(downStationId);
        return new Section(upwardStation, downwardStation, distance);
    }

    public Sections loadSections(Long lineId) {
        Stations stations = new Stations(stationDao.selectAll());
        return new Sections(sectionDao.selectAll(lineId, stations));
    }

    public Long insert(Long lineId, Section section) {
        return sectionDao.insert(lineId, section);
    }

    public void delete(Long lineId, Long stationId) {
        sectionDao.delete(lineId, stationId);
    }

    public void deleteBottomSection(Long lineId, Section bottomSection) {
        sectionDao.deleteBottomSection(lineId, bottomSection);
    }

    public void deleteTopSection(Long lineId, Section topSection) {
        sectionDao.deleteTopSection(lineId, topSection);
    }

    public void updateWhenNewStationDownward(Long lineId, Section section) {
        sectionDao.updateWhenNewStationDownward(lineId, section);
    }

    public void updateWhenNewStationUpward(Long lineId, Section section) {
        sectionDao.updateWhenNewStationUpward(lineId, section);
    }
}
