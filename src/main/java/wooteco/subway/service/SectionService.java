package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;

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
        sections.addSection(newSection);
        return sectionDao.update(sections.getSections());
    }

    private Section createSection(final Long lineId, final SectionRequest sectionRequest) {
        final Station upStation = stationDao.findById(sectionRequest.getUpStationId());
        final Station downStation = stationDao.findById(sectionRequest.getDownStationId());
        return new Section(lineId, upStation, downStation, sectionRequest.getDistance());
    }
}
