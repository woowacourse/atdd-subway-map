package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.SectionResponse;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionResponse createLine(Long lineId, SectionRequest sectionRequest) {
        Section newSection = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), new Distance(sectionRequest.getDistance()));
        Section createdSection = sectionDao.save(newSection);
        return new SectionResponse(createdSection);
    }
}
