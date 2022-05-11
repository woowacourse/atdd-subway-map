package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.AddSectionException;
import wooteco.subway.exception.DeleteSectionException;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(final SectionDao sectionDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public int save(final Long lineId, final SectionRequest sectionRequest) {
        final Section section = createSection(lineId, sectionRequest);
        final Sections sections = new Sections(sectionDao.findByLineId(lineId));
        final Section newSection = sectionDao.save(section);
        addSection(sections, newSection);
        return sectionDao.update(sections.getSections());
    }

    private Section createSection(final Long lineId, final SectionRequest sectionRequest) {
        final Station upStation = stationDao.findById(sectionRequest.getUpStationId());
        final Station downStation = stationDao.findById(sectionRequest.getDownStationId());
        return new Section(lineId, upStation, downStation, sectionRequest.getDistance());
    }

    private void addSection(final Sections sections, final Section newSection) {
        try {
            sections.addSection(newSection);
        } catch (AddSectionException e) {
            throw new AddSectionException(e.getMessage());
        }
    }

    @Transactional
    public int delete(final Long lineId, final Long stationId) {
        final Sections sections = new Sections(sectionDao.findByLineId(lineId));
        final Station station = stationDao.findById(stationId);
        deleteSection(sections, station);
        return sectionDao.update(sections.getSections());
    }

    private void deleteSection(final Sections sections, final Station station) {
        try {
            final Section section = sections.deleteSection(station);
            sectionDao.delete(section);
        } catch (DeleteSectionException e) {
            throw new DeleteSectionException(e.getMessage());
        }
    }
}
