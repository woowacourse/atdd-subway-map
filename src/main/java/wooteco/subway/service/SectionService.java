package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.AccessNoneDataException;

import java.util.Optional;

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
        validateExistLine(lineId);
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        validateExistStationInLine(sections, request.getUpStationId(), request.getDownStationId());

        if (sections.isLastStation(request.getUpStationId(), request.getDownStationId())) {
            sectionDao.insert(new Section(lineId, request.getUpStationId(), request.getDownStationId(), request.getDistance()));
            return;
        }
        createMiddleSection(lineId, request, sections);
    }

    private void createMiddleSection(Long lineId, SectionRequest request, Sections sections) {
        Section existNearSection = sections.findNearSection(request.getUpStationId(), request.getDownStationId());
        if (request.getDistance() >= existNearSection.getDistance()) {
            throw new IllegalArgumentException("새로운 구간의 길이는 기존 역 사이의 길이보다 작아야 합니다.");
        }
        sectionDao.insert(new Section(lineId, request.getUpStationId(), request.getDownStationId(), request.getDistance()));
        Section updateExistSection = new Section(existNearSection.getId(), existNearSection.getLineId(), request.getUpStationId(),
                existNearSection.getDownStationId(), existNearSection.getDistance() - request.getDistance());
        sectionDao.update(updateExistSection);
    }

    private void validateExistLine(Long id) {
        if (!lineDao.existLineById(id)) {
            throw new AccessNoneDataException("존재하지 않는 노선입니다.");
        }
    }

    private void validateExistStationInLine(Sections sections, Long upStationId, Long downStationId) {
        if (!stationDao.existStationById(upStationId) || !stationDao.existStationById(downStationId)) {
            throw new AccessNoneDataException("등록되지 않은 역으로는 구간을 만들 수 없습니다.");
        }
        boolean isExistUpStation = sections.hasStation(upStationId);
        boolean isExistDownStation = sections.hasStation(downStationId);
        if (!isExistUpStation && !isExistDownStation) {
            throw new IllegalArgumentException("구간을 추가하기 위해서는 노선에 들어있는 역이 필요합니다.");
        }
        if (isExistUpStation && isExistDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        validateExistLine(lineId);
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        validateExistStationInLine(sections, stationId);
        validateRemainOneSection(sections);

        Optional<Section> sectionWithLastStation = sections.checkAndExtractLastStation(stationId);
        if (sectionWithLastStation.isPresent()) {
            sectionDao.deleteById(sectionWithLastStation.get().getId());
            return;
        }
        deleteMiddleStation(sections, lineId, stationId);
    }

    private void validateExistStationInLine(Sections sections, Long stationId) {
        if (!sections.hasStation(stationId)) {
            throw new IllegalArgumentException("현재 라인에 존재하지 않는 역입니다.");
        }
    }

    private void validateRemainOneSection(Sections sections) {
        if (sections.hasOneSection()) {
            throw new IllegalArgumentException("구간이 하나인 노선에서는 구간 삭제가 불가합니다.");
        }
    }

    private void deleteMiddleStation(Sections sections, Long lineId, Long stationId) {
        Section upSideStation = sections.extractUpSideStation(stationId);
        Section downSideStation = sections.extractDownSideStation(stationId);
        Section newSection = new Section(lineId, upSideStation.getUpStationId(), downSideStation.getDownStationId(),
                upSideStation.getDistance() + downSideStation.getDistance());
        sectionDao.deleteById(upSideStation.getId());
        sectionDao.deleteById(downSideStation.getId());
        sectionDao.insert(newSection);
    }
}
