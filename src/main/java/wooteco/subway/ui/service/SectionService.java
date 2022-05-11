package wooteco.subway.ui.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.SectionRequest;

@Service
public class SectionService {
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public void create(SectionRequest sectionRequest, Long lineId) {
        Long upStationId = sectionRequest.getUpStationId();
        Long downStationId = sectionRequest.getDownStationId();
        int distance = sectionRequest.getDistance();

        Station upStation = stationDao.findById(upStationId);
        Station downStation = stationDao.findById(downStationId);

        Section section = new Section(upStation, downStation, distance);
        sectionDao.save(section, lineId);
    }
}
