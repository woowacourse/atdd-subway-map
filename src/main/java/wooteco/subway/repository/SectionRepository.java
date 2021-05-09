package wooteco.subway.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.ExceptionStatus;
import wooteco.subway.exception.SubwayException;

import java.util.List;

@Repository
public class SectionRepository {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionRepository(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public long save(Section section) {
        return sectionDao.save(section);
    }

    public Sections findSectionsByLineId(long lineId) {
        List<Section> sections = sectionDao.findAllByLineId(lineId);
        sections.forEach(this::addStations);
        return new Sections(sections);
    }

    private void addStations(Section section) {
        long id = section.getId();
        List<Long> stationIds = sectionDao.findStationIdsById(id);
        long upStationId = stationIds.get(0);
        long downStationId = stationIds.get(1);
        Station upStation = findStationById(upStationId);
        Station downStation = findStationById(downStationId);
        section.setUpStation(upStation);
        section.setDownStation(downStation);
    }

    private Station findStationById(long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new SubwayException(ExceptionStatus.ID_NOT_FOUND));
    }

    public void update(Section section) {
        sectionDao.update(section);
    }

    public Section findById(long id) {
        Section section = sectionDao.findById(id)
                .orElseThrow(() -> new SubwayException(ExceptionStatus.ID_NOT_FOUND));
        addStations(section);
        return section;
    }

    public void delete(Section section) {
        sectionDao.delete(section);
    }

    public List<Section> findByStationId(long stationId) {
        List<Section> sections = sectionDao.findByStationId(stationId);
        sections.forEach(this::addStations);
        return sections;
    }
}
