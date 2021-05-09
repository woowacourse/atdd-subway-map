package wooteco.subway.line;

import org.springframework.stereotype.Service;
import wooteco.subway.section.*;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.StationResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public LineResponse save(LineRequest lineRequest) {
        if (lineDao.existsByNameOrColor(lineRequest.getName(), lineRequest.getColor())) {
            throw new IllegalArgumentException("노선 이름 또는 색이 이미 존재합니다.");
        }
        Station upStation = stationDao.findById(lineRequest.getUpStationId()).orElseThrow(() -> new IllegalArgumentException("노선 ID가 존재하지 않습니다."));
        Station downStation = stationDao.findById(lineRequest.getDownStationId()).orElseThrow(() -> new IllegalArgumentException("노선 ID가 존재하지 않습니다."));
        if (lineRequest.getUpStationId().equals(lineRequest.getDownStationId())) {
            throw new IllegalArgumentException("상행 종점역과 하행 종점역이 같으면 안됩니다.");
        }
        Section section = new Section(upStation, downStation, Distance.of(lineRequest.getDistance()));

        Line newLine = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        sectionDao.save(newLine.getId(), section);

        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id).orElseThrow(() -> new IllegalArgumentException("노선 ID가 존재하지 않습니다."));

        List<SectionEntity> sectionEntities = sectionDao.findAllByLineId(id);

        Set<Section> sections = new HashSet<>();
        for (SectionEntity sectionEntity : sectionEntities) {
            Station upStation = stationDao.findById(sectionEntity.getUpStationId()).get();
            Station downStation = stationDao.findById(sectionEntity.getDownStationId()).get();
            sections.add(new Section(upStation, downStation, Distance.of(sectionEntity.getDistance())));
        }

        Line lineWithSections = new Line(line.getId(), line.getName(), line.getColor(), new Sections(sections));

        List<StationResponse> stationResponses = lineWithSections.path()
                .stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());

        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
    }

    public void updateById(Long id, Line line) {
        if (!lineDao.findById(id).isPresent()) {
            throw new IllegalArgumentException("노선 ID가 존재하지 않습니다.");
        }
        if (lineDao.existsByNameOrColorWithDifferentId(line.getName(), line.getColor(), id)) {
            throw new IllegalArgumentException("노선 이름 또는 색이 이미 존재합니다.");
        }
        lineDao.updateById(id, line);
    }

    public void deleteById(Long id) {
        if (!lineDao.findById(id).isPresent()) {
            throw new IllegalArgumentException("노선 ID가 존재하지 않습니다.");
        }
        lineDao.deleteById(id);
    }
}
