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
import wooteco.subway.exception.SectionServiceException;

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
        validateExistStations(request.getUpStationId(), request.getDownStationId());
        Section newSection = request.toSection(lineId);
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));

        Optional<Section> needToBeUpdatedSection = sections.add(newSection);
        needToBeUpdatedSection.ifPresent(sectionDao::update);
        sectionDao.insert(newSection);
    }

    private void validateExistLine(Long id) {
        if (!lineDao.existLineById(id)) {
            throw new AccessNoneDataException("존재하지 않는 노선입니다.");
        }
    }

    private void validateExistStations(Long upStationId, Long downStationId) {
        if (!stationDao.existStationById(upStationId) || !stationDao.existStationById(downStationId)) {
            throw new AccessNoneDataException("등록되지 않은 역으로는 구간을 만들 수 없습니다.");
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
        if (!sections.hasStation(stationId)) { // sections로 이동
            throw new AccessNoneDataException("현재 라인에 존재하지 않는 역입니다.");
        }
    }

    private void validateRemainOneSection(Sections sections) {
        if (sections.hasOneSection()) { // sections로 이동
            throw new SectionServiceException("구간이 하나인 노선에서는 구간 삭제가 불가합니다.");
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
