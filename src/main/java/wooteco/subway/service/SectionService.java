package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.DataNotFoundException;

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

    public void save(Long lineId, SectionRequest sectionRequest) {
        validateLineExistence(lineId);
        validateSectionRequest(sectionRequest);
        Section section = sectionRequest.toSection(lineId);

        if (notEndSection(lineId, section)) {
            updatePreviousSection(section);
        }
        sectionDao.save(section);
    }

    private void validateLineExistence(Long lineId) {
        if (!lineDao.existById(lineId)) {
            throw new DataNotFoundException("존재하지 않는 지하철입니다.");
        }
    }

    private void validateSectionRequest(SectionRequest sectionRequest) {
        validateStationExistence(sectionRequest.getUpStationId());
        validateStationExistence(sectionRequest.getDownStationId());
    }

    private void validateStationExistence(Long stationId) {
        if (!stationDao.existById(stationId)) {
            throw new DataNotFoundException("존재하지 않는 지하철입니다.");
        }
    }

    private boolean notEndSection(Long lineId, Section section) {
        boolean existUpStation = sectionDao.existByLineIdAndDownStationId(lineId, section.getUpStationId());
        boolean existDownStation = sectionDao.existByLineIdAndUpStationId(lineId, section.getDownStationId());
        if (existUpStation && existDownStation) {
            throw new IllegalArgumentException("기존에 등록되지 않은 구간만 추가해야합니다.");
        }
        return !existUpStation && !existDownStation;
    }

    private void updatePreviousSection(Section section) {
        Section prevSection = sectionDao.findByUpOrDownStationId(section.getLineId(), section.getUpStationId(),
                        section.getDownStationId())
                .orElseThrow(() -> new IllegalArgumentException("상행역과 하행역 중 하나는 노선에 존재해야합니다."));
        int newDistance = calculateDistanceDifference(section, prevSection);

        if (prevSection.equalsUpSection(section)) {
            sectionDao.updateUpStationId(prevSection.getId(), section.getUpStationId(), newDistance);
        }
        sectionDao.updateDownStationId(prevSection.getId(), section.getDownStationId(), newDistance);
    }

    private int calculateDistanceDifference(Section section, Section prevSection) {
        int distanceDifference = prevSection.getDistance() - section.getDistance();
        if (distanceDifference <= 0) {
            throw new IllegalArgumentException("새로운 구간의 길이는 기존 역 사이 길이보다 작아야합니다.");
        }
        return distanceDifference;
    }
}
