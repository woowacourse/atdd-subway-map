package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public Section save(Section section) {
        Sections sections = new Sections(sectionDao.findByLineId(section.getLineId()), section.getLineId());
        sections.validateAddable(section);
        if (sections.needToChangeExistingSection(section)) {
            Section newSection = sections.findNeedUpdatingSection(section);
            sectionDao.update(newSection);
        }
        return sectionDao.save(section);
    }

    public List<Section> findByLineId(long lineId) {
        return sectionDao.findByLineId(lineId);
    }

    public List<Long> findArrangedStationIdsByLineId(Long id) {
        Sections sections = new Sections(sectionDao.findByLineId(id), id);
        List<Section> endSections = sections.findEndSections();
        Section endUpSection = endSections.get(0);

        return new ArrayList<>(sections.findArrangedStationIds(endUpSection));
    }

    @Transactional
    public void remove(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId), lineId);
        sections.validateRemovable(stationId);
        if (sections.isEndStation(stationId)) {
            Long sectionId = sections.findEndSectionIdToRemove(stationId);
            sectionDao.delete(sectionId);
            return;
        }
        List<Long> sectionIds = sections.findSectionIdsToRemove(stationId);
        Section newSection = sections.makeNewSection(stationId);

        sectionDao.deleteByIds(sectionIds);
        sectionDao.save(newSection);
    }
}
