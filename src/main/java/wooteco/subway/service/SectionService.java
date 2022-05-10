package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

import java.util.List;

@Service
public class SectionService {
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public SectionService(final StationDao stationDao, final SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public void create(final Long id, final Long upStationId, final Long downStationId, final int distance) {
        final Section section = new Section(upStationId, downStationId, distance);
        sectionDao.save(id, section);
    }

    public void deleteSectionByStationIdInLineId(final Long lineId, final Long stationId) {
        sectionDao.delete(lineId, stationId);
    }

    public List<Station> getStationsByLineId(Long id) {
        return List.of();
    }
}
