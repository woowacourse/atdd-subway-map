package wooteco.subway.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.DeleteResult;
import wooteco.subway.domain.MetroManager;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.StationInfo;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Section enroll(Section section) {
        MetroManager metroManager = new MetroManager(sectionDao.findAll(section.getLineId()));
        validateNonExistSection(section, metroManager);
        if ((metroManager.findUpStationEnd() == section.getDownStationId().longValue()) && (metroManager.findDownStationEnd() == section.getUpStationId().longValue())) {
            metroManager.add_cycle(section);
            return sectionDao.save(section, section.getLineId());
        }
        validateExistSection(section, metroManager);
        return addSection(section, metroManager);
    }

    private void validateExistSection(Section section, MetroManager metroManager) {
        if ((metroManager.isIn(section.getUpStationId())) && (metroManager.isIn(section.getDownStationId()))) {
            throw new IllegalArgumentException();
        }
    }

    private void validateNonExistSection(Section section, MetroManager metroManager) {
        if ((!metroManager.isIn(section.getUpStationId())) && (!metroManager.isIn(section.getDownStationId()))) {
            throw new IllegalArgumentException();
        }
    }

    private Section addSection(Section section, MetroManager metroManager) {
        if (metroManager.isIn(section.getUpStationId())) {
            StationInfo rightInfo = metroManager.getAdjacency(section.getUpStationId()).getRight();
            validateDistance(section, rightInfo);
            metroManager.add_right(section);
            return sectionDao.save(section, section.getLineId());
        }
        StationInfo leftInfo = metroManager.getAdjacency(section.getDownStationId()).getLeft();
        validateDistance(section, leftInfo);
        metroManager.add_left(section);
        return sectionDao.save(section, section.getLineId());
    }

    private void validateDistance(Section section, StationInfo stationInfo) {
        if (!stationInfo.canDivisible(section.getDistance())) {
            throw new IllegalArgumentException();
        }
    }

    public void delete(Long lineId, Long stationId) {
        MetroManager metroManager = new MetroManager(sectionDao.findAll(lineId));
        if (metroManager.isCycle()) {
            List<Section> deletedSections = metroManager.delete_cycle(lineId, stationId);
            for (Section section : deletedSections) {
                sectionDao.deleteByLineIdAndStationIds(lineId, section.getUpStationId(), section.getDownStationId());
            }
            return;
        }
        Map<DeleteResult, List<Section>> deleteResult = metroManager.delete(lineId, stationId);
        for (Section section : deleteResult.get(DeleteResult.NEW_DELETE)) {
            sectionDao.deleteByLineIdAndStationIds(lineId, section.getUpStationId(), section.getDownStationId());
        }
        for (Section section : deleteResult.get(DeleteResult.NEW_SAVE)) {
            sectionDao.save(section, lineId);
        }
    }
}
