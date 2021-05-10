package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dto.response.LineResponse;
import wooteco.subway.section.Section;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.dto.request.SectionCreateRequest;
import wooteco.subway.section.dto.response.SectionCreateResponse;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public SectionCreateResponse save(LineResponse line, SectionCreateRequest sectionRequest) {
        Section section =
                new Section(line.getId(), sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        Section newSection = sectionDao.save(section);
        return new SectionCreateResponse(newSection);
    }
}

