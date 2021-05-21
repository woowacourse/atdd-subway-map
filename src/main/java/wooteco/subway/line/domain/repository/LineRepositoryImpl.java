package wooteco.subway.line.domain.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.exception.badrequest.LineNotFoundException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.section.Section;
import wooteco.subway.line.domain.section.Sections;
import wooteco.subway.line.infra.line.LineDao;
import wooteco.subway.line.infra.section.SectionDao;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class LineRepositoryImpl implements LineRepository {

    private LineDao lineDao;
    private SectionDao sectionDao;

    public LineRepositoryImpl(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @Override
    public Line saveLine(Line line) {
        return lineDao.save(line);
    }

    @Override
    public Line findById(Long id) {
        return lineDao.findById(id).orElseThrow(LineNotFoundException::new);
    }

    @Override
    public List<Line> findAll() {
        return lineDao.findAll();
    }

    @Override
    public Line findLineSectionById(Long id) {
        return lineDao.findLineSectionById(id).orElseThrow(LineNotFoundException::new);
    }

    @Override
    public void delete(Long id) {
        lineDao.delete(id);
    }

    @Override
    public void update(Line line) {
        lineDao.update(line);
    }

    @Override
    public Section saveSection(Section section) {
        return sectionDao.save(section);
    }

    @Override
    public void addSection(Section section) {
        Sections sections = new Sections(sectionDao.findByLineId(section.getLineId()));
        List<Section> tempSections = sections.toList();
        sections.add(section);
        List<Section> updatedSections = sections.toList();
        updateSectionByChanged(tempSections, updatedSections);
    }

    @Override
    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        List<Section> tempSections = sections.toList();
        sections.delete(stationId);
        List<Section> deletedSections = sections.toList();
        updateSectionByChanged(tempSections, deletedSections);
    }

    private void updateSectionByChanged(List<Section> sections, List<Section> updatedSections) {
        sections.stream()
                .filter(section -> !updatedSections.contains(section))
                .collect(Collectors.toList())
                .forEach(section -> sectionDao.delete(section.getId()));

        updatedSections.stream()
                .filter(section -> !sections.contains(section))
                .collect(Collectors.toList())
                .forEach(sectionDao::save);
    }
}
