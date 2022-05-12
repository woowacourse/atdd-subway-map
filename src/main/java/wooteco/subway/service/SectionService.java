package wooteco.subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    @Autowired
    private SectionDao sectionDao;

    public void save(SectionRequest sectionRequest, Long lineId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        Section section = new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), lineId,
                sectionRequest.getDistance());
        sectionDao.deleteByLineId(lineId);
        sectionDao.saveAll(sections.update(section).value());
    }
}
