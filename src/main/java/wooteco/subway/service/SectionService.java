package wooteco.subway.service;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.service.dto.SectionRequest;

public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void save(final Long lineId, final SectionRequest sectionRequest) {
        Section section = convertSection(sectionRequest);
        sectionDao.save(lineId, section);
    }

    private Section convertSection(final SectionRequest sectionRequest) {
        return new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
    }
}
