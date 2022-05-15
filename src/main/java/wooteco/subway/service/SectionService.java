package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public void addSection(long id, SectionRequest sectionRequest) {
        List<Section> originSectionList = sectionDao.findByLineId(id).getSections();
        Sections updateSections = new Sections(originSectionList);
        Section addSection = new Section(id, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
            sectionRequest.getDistance());
        updateSections.add(addSection);
        compareWithDao(id, originSectionList, updateSections.getSections());
    }

    public void deleteSection(long id, Long stationId) {
        List<Section> originSectionList = sectionDao.findByLineId(id).getSections();
        Sections updateSections = new Sections(originSectionList);
        updateSections.remove(stationId);
        compareWithDao(id, originSectionList, updateSections.getSections());
    }

    private void compareWithDao(Long id, List<Section> originSectionList, List<Section> updateSections) {
        List<Section> addSectionList = generateNonMatchList(updateSections, originSectionList);
        List<Section> deleteSectionList = generateNonMatchList(originSectionList, updateSections);
        System.out.println("addSectionList size :  " + addSectionList.size());
        System.out.println("deleteSectionList size :  " + deleteSectionList.size());
        if (!deleteSectionList.isEmpty()) {
            deleteSectionList.forEach(section -> sectionDao.delete(section.getId()));
        }
        if (!addSectionList.isEmpty()) {
            addSectionList.forEach(section -> sectionDao.save(new Section(id, section.getUpStationId(),
                section.getDownStationId(), section.getDistance())));
        }
    }

    private List<Section> generateNonMatchList(List<Section> baseSectionList, List<Section> findSectionList) {
        return baseSectionList.stream()
            .filter(section -> !findSectionList.contains(section))
            .collect(Collectors.toList());
    }
}
