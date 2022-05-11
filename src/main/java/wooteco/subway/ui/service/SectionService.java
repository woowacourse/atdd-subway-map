package wooteco.subway.ui.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;

@Service
public class SectionService {
    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void deleteByLine(Long id) {
        sectionDao.deleteByLine(id);
    }
}
