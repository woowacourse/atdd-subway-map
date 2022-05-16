package wooteco.subway.service;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;

@Transactional
@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final LineDao lineDao;
    private final StationDao stationDao;

    public SectionService(final SectionDao sectionDao, final StationDao stationDao, final LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    public void createSection(final Long lineId, final SectionRequest sectionRequest) {
        validateExistLine(lineId);
        final Station upStation = stationDao.findById(sectionRequest.getUpStationId())
            .orElseThrow(NoSuchElementException::new);
        final Station downStation = stationDao.findById(sectionRequest.getDownStationId())
            .orElseThrow(NoSuchElementException::new);
        final Sections sections = new Sections(sectionDao.findByLineId(lineId));
        final Section newSection = new Section(lineId, upStation, downStation, sectionRequest.getDistance());

        sectionDao.deleteByLineId(lineId);
        sectionDao.saveAll(sections.addSection(newSection));
    }

    private void validateExistLine(final Long lineId) {
        if (!lineDao.existsById(lineId)) {
            throw new NoSuchElementException();
        }
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        final Sections sections = new Sections(sectionDao.findByLineId(lineId));
        sectionDao.deleteByLineId(lineId);
        sectionDao.saveAll(sections.removeSection(stationId));
    }
}
