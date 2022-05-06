package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

import java.util.*;

@Service
public class SectionService {
    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void save(Section section) {
        checkAndFixOverLappingBy(section);
        sectionDao.save(section);
    }

    private void checkAndFixOverLappingBy(Section section) {
        Optional<Section> overLappedSection = sectionDao.getSectionsOverLappedBy(section);

        if (overLappedSection.isPresent()) {
            Section revisedSection = reviseSection(overLappedSection.get(), section);
            sectionDao.update(revisedSection);
        }
    }

    private Section reviseSection(Section existedSection, Section newSection) {
        Long id = existedSection.getId();
        Long lineId = existedSection.getLineId();
        int revisedDistance = existedSection.getDistance() - newSection.getDistance();

        if (Objects.equals(existedSection.getUpStationId(), newSection.getUpStationId())) {
            return new Section(id, lineId, newSection.getDownStationId(), existedSection.getDownStationId(), revisedDistance);
        }
        return new Section(id, lineId, existedSection.getUpStationId(), newSection.getUpStationId(), revisedDistance);
    }

    public void checkValidAndSave(Section section) {
        checkSavable(section);
        save(section);
    }

    private void checkSavable(Section section) {
        checkExistence(section);
        checkConnected(section);
        checkDistance(section);
    }

    private void checkExistence(Section section) {
        if (isLineAlreadyHasBothStationsOf(section)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 모두 존재합니다.");
        }
    }

    private boolean isLineAlreadyHasBothStationsOf(Section section) {
        Long lineId = section.getLineId();
        Long upStationId = section.getUpStationId();
        Long downStationId = section.getDownStationId();

        return sectionDao.getSectionsHaving(lineId, upStationId).isPresent()
                && sectionDao.getSectionsHaving(lineId, downStationId).isPresent();
    }

    private void checkConnected(Section section) {
        if (sectionDao.getSectionsConnectedTo(section).isEmpty()) {
            throw new IllegalArgumentException("기존 노선과 연결된 구간이 아닙니다.");
        }
    }

    private void checkDistance(Section section) {
        Optional<Section> overLappedSection = sectionDao.getSectionsOverLappedBy(section);

        if (overLappedSection.isPresent() && isInvalidDistance(section, overLappedSection.get())) {
            throw new IllegalArgumentException("적절한 거리가 아닙니다.");
        }
    }

    private boolean isInvalidDistance(Section newSection, Section oldSection) {
        return newSection.getDistance() >= oldSection.getDistance();
    }

    public void checkAndDelete(Long lineId, Long stationId) {
        checkDeletable(lineId);
        checkAndFixDisconnectionByDeleting(lineId, stationId);
        sectionDao.delete(lineId, stationId);
    }

    private void checkDeletable(Long lineId) {
        if (sectionDao.getSectionsIn(lineId).size() == 1) {
            throw new IllegalArgumentException("노선의 유일한 구간은 삭제할 수 없습니다.");
        }
    }

    private void checkAndFixDisconnectionByDeleting(Long lineId, Long stationId) {
        Optional<Section> upSection = sectionDao.findSectionHavingDownStationOf(lineId, stationId);
        Optional<Section> downSection = sectionDao.findSectionHavingUpStationOf(lineId, stationId);

        if (upSection.isPresent() && downSection.isPresent()) {
            createConnectedSection(lineId, upSection.get(), downSection.get());
        }
    }

    private void createConnectedSection(Long lineId, Section upSection, Section downSection) {
        sectionDao.save(new Section(lineId, upSection.getUpStationId(), downSection.getDownStationId(),
                upSection.getDistance() + downSection.getDistance()));
    }

    public Set<Long> findStationIdsIn(Line line) {
        List<Section> sections = sectionDao.findSectionsIn(line);

        Set<Long> stationIds = new HashSet<>();
        for (Section section : sections) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }

        return stationIds;
    }
}
