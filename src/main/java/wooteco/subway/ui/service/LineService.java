package wooteco.subway.ui.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineResponse;

@Service
public class LineService {
    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse create(LineRequest lineRequest) {
        String name = lineRequest.getName();
        String color = lineRequest.getColor();
        Line line = new Line(name, color);
        Line createdLine = lineDao.save(line);

        Station upStation = stationDao.findById(lineRequest.getUpStationId());
        Station downStation = stationDao.findById(lineRequest.getDownStationId());
        return LineResponse.from(createdLine, List.of(upStation, downStation));
    }

    public List<LineResponse> findAll() {
        final List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> LineResponse.from(line, findStations(line)))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        final Line line = lineDao.findById(id);
        List<Station> stations = findStations(line);
        return LineResponse.from(line, stations);
    }

    private List<Station> findStations(Line line) {
        final List<Section> sections = sectionDao.findByLine(line.getId());
        return new Sections(line, sections).getStations();
    }

    public void modify(Long id, LineRequest lineRequest) {
        final Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineDao.update(line);
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }
}
