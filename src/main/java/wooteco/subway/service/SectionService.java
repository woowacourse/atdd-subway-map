package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.DeleteAndUpdateSectionsInfo;
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
        DeleteAndUpdateSectionsInfo deleteAndUpdateSectionsInfo = sections.delete(stationId);

        sectionDao.deleteById(deleteAndUpdateSectionsInfo.getSectionToBeRemoved().getId());
        if (deleteAndUpdateSectionsInfo.getSectionToBeUpdated() != null) {
            sectionDao.update(deleteAndUpdateSectionsInfo.getSectionToBeUpdated());
        }
    }
}
