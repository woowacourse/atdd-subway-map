package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final LineDao lineDao;

    public SectionService(SectionDao sectionDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    public void addSection(Long lineId, SectionRequest request) {
        final Line line = lineDao.findById(lineId);
        final Section requestSection = new Section(
                lineId, request.getUpStationId(), request.getDownStationId(), request.getDistance());
        final Sections sections = new Sections(sectionDao.findAllByLineId(lineId));

        sections.add(requestSection);
        sectionDao.deleteAll(line.getId());
        sectionDao.saveAll(lineId, sections.getSections());
    }
}
