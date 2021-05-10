package wooteco.subway.line;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.line.DuplicatedLineInformationException;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.section.dao.SectionDao;

@RequiredArgsConstructor
@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public Line createLine(String name, String color, Station upStation, Station downStation, int distance) {
        if (lineDao.findByNameAndColor(name, color).isPresent()) {
            throw new DuplicatedLineInformationException();
        }
        Line line = lineDao.save(Line.of(name, color));
        Section section = Section.of(upStation, downStation, distance);
        sectionDao.save(section, line.getId());
        line.addSection(section);
        return line;
    }

    public Line findLine(Long lineId) {
        Line line = lineDao.findById(lineId).orElseThrow(LineNotFoundException::new);
        Sections sections = sectionDao.findSectionsByLineId(lineId);
        line.insertSections(sections);
        return line;
    }

}
