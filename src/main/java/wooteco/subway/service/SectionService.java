package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;

import java.util.List;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Section save(Section section) {
        //validateSection 등록할 수 있는 구간인지 검증
        //validateSection(section);
        return sectionDao.save(section);
    }
}
