package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.exception.StationError;
import wooteco.subway.station.exception.StationException;

@Service
public class SectionService {
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public SectionService(StationDao stationDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public void initSection(Long lineId, SectionRequest sr) {
        stationById(sr.getUpStationId());
        stationById(sr.getDownStationId());

        sectionDao.save(lineId, sr);
    }

    public void addSection(Long lineId, SectionRequest sr) {
        Sections sections = sectionsByLineId(lineId);
        sections.add(sectionOf(sr));

        updateSections(lineId, sections);
    }

    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = sectionsByLineId(lineId);
        sections.delete(stationById(stationId));

        updateSections(lineId, sections);
    }

    private Section sectionOf(SectionRequest sectionRequest) {
        Station upStation = stationById(sectionRequest.getUpStationId());
        Station downStation = stationById(sectionRequest.getDownStationId());

        return new Section(upStation, downStation, sectionRequest.getDistance());
    }

    private Station stationById(Long id) {
        return stationDao.findById(id)
                .orElseThrow(() -> new StationException(StationError.NO_STATION_BY_ID));
    }
    private void updateSections(Long lineId, Sections sections) {
        sectionDao.deleteSectionsOf(lineId);
        sectionDao.saveSections(lineId, sections.getSections());
    }

    public Sections sectionsByLineId(Long id) {
        return new Sections(sectionDao.findSections(id));
    }
}
