package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }


    public void save(Long lineId, SectionRequest sectionReq) {
        if (sectionDao.existByLineIdAndStationId(lineId, sectionReq.getUpStationId())
                && sectionDao.existByLineIdAndStationId(lineId, sectionReq.getDownStationId())) {
            throw new IllegalArgumentException("상행, 하행이 대상 노선에 둘 다 존재합니다.");
        }

        Section section = createSection(lineId, sectionReq);
        section.setLineOrder(1L);
        sectionDao.save(section);
    }

    private Section createSection(Long lineId, SectionRequest sectionReq) {
        return new Section(lineId,
                sectionReq.getUpStationId(),
                sectionReq.getDownStationId(),
                sectionReq.getDistance(),
                null);
    }
}
