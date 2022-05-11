package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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

    public Section save(Section section) {
        Sections sections = new Sections(sectionDao.findByLineId(section.getLineId()), section.getLineId());
        sections.validateAddable(section);
        if (sections.needToChange(section)) {
            Section newSection = sections.findUpdatingSection(section);
            sectionDao.update(newSection);
        }
        return sectionDao.save(section);
    }

    public List<Section> findByLineId(long lineId) {
        return sectionDao.findByLineId(lineId);
    }

    public List<Long> findStationIdsByLineId(Long id) {
        List<Section> sections = sectionDao.findByLineId(id);

        Set<Long> collect = sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toSet());

        Set<Long> collect2 = sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toSet());
        collect.addAll(collect2);

        return new ArrayList<>(collect);
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
