package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

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

    public void createSection(final Long lineId, final SectionRequest sectionRequest) {
        final Station upStation = stationDao.findById(sectionRequest.getUpStationId())
            .orElseThrow(NoSuchElementException::new);
        final Station downStation = stationDao.findById(sectionRequest.getDownStationId())
            .orElseThrow(NoSuchElementException::new);
        final Sections previousSections = new Sections(sectionDao.findByLineId(lineId));

        final Section newSection = new Section(lineId, upStation, downStation, sectionRequest.getDistance());
        final Sections updatedSections = new Sections(previousSections.addSection(newSection));

        updatedSections.findUpdateSections(previousSections)
            .forEach(sectionDao::save);
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        final Sections previousSections = new Sections(sectionDao.findByLineId(lineId));
        List<Section> updatedSections = previousSections.remove(lineId, stationId);
        updatedSections.forEach(section -> sectionDao.deleteById(section.getId()));

        sectionDao.save(previousSections.mergeSection(lineId, stationId));
    }
}
