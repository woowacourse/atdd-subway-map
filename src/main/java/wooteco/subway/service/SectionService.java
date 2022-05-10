package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.entity.SectionEntity;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.utils.exceptions.StationNotFoundException;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    public Section createSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationDao.findById(sectionRequest.getUpStationId())
                .orElseThrow(() -> new StationNotFoundException(
                        sectionRequest.getUpStationId()));

        Station downStation = stationDao.findById(sectionRequest.getDownStationId())
                .orElseThrow(() -> new StationNotFoundException(
                        sectionRequest.getDownStationId()));

        Section section = new Section(lineId, upStation, downStation,
                sectionRequest.getDistance());
        SectionEntity saveEntity = sectionDao.save(section);
        return new Section(saveEntity.getLineId(), upStation, downStation, saveEntity.getDistance());
    }
}
