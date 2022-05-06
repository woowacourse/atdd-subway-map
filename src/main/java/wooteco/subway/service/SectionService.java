package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Section save(Section section) {
        if (sectionDao.existByUpStationIdAndDownStationId(section.getUpStationId(), section.getDownStationId())) {
            throw new IllegalStateException("이미 존재하는 Section입니다.");
        }
        return sectionDao.save(section);
    }
}
