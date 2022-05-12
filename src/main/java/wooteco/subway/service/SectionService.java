package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

@Service
public class SectionService {
    private static final int MERGE_REQUIRED = 2;
    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void create(Section section) {
        Sections sections = sectionDao.findByLineId(section.getLineId());
        Section savedSection = sectionDao.save(new Section(section.getLineId(),
                section.getUpStationId(), section.getDownStationId(), section.getDistance()));

        Section updateSection = sections.add(savedSection);
        sectionDao.updateSection(updateSection);
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        Sections sections = sectionDao.findByLineId(lineId);
        List<Section> updateSections = sections.delete(stationId);
        sectionDao.deleteSections(updateSections);

        if (isDeletedInMiddle(updateSections)) {
            Section mergedSection = updateSections.get(0).merge(updateSections.get(1));
            sectionDao.save(mergedSection);
        }
    }

    private boolean isDeletedInMiddle(List<Section> updateSections) {
        return updateSections.size() == MERGE_REQUIRED;
    }
}
