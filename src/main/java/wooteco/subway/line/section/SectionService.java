package wooteco.subway.line.section;

import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.section.dto.SectionRequest;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void save(Long lineId, SectionRequest sectionRequest, boolean isFirstSave) {
        Section section = getSection(sectionRequest);
        if (!isFirstSave) {
            List<Section> lineSections = findByLineId(lineId);
            Sections sections = getSortedSections(lineSections);
            Section resultSection = sections.findJoinResultSection(section);
            sectionDao.update(lineId, resultSection);
        }
        sectionDao.save(lineId, section);
    }

    private Section getSection(SectionRequest sectionRequest) {
        return new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
            sectionRequest.getDistance());
    }

    public void deleteByLineId(Long lineId) {
        sectionDao.deleteByLineId(lineId);
    }

    public List<Section> findByLineId(Long lineId) {
        return sectionDao.findByLineId(lineId);
    }

    public void deleteByStationId(Long lineId, Long stationId) {
        List<Section> lineSections = findByLineId(lineId);

        Sections sections = getSortedSections(lineSections);

        Section deleteResultSection = sections.findDeleteResultSection(stationId);
        Section updateResultSection = sections
            .findDeleteUpdateResultSection(stationId, deleteResultSection);
        if (Objects.nonNull(updateResultSection)) {
            sectionDao.update(lineId, updateResultSection);
        }
        sectionDao.deleteById(lineId, deleteResultSection.getId());
    }

    public Sections getSortedSections(List<Section> lineSections) {
        Sections sections = new Sections(lineSections);
        sections.sort();
        return sections;
    }
}
