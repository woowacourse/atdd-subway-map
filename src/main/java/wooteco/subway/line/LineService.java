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
import wooteco.subway.line.dao.JdbcLineDao;
import wooteco.subway.line.web.LineRequest;
import wooteco.subway.section.SectionService;
import wooteco.subway.section.dao.JdbcSectionDao;
import wooteco.subway.station.StationService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LineService {
    private final JdbcLineDao lineDao;
    private final JdbcSectionDao sectionDao;
    private final SectionService sectionService;
    private final StationService stationService;

    public Line find(Long lineId) {
        validateExistById(lineId);

        Line line = lineDao.findById(lineId);
        Sections sections = sectionService.findAllByLineId(lineId);
        line.setSections(sections);

        return line;
    }

    public List<Line> findAll() {
        List<Line> lines = lineDao.showAll();
        for (Line line : lines) {
            Sections sections = sectionService.findAllByLineId(line.getId());
            line.setSections(sections);
        }

        return lines;
    }

    @Transactional
    public Line create(LineRequest lineRequest) {
        String name = lineRequest.getName();
        String color = lineRequest.getColor();
        int distance = lineRequest.getDistance();

        validateExistInfo(name, color);

        Station upStation = stationService.find(lineRequest.getUpStationId());
        Station downStation = stationService.find(lineRequest.getDownStationId());
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
