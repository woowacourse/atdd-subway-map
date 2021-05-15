package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.Line;
import wooteco.subway.section.Section;
import wooteco.subway.section.Sections;
import wooteco.subway.section.dao.SectionDao;

import java.util.List;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Sections findByLine(Line line) {
        List<Section> sections = sectionDao.findAllByLineId(line.getId());
        return new Sections(sections);
    }

    @Transactional
    public Section add(Section section) {
        return sectionDao.save(section);
    }

    public void addInLine(Line line, Section section) {
        Sections sections = line.getSections();
        sections.addSection(section);
        synchronizeDB(line);
    }

    public void synchronizeDB(Line line) {
        deleteByLineId(line.getId());
        addSections(line.getSections());
    }

    private void deleteByLineId(Long id) {
        sectionDao.deleteByLineId(id);
    }

    private void addSections(Sections sections) {
        sectionDao.batchInsert(sections.getSections());
    }
}

