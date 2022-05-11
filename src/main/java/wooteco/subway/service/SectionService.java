package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.DeletableSections;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionDeleteRequest;
import wooteco.subway.dto.SectionSaveRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
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
        sections.getDividedSectionsFrom(sectionForSave).ifPresent(sectionDao::update);
    }

    public void delete(SectionDeleteRequest request) {
        Sections sections = new Sections(sectionDao.findByLineId(request.getLineId()));
        DeletableSections deletableSections = new DeletableSections(
                sections.findDeletableByStationId(request.getStationId()));
        deleteNearSections(deletableSections);
        deletableSections.mergeSections()
                .ifPresent(sectionDao::update);
    }

    private void deleteNearSections(DeletableSections deletableSections) {
        for (Long id : deletableSections.getSectionIds()) {
            sectionDao.deleteById(id);
        }
    }
}
