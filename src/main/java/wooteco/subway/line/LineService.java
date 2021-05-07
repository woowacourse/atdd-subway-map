package wooteco.subway.line;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicatedLineInformationException;
import wooteco.subway.section.SectionDao;

@RequiredArgsConstructor
@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public Line createLine(String name, String color, Station upStation, Station downStation, int distance) {
        if (lineDao.findLineByInfo(name, color).isPresent()) {
            throw new DuplicatedLineInformationException();
        }
        Line line = lineDao.save(Line.of(name, color));
        Section section = Section.of(upStation, downStation, distance);
        sectionDao.save(section, line.getId());
        line.addSection(section);
        return line;
    }
}
