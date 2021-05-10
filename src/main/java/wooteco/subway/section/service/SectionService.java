package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dto.response.LineCreateResponse;
import wooteco.subway.section.Section;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.dto.request.SectionCreateRequest;
import wooteco.subway.section.dto.response.SectionCreateResponse;
import wooteco.subway.section.dto.response.SectionResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public SectionCreateResponse save(LineCreateResponse line, SectionCreateRequest sectionRequest) {
        Section section =
                new Section(line.getId(), sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        Section newSection = sectionDao.save(section);
        return new SectionCreateResponse(newSection);
    }

    public List<SectionResponse> findAllByLineId(Long id) {
        List<Section> sections = sectionDao.findAllByLineId(id);
        return sections.stream().map(SectionResponse::new)
                .collect(Collectors.toList());
    }
}

