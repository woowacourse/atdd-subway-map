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
        validateSection(section);
        return sectionDao.save(section);
    }


    private void validateSection(Section section) {
        // 정확히 일치하는 구간이 있으면 새로 등록할 수 없다

        // 정확히 일치하지 않더라도 구간이 연결되어 있으면 새로 등록할 수 없다
        List<Section> sections = sectionDao.findAll();
        if (section.isExistedIn(sections)) {
            throw new IllegalArgumentException("기존에 존재하는 노선은 등록할 수 없습니다.");
        };

    }
}
