package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Section init(Section section) {
        return sectionDao.save(section);
    }

    public Set<Long> getStationIds(Long lineId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));

        return sections.getDistinctStationIds();
    }

    public void add(Line line, SectionRequest sectionRequest) {
        Sections sections = new Sections(sectionDao.findByLineId(line.getId()));
        Section sectionToInsert = new Section(sectionRequest.getDistance(), line.getId(), sectionRequest.getUpStationId(), sectionRequest.getDownStationId());
        sections.validateInsertable(sectionToInsert);
        Optional<Section> deletableSection = sections.getSectionToDelete(sectionToInsert);

        sectionDao.save(sectionToInsert);

        deletableSection.ifPresent(sectionToDelete -> {
            Section sectionToUpdate = sections.getSectionToUpdate(sectionToDelete, sectionToInsert);
            sectionDao.update(sectionToUpdate);
        });
    }

    public void delete(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        sections.validateDeletable();
        Sections sectionsToDelete = sections.getByStationId(stationId);

        for (Section section : sectionsToDelete.getSections()) {
            sectionDao.deleteById(section.getId());
        }

        if (sectionsToDelete.needMerge()) {
            Section section = sectionsToDelete.mergeSections();
            sectionDao.save(section);
        }
    }
}
