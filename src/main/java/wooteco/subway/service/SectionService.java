package wooteco.subway.service;

import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void insertSection(Long lineId, SectionRequest sectionRequest) {
        Section section = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        Sections newSections = sections.add(section);
        sectionDao.deleteByLineId(lineId);
        sectionDao.save(lineId, newSections.getValue());
    }

    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        Set<Long> stationIds = sections.getStationIds();
        validateExistStation(stationId, stationIds);
        Sections newSections = sections.delete(lineId, stationId);
        sectionDao.deleteByLineId(lineId);
        sectionDao.save(lineId, newSections.getValue());
    }

    private void validateExistStation(Long stationId, Set<Long> stationIds) {
        if (!stationIds.contains(stationId)) {
            throw new IllegalArgumentException("삭제할 구간이 존재하지 않습니다.");
        }
        if (stationIds.size() == 2) {
            throw new IllegalArgumentException("삭제할 수 없습니다.");
        }
    }
}
