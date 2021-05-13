package wooteco.subway.line.section;

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
            Sections sections = findByLineId(lineId);
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

    public Sections findByLineId(Long lineId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        sections.sort();
        return sections;
    }

    public void deleteByStationId(Long lineId, Long stationId) {
        Sections sections = findByLineId(lineId);

        Section deleteResultSection = sections.findDeleteResultSection(stationId);
        Section updateResultSection = sections.findDeleteUpdateResultSection(stationId, deleteResultSection);
        if (Objects.nonNull(updateResultSection)) {
            sectionDao.update(lineId, updateResultSection);
        }
        sectionDao.deleteById(lineId, deleteResultSection.getId());
    }
}
