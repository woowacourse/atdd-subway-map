package wooteco.subway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.line.LineDao;
import wooteco.subway.dao.section.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.line.DuplicatedLineInformationException;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.web.request.LineRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public Line create(LineRequest lineRequest, Station upStation, Station downStation) {
        return create(lineRequest.getName(), lineRequest.getColor(), upStation, downStation, lineRequest.getDistance());
    }

    public Line create(String name, String color, Station upStation, Station downStation, int distance) {
        if (lineDao.findByNameOrColor(name, color).isPresent()) {
            throw new DuplicatedLineInformationException();
        }
        Line line = lineDao.save(Line.of(name, color));
        Section section = Section.of(upStation, downStation, distance);
        line.addSection(sectionDao.save(section, line.getId()));
        return line;
    }

    public Line find(Long lineId) {
        Line line = lineDao.findById(lineId)
                .orElseThrow(LineNotFoundException::new);
        Sections sections = sectionDao.findById(lineId);
        line.insertSections(sections);
        return line;
    }

    public Optional<Line> findByNameOrColor(LineRequest lineRequest) {
        return lineDao.findByNameOrColor(lineRequest.getName(), lineRequest.getColor());
    }

    public List<Line> findAll() {
        List<Line> lines = lineDao.findAll();
        Map<Long, Sections> all = sectionDao.findAll();
        return insertSections(lines, all);
    }

    private List<Line> insertSections(List<Line> lines, Map<Long, Sections> sectionDictionary) {
        for (Line line : lines) {
            line.insertSections(sectionDictionary.get(line.getId()));
        }
        return lines;
    }

    public void delete(Long lineId) {
        if (lineDao.findById(lineId).isEmpty()) {
            throw new LineNotFoundException();
        }
        sectionDao.deleteByLineId(lineId);
        lineDao.delete(lineId);
    }

    public void update(Long id, LineRequest lineRequest) {
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }
}
