package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@Transactional
@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void addSection(long id, SectionRequest sectionRequest) {
        List<Section> originSections = sectionDao.findByLineId(id).getSections();
        Sections updateSections = new Sections(originSections);
        Section addSection = new Section(id, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
            sectionRequest.getDistance());
        updateSections.add(addSection);
        modifySectionsByComparison(id, originSections, updateSections.getSections());
    }

    public void deleteSection(long id, Long stationId) {
        List<Section> originSections = sectionDao.findByLineId(id).getSections();
        Sections updateSections = new Sections(originSections);
        updateSections.remove(stationId);
        modifySectionsByComparison(id, originSections, updateSections.getSections());
    }

    private void modifySectionsByComparison(Long id, List<Section> originSections, List<Section> updateSections) {
        List<Section> addSections = generateNonMatchSections(updateSections, originSections);
        List<Section> deleteSections = generateNonMatchSections(originSections, updateSections);
        if (!deleteSections.isEmpty()) {
            deleteSections.forEach(section -> sectionDao.delete(section.getId()));
        }
        if (!addSections.isEmpty()) {
            addSections.forEach(section -> sectionDao.save(new Section(id, section.getUpStationId(),
                section.getDownStationId(), section.getDistance())));
        }
    }

    private List<Section> generateNonMatchSections(List<Section> baseSections, List<Section> findSections) {
        return baseSections.stream()
            .filter(section -> !findSections.contains(section))
            .collect(Collectors.toList());
    }
}
