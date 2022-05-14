package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.SectionsToBeCreatedAndUpdated;
import wooteco.subway.dto.SectionsToBeDeletedAndUpdated;
import wooteco.subway.exception.NotFoundException;

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

        SectionsToBeCreatedAndUpdated result = sections.add(newSection);
        sectionDao.insert(result.getSectionToBeCreated());
        if (result.getSectionToBeUpdated() != null) {
            sectionDao.update(result.getSectionToBeUpdated());
        }
    }

    private void validateExistLine(Long id) {
        if (!lineDao.existLineById(id)) {
            throw new NotFoundException("존재하지 않는 노선입니다.");
        }
    }

    private void validateExistStations(Long upStationId, Long downStationId) {
        if (!stationDao.existStationById(upStationId) || !stationDao.existStationById(downStationId)) {
            throw new NotFoundException("등록되지 않은 역으로는 구간을 만들 수 없습니다.");
        }
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        validateExistLine(lineId);
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        SectionsToBeDeletedAndUpdated result = sections.delete(stationId);

        sectionDao.deleteById(result.getSectionToBeRemoved().getId());
        if (result.getSectionToBeUpdated() != null) {
            sectionDao.update(result.getSectionToBeUpdated());
        }
    }
}
