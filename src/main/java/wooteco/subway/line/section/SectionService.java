package wooteco.subway.line.section;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.section.dto.SectionRequest;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void save(Long lineId, SectionRequest sectionRequest, boolean isFirstSave) {
        Section section = new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        if (!isFirstSave) {
            Sections sections = findByLineId(lineId);
            Section resultSection = sections.findJoinResultSection(section);
            sectionDao.update(lineId, resultSection);
        }
        sectionDao.save(lineId, section);
    }

    public void deleteByLineId(Long lineId) {
        sectionDao.deleteByLineId(lineId);
    }

    public Sections findByLineId(Long lineId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        sections.sort();
        return sections;
    }
}
