package wooteco.subway.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.NotFoundStationException;
import wooteco.subway.repository.dao.SectionDao;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.entity.SectionEntity;
import wooteco.subway.repository.entity.StationEntity;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(final SectionDao sectionDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Section resister(final Long lineId, final Long upStationId, final Long downStationId, final Integer distance) {
        final StationEntity upStationEntity = stationDao.findById(upStationId)
                .orElseThrow(() -> new NotFoundStationException("[ERROR] 지하철 역을 찾을 수 없습니다."));
        final Station upStation = new Station(upStationEntity.getId(), upStationEntity.getName());
        final StationEntity downStationEntity = stationDao.findById(downStationId)
                .orElseThrow(() -> new NotFoundStationException("[ERROR] 지하철 역을 찾을 수 없습니다."));
        final Station downStation = new Station(downStationEntity.getId(), downStationEntity.getName());

        final Section section = Section.createWithoutId(upStation, downStation, distance);
        final SectionEntity sectionEntity = new SectionEntity(section, lineId);
        final SectionEntity savedSectionEntity = sectionDao.save(sectionEntity);

        return new Section(
                savedSectionEntity.getId(),
                section.getUpStation(),
                section.getDownStation(),
                section.getDistance()
        );
    }
}
