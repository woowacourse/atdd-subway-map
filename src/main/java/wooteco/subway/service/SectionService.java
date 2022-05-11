package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void add(long lineId, SectionRequest sectionRequest) {
        Section newSection = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
        Sections sections = new Sections(sectionDao.findByLineId(lineId));

        Section section = sectionDao.save(newSection);
        sections.add(section);
        sectionDao.update(sections.getValue());
    }
}
