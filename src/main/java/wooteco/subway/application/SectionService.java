package wooteco.subway.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.NoSuchStationException;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(final SectionDao sectionDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Section createSection(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        Station upStation = stationDao.findById(upStationId)
                .orElseThrow(() -> new NoSuchStationException(upStationId));
        Station downStation = stationDao.findById(downStationId)
                .orElseThrow(() -> new NoSuchStationException(downStationId));
        return sectionDao.save(lineId, new Section(upStation, downStation, distance));
    }

    @Transactional(readOnly = true)
    public List<Section> findSectionsByLineId(final Long lineId) {
        return sectionDao.findByLineId(lineId);
    }

    public void updateSections(Long lineId, List<Section> sections) {
        sectionDao.batchUpdate(lineId, sections);
    }

    public void deleteSection(Long lineId, Long sectionId, List<Section> sections) {
        sectionDao.deleteById(sectionId, lineId);
        sectionDao.batchUpdate(lineId, sections);
    }

    public void deleteAllSectionsRelevantToLine(Long lineId) {
        sectionDao.deleteSectionsByLineId(lineId);
    }
}
