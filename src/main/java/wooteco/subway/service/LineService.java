package wooteco.subway.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse create(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), lineRequest.getDistance());
        Line newLine = lineDao.save(line);

        Section saveSection = new Section(newLine.getId(), lineRequest.getUpStationId(), lineRequest.getDownStationId(),
                lineRequest.getDistance());
        sectionDao.save(saveSection);
        List<StationResponse> stations = extractUniqueStationsFromSections(newLine);
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), stations);
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(
                        line.getId(),
                        line.getName(),
                        line.getColor(),
                        extractUniqueStationsFromSections(line)))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id);
        List<StationResponse> stations = extractUniqueStationsFromSections(line);
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
    }

    public void changeField(LineResponse findLine, LineRequest lineRequest) {
        lineDao.changeLineName(findLine.getId(), lineRequest.getName());
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }

    private ArrayList<StationResponse> extractUniqueStationsFromSections(Line line) {
        List<Section> sections = sectionDao.findAllByLineId(line.getId());
        Set<StationResponse> stations = new LinkedHashSet<>();

        for (Section section : sections) {
            Station upStation = stationDao.findById(section.getUpStationId());
            Station downStation = stationDao.findById(section.getDownStationId());
            stations.add(new StationResponse(upStation));
            stations.add(new StationResponse(downStation));
        }
        return new ArrayList<StationResponse>(stations);
    }
}
