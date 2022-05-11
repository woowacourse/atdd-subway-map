package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionSaveRequest;

@Service
public class SectionService {

    private final JdbcSectionDao sectionDao;

    public SectionService(JdbcSectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Section save(SectionSaveRequest request) {
        Section sectionForSave = new Section(request.getLineId(), request.getUpStationId(),
                request.getDownStationId(), request.getDistance());
        updateDividedSection(sectionForSave);
        return sectionDao.save(sectionForSave);
    }

    private void updateDividedSection(Section sectionForSave) {
        Sections sections = new Sections(sectionDao.findByLineId(sectionForSave.getLine_id()));
        sections.getDividedSectionsFrom(sectionForSave)
                .ifPresent(sectionDao::update);
    }
}
