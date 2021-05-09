package wooteco.subway.section;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SectionService {
    private SectionDao sectionDao;

    @Autowired
    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Section createSection(long lineId, Section section) {
        Section section1 = new Section(lineId, section.getUpStationId(), section.getDownStationId());
        sectionDao.save(section1);
        return section1;
    }
}
