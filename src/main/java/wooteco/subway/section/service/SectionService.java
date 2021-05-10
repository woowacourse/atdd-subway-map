package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import wooteco.subway.section.Section;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.dto.SectionResponse;
import wooteco.subway.section.repository.JdbcSectionDao;

@Service
public class SectionService {
    private JdbcSectionDao jdbcSectionDao;

    public SectionService(JdbcSectionDao jdbcSectionDao) {
        this.jdbcSectionDao = jdbcSectionDao;
    }
    public SectionResponse save(Long id, SectionRequest sectionRequest) {
        Section savedSection = jdbcSectionDao.save(id, sectionRequest);
        return new SectionResponse(savedSection);
    }
}
