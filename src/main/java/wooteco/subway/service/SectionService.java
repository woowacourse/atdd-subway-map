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
import java.util.Optional;

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
    public void add2(Line line, SectionRequest sectionRequest) {
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

    public void add(Line line, SectionRequest sectionRequest) {
        Sections sections = new Sections(sectionDao.findByLineId(line.getId()));
        Section sectionToInsert = Section.of(line, sectionRequest);
        //validate 먼저 해줘야하나? -> 갈래길도, 종점도 아닌거에 대한 validate 먼저 해주고
        sections.validateInsertable(sectionToInsert);

        Optional<Section> deletableSection = sections.getSectionToDelete(sectionToInsert);

        // 변경한 섹션을 저장
        sectionDao.save(sectionToInsert);

        // 수정할 섹션을 수정
        deletableSection.ifPresent(sectionToDelete -> {
            Section sectionToUpdate = sections.getSectionToUpdate(sectionToDelete, sectionToInsert);
            sectionDao.update(sectionToUpdate);
        });
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
