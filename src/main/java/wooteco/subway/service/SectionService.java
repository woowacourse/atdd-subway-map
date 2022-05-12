package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.SectionResult;

import java.util.List;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Section save(Section section) {
        validateSection(section);
        return sectionDao.save(section);
    }

    public List<Long> getStationIds(Long lineId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));

        return sections.getDistinctStationIds();
    }

    @Transactional
    public void add(Line line, SectionRequest sectionRequest) {
        Sections sections = new Sections(sectionDao.findByLineId(line.getId()));
        Section section = Section.of(line, sectionRequest);
        if (section.canAddAsLastStation(sections)) {
            save(section);
            return;
        }

        SectionResult result = section.canAddAsBetweenStation(sections);
        if (result.canAddAsBetweenStation()) {
            sectionDao.deleteById(result.getExistedSection().getId());
            save(result.getInsertedSection());
            save(result.getGeneratedSection());
            return;
        }
        throw new IllegalArgumentException("추가할 수 없는 노선입니다.");
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        sections.validateSize();
        Sections sectionsToDelete = sections.getByStationId(stationId);

        for (Section section : sectionsToDelete.getSections()) {
            sectionDao.deleteById(section.getId());
        }

        if (sectionsToDelete.isIntermediateStation()) {
            Section section = sectionsToDelete.mergeSections();
            sectionDao.save(section);
        }
    }

    private void validateSection(Section section) {
        List<Section> sections = sectionDao.findByLineId(section.getLineId());
        if (section.isExistedIn(sections)) {
            throw new IllegalArgumentException("기존에 존재하는 노선 구간은 등록할 수 없습니다.");
        }
    }
}
