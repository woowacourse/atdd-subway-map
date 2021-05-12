package wooteco.subway.line;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.line.LineDuplicatedInformationException;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.section.dao.SectionDao;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public Line find(Long lineId) {
        validateExistById(lineId);

        Line line = lineDao.findById(lineId);
        Sections sections = sectionDao.findSectionsByLineId(lineId);
        line.setSections(sections);

        return line;
    }

    public List<Line> findAll() {
        List<Line> lines = lineDao.showAll();
        for (Line line : lines) {
            Sections sections = sectionDao.findSectionsByLineId(line.getId());
            line.setSections(sections);
        }

        return lines;
    }

    @Transactional
    public Line create(String name, String color, Station upStation, Station downStation, int distance) {
        validateExistInfo(name, color);

        Line line = lineDao.create(Line.create(name, color));
        Section section = Section.create(upStation, downStation, distance);
        sectionDao.create(section, line.getId());
        line.setSections(Sections.create(section));

        return line;
    }

    private void validateExistInfo(String name, String color) {
        if (lineDao.existByInfo(name, color)) {
            throw new LineDuplicatedInformationException();
        }
    }

    private void validateExistById(Long lineId) {
        if (!lineDao.existById(lineId)) {
            throw new LineNotFoundException();
        }
    }

}
