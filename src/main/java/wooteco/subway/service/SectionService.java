package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionSaveRequest;

@Service
public class SectionService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(final LineDao lineDao, final SectionDao sectionDao, final StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public int save(final long lineId, final SectionSaveRequest sectionSaveRequest) {
        Section addSection = getAdditionSection(lineId, sectionSaveRequest);
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        sections.addSection(addSection);

        sectionDao.save(addSection);
        return sectionDao.updateSections(sections.getSections());
    }

    private Section getAdditionSection(final long lineId, final SectionSaveRequest sectionSaveRequest) {
        Line line = lineDao.findById(lineId);
        Station upStation = stationDao.findById(sectionSaveRequest.getUpStationId());
        Station downStation = stationDao.findById(sectionSaveRequest.getDownStationId());
        return new Section(line.getId(), upStation, downStation, sectionSaveRequest.getDistance());
    }
}
