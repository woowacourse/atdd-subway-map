package wooteco.subway.application;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;

public class SectionService {

    private SectionDao sectionDao;
    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Long createSection(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        return sectionDao.save(
                Section.builder()
                        .lineId(lineId)
                        .upStationId(upStationId)
                        .downStationId(downStationId)
                        .distance(distance)
                        .build()
        );
    }

}
