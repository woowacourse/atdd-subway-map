package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionRequest;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void firstSave(Long lineId, SectionRequest sectionRequest) {
        sectionDao.save(
            createSection(lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance(),
                1L));
    }

    private Section createSection(Long lineId, long upStationId, long downStationId, int distance, Long lineOrder) {
        return new Section(null, lineId, upStationId, downStationId, distance, lineOrder);
    }
}
