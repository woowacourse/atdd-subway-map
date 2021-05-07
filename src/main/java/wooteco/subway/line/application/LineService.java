package wooteco.subway.line.application;

import org.springframework.stereotype.Service;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineDao;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.SectionDao;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationDao;
import wooteco.subway.station.dto.StationResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    public LineResponse save(final LineRequest lineRequest) {
        Line savedLine = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        sectionDao.save(new Section(savedLine.id(), lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance()));
        List<Station> stations = Arrays.asList(stationDao.findById(lineRequest.getUpStationId()).orElseThrow(() -> new IllegalStateException("없는 역임!")), stationDao.findById(lineRequest.getDownStationId()).orElseThrow(() -> new IllegalStateException("없는 역임!")));
        return new LineResponse(savedLine.id(), savedLine.name(), savedLine.color(), stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList())
        );
    }
}
