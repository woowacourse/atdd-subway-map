package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;

@Service
@Transactional(readOnly = true)
public class SectionService {
    
    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public void add(Long lineId, SectionRequest sectionRequest) {
        Section newSection = createNewSection(lineId, sectionRequest);
        Sections sections = new Sections(sectionDao.findByLineId(lineId));

        Section section = sectionDao.save(newSection);
        sections.add(section);
        sectionDao.update(sections.getValue());
    }

    private Section createNewSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationDao.findById(sectionRequest.getUpStationId());
        Station downStation = stationDao.findById(sectionRequest.getDownStationId());
        Line line = lineDao.findById(lineId);
        return new Section(line.getId(), upStation.getId(), downStation.getId(), sectionRequest.getDistance());
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        Station station = stationDao.findById(stationId);
        Line line = lineDao.findById(lineId);
        Sections sections = new Sections(sectionDao.findByLineId(line.getId()));

        Section deleteSection = sections.delete(station.getId());
        sectionDao.deleteById(deleteSection.getId());
        sectionDao.update(sections.getValue());
    }
}
