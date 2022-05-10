package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.line.LineDao;
import wooteco.subway.dao.section.SectionDao;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.section.SectionSaveRequest;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(final LineDao lineDao, final SectionDao sectionDao, final StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public int save(final long lineId, final SectionSaveRequest sectionSaveRequest) {
        Section addSection = getAdditionSection(lineId, sectionSaveRequest);
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        long savedSectionId = sectionDao.save(addSection);
        sections.addSection(new Section(savedSectionId, addSection));

        return sectionDao.updateSections(sections.getSections());
    }

    private Section getAdditionSection(final long lineId, final SectionSaveRequest sectionSaveRequest) {
        Line line = lineDao.findById(lineId);
        Station upStation = stationDao.findById(sectionSaveRequest.getUpStationId());
        Station downStation = stationDao.findById(sectionSaveRequest.getDownStationId());
        return new Section(line.getId(), upStation, downStation, sectionSaveRequest.getDistance());
    }

    @Transactional
    public void delete(final long lineId, final long stationId) {
        Station station = stationDao.findById(stationId);
        Line line = lineDao.findById(lineId);
        Sections sections = new Sections(sectionDao.findAllByLineId(line.getId()));

        Section section = sections.removeSection(station);
        sectionDao.delete(section.getId());
        sectionDao.updateSections(sections.getSections());
    }
}
