package wooteco.subway.service;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.service.dto.SectionRequest;

public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(final SectionDao sectionDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public void save(final Long lineId, final SectionRequest sectionRequest) {
        validateStation(sectionRequest);

        Section section = convertSection(sectionRequest);
        if (!canSaving(lineId, section)) {
            throw new IllegalArgumentException("현재 위치에 구간을 저장할 수 없습니다.");
        }
        sectionDao.save(lineId, section);
    }

    private void validateStation(final SectionRequest sectionRequest) {
        if (!stationDao.existStationById(sectionRequest.getUpStationId())) {
            throw new IllegalArgumentException("상행역이 존재하지 않습니다.");
        }
        if (!stationDao.existStationById(sectionRequest.getDownStationId())) {
            throw new IllegalArgumentException("하행역이 존재하지 않습니다.");
        }
    }

    private Section convertSection(final SectionRequest sectionRequest) {
        return new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
    }

    private boolean canSaving(final Long lineId, final Section section) {
        if (canSavingLastUpStation(lineId, section.getDownStationId())) {
            return true;
        }
        return false;
    }

    private boolean canSavingLastUpStation(final Long lineId, final Long downStationId) {
        return sectionDao.existUpStation(lineId, downStationId);
    }
}
