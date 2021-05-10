package wooteco.subway.line.section;

import org.springframework.stereotype.Service;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionResponse createSection(final long lineId, final SectionRequest sectionRequest) {
        final Section section = sectionDao.save(sectionRequest.toEntity(lineId));
        return SectionResponse.from(section);
    }

    public Sections findSectionsByLineId(final Long lineId) {
        return new Sections(sectionDao.findByLineId(lineId));
    }
}

