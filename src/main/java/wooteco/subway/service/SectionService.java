package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

import java.util.List;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void save(final Long lineId, final SectionRequest sectionRequest) {
        Sections sections = getSections(lineId);

        sections.add(new Section(
                lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance())
        );

        updateSection(lineId, sections);
    }

    @Transactional
    public void deleteById(final Long lineId, final Long stationId) {
        Sections sections = new Sections(sectionDao.getSectionByLineId(lineId));
        sections.remove(stationId);
        updateSection(lineId, sections);
    }

    private void updateSection(Long lineId, Sections sections) {
        sectionDao.deleteByLineId(lineId);
        for (Section section : sections.getSections()) {
            sectionDao.save(lineId, section.getUpStationId(), section.getDownStationId(), section.getDistance());
        }
    }

    public Sections getSections(final Long lineId) {
        List<Section> sections = sectionDao.getSectionByLineId(lineId);
        return new Sections(sections);
    }
}
