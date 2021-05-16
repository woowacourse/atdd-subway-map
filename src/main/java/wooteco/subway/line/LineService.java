package wooteco.subway.line;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.*;
import wooteco.subway.exception.line.LineDuplicatedInformationException;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.line.dao.JdbcLineDao;
import wooteco.subway.line.web.LineRequest;
import wooteco.subway.line.web.LineResponse;
import wooteco.subway.section.SectionService;
import wooteco.subway.station.StationService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LineService {
    private final JdbcLineDao lineDao;
    private final SectionService sectionService;
    private final StationService stationService;

    public LineResponse findById(Long lineId) {
        validateExistById(lineId);

        Line line = lineDao.findById(lineId);
        Sections sections = sectionService.findAllByLineId(lineId);
        line.setStationsBySections(sections);

        return LineResponse.create(line);
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.showAll();
        for (Line line : lines) {
            Sections sections = sectionService.findAllByLineId(line.getId());
            line.setStationsBySections(sections);
        }
        List<LineResponse> lineResponses = new ArrayList<>();
        for (Line line : lines) {
            lineResponses.add(LineResponse.create(line));
        }

        return lineResponses;
    }

    @Transactional
    public LineResponse create(LineRequest lineRequest) {
        String name = lineRequest.getName();
        String color = lineRequest.getColor();
        int distance = lineRequest.getDistance();

        validateExistInfo(name, color);

        Station upStation = stationService.findById(lineRequest.getUpStationId());
        Station downStation = stationService.findById(lineRequest.getDownStationId());
        Line line = lineDao.create(Line.create(name, color));
        Section section = Section.create(upStation, downStation, distance);
        sectionService.createInitial(section, line.getId());
        line.setStationsBySections(Sections.create(section));

        return LineResponse.create(line);
    }

    private void validateExistInfo(String name, String color) {
        if (lineDao.existByInfo(name, color)) {
            throw new LineDuplicatedInformationException();
        }
    }

    public void validateExistById(Long lineId) {
        if (!lineDao.existById(lineId)) {
            throw new LineNotFoundException();
        }
    }

}
