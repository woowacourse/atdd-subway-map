package wooteco.subway.application;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }
}
