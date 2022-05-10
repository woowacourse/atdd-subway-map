package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionRequest;

import java.util.List;

@Service
public class SectionService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public SectionService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void create(Long lineId, SectionRequest request) {
        validateRequest(lineId, request);
        List<Section> sections = sectionDao.findAllByLineId(lineId);
        if (isLastUpStation(sections, request.getDownStationId()) || isLastDownStation(sections, request.getUpStationId())) {
            sectionDao.insert(new Section(lineId, request.getUpStationId(), request.getDownStationId(), request.getDistance()));
            return;
        }

        Section existSection = findExistSection(sections, request.getUpStationId(), request.getDownStationId());
        if (request.getDistance() >= existSection.getDistance()) {
            throw new IllegalArgumentException("새로운 구간의 길이는 기존 역 사이의 길이보다 작아야 합니다.");
        }
        sectionDao.insert(new Section(lineId, request.getUpStationId(), request.getDownStationId(), request.getDistance()));
        Section updateExistSection = new Section(existSection.getId(), existSection.getLineId(), request.getUpStationId(),
                existSection.getDownStationId(), existSection.getDistance() - request.getDistance());
        sectionDao.update(updateExistSection);
    }

    private void validateRequest(Long lineId, SectionRequest request) {
        validateExistLine(lineId);
        validateExistStationInLine(lineId, request.getUpStationId(), request.getDownStationId());
        validatePositiveDistance(request.getDistance());
    }

    private void validateExistLine(Long id) {
        if (!lineDao.existLineById(id)) {
            throw new IllegalArgumentException("존재하지 않는 노선입니다.");
        }
    }

    private void validateExistStationInLine(Long lineId, Long upStationId, Long downStationId) {
        if (!stationDao.existStationById(upStationId) || !stationDao.existStationById(downStationId)) {
            throw new IllegalArgumentException("등록되지 않은 역으로는 구간을 만들 수 없습니다.");
        }
        boolean isExistUpStation = stationDao.hasStationByStationAndLineId(lineId, upStationId);
        boolean isExistDownStation = stationDao.hasStationByStationAndLineId(lineId, downStationId);
        if (!isExistUpStation && !isExistDownStation) {
            throw new IllegalArgumentException("구간을 추가하기 위해서는 노선에 들어있는 역이 필요합니다.");
        }
        if (isExistUpStation && isExistDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }
    }

    private void validatePositiveDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간 사이의 거리는 0보다 커야합니다.");
        }
    }

    private boolean isLastUpStation(List<Section> sections, Long stationId) {
        boolean notExitInLine = sections.stream()
                .anyMatch(s -> s.getDownStationId().equals(stationId) || s.getUpStationId().equals(stationId));
        if (!notExitInLine) {
            return false;
        }
        return sections.stream()
                .noneMatch(s -> s.getDownStationId().equals(stationId));
    }

    private boolean isLastDownStation(List<Section> sections, Long stationId) {
        boolean notExitInLine = sections.stream()
                .anyMatch(s -> s.getDownStationId().equals(stationId) || s.getUpStationId().equals(stationId));
        if (!notExitInLine) {
            return false;
        }
        return sections.stream()
                .noneMatch(s -> s.getUpStationId().equals(stationId));
    }

    private Section findExistSection(List<Section> sections, Long upStationId, Long downStationId) {
        return sections.stream()
                .filter(s -> s.getUpStationId().equals(upStationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
