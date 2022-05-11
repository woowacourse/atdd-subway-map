package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionDeleteRequest;
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

    public void delete(SectionDeleteRequest request) {
        Sections sections = new Sections(sectionDao.findByLineId(request.getLineId()));
        List<Section> nearSections = sections.findNearByStationId(request.getStationId());
        for (Section section : nearSections) {
            sectionDao.deleteById(section.getId());
        }
        if (nearSections.size() == 2) {
            Section merged = nearSections.get(0).merge(nearSections.get(1));
            sectionDao.save(merged);
        }
    }
}
