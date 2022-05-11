package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionRequest;

import java.util.*;

@Service
public class SectionService {
    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void save(Long lineId, SectionRequest sectionRequest) {
        Section section = sectionRequest.toSection(lineId);
        checkSavable(section);
        fixOverLappingBy(section);
        sectionDao.save(section);
    }

    private void fixOverLappingBy(Section section) {
        sectionDao.getSectionsOverLappedBy(section)
                .ifPresent(value -> sectionDao.update(value.revisedBy(section)));
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

        return sectionDao.existByLineAndStation(lineId, section.getUpStationId())
                && sectionDao.existByLineAndStation(lineId, section.getDownStationId());
    }

    private void checkConnected(Section section) {
        if (!sectionDao.existConnectedTo(section)) {
            throw new IllegalArgumentException("기존 노선과 연결된 구간이 아닙니다.");
        }
    }

    private void checkDistance(Section section) {
        Optional<Section> overLappedSection = sectionDao.getSectionsOverLappedBy(section);

        if (overLappedSection.isPresent() && section.isLongerThan(overLappedSection.get())) {
            throw new IllegalArgumentException("적절한 거리가 아닙니다.");
        }
    }

    public void delete(Long lineId, Long stationId) {
        checkDeletable(lineId);
        fixDisconnectionByDeleting(lineId, stationId);
        sectionDao.delete(lineId, stationId);
    }

    private void checkDeletable(Long lineId) {
        if (sectionDao.countSectionsIn(lineId) == 1) {
            throw new IllegalArgumentException("노선의 유일한 구간은 삭제할 수 없습니다.");
        }
    }

    private void fixDisconnectionByDeleting(Long lineId, Long stationId) {
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
}
