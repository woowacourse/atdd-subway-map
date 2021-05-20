package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.domain.station.Stations;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.SectionResponse;

@Service
@Transactional(readOnly = true)
public class SectionService {
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public SectionResponse createSection(Long lineId, SectionRequest sectionRequest) {
        Station upwardStation = stationDao.select(sectionRequest.getUpStationId());
        Station downwardStation = stationDao.select(sectionRequest.getDownStationId());
        int distance = sectionRequest.getDistance();

        Section section = new Section(upwardStation, downwardStation, distance);
        Long sectionId = sectionDao.insert(lineId, section);
        return new SectionResponse(sectionId, lineId, section);
    }

    @Transactional
    public SectionResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Station upwardStation = stationDao.select(sectionRequest.getUpStationId());
        Station downwardStation = stationDao.select(sectionRequest.getDownStationId());
        int distance = sectionRequest.getDistance();

        Section section = new Section(upwardStation, downwardStation, distance);

        Stations stations = new Stations(stationDao.selectAll());
        Sections sections = new Sections(sectionDao.selectAll(lineId, stations));
        sections.validateIfPossibleToInsert(section);

        updateExistingSections(lineId, section, sections);
        Long sectionId = sectionDao.insert(lineId, section);
        return new SectionResponse(sectionId, lineId, section);
    }

    private void updateExistingSections(Long lineId, Section section, Sections sections) {
        if (sections.isNewStationDownward(section)) {
            sectionDao.updateWhenNewStationDownward(lineId, section);
            return;
        }
        sectionDao.updateWhenNewStationUpward(lineId, section);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Stations stations = new Stations(stationDao.selectAll());
        Sections sections = new Sections(sectionDao.selectAll(lineId, stations));
        sections.validateIfPossibleToDelete();

        if (sections.hasStationAsDownward(stationId) && sections.hasStationAsUpward(stationId)) {
            sectionDao.delete(lineId, stationId);
            sectionDao.insert(lineId, sections.createMergedSectionAfterDeletion(stationId));
            return;
        }

        if (sections.hasStationAsDownward(stationId)) {
            sectionDao.deleteBottomSection(lineId, sections.getBottomSection());
        }

        if (sections.hasStationAsUpward(stationId)) {
            sectionDao.deleteTopSection(lineId, sections.getTopSection());
        }
    }

    public Sections loadSections(Long lineId) {
        Stations stations = new Stations(stationDao.selectAll());
        return new Sections(sectionDao.selectAll(lineId, stations));
    }
}
