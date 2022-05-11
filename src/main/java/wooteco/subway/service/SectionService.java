package wooteco.subway.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.WooTecoException;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.DeleteResult;
import wooteco.subway.domain.MetroManager;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.StationInfo;
import wooteco.subway.dto.SectionRequest;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Section save(SectionRequest sectionRequest, Long lineId) {
        Section section = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
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
            throw new WooTecoException("[ERROR] 이미 존재하는 구간입니다.");
        }
    }

    private void validateNonExistSection(Section section, MetroManager metroManager) {
        if ((!metroManager.isIn(section.getUpStationId())) && (!metroManager.isIn(section.getDownStationId()))) {
            throw new WooTecoException("[ERROR] 공유하는 역이 없습니다.");
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
            throw new WooTecoException("[ERROR] 포함되는 구간보다 더 큰 거리를 가질 수 없습니다.");
        }
    }

    public void delete(Long lineId, Long stationId) {
        MetroManager metroManager = new MetroManager(sectionDao.findAll(lineId));
        validateOneSectionExist(metroManager);
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

    private void validateOneSectionExist(MetroManager metroManager) {
        if (metroManager.isOneExist()) {
            throw new WooTecoException("[ERROR] 하나의 구간만 남았을 때는 삭제할 수 없습니다.");
        }
    }
}
