package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineCreateRequest;
import wooteco.subway.dto.LineCreateResponse;
import wooteco.subway.dto.StationResponse;

@Service
public class LineCreateService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineCreateService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public LineCreateResponse create(LineCreateRequest request) {
        Station upStation = stationDao.findById(request.getUpStationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
        Station downStation = stationDao.findById(request.getDownStationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));

        Line savedLine = saveLine(request.getName(), request.getColor());
        Section savedSection = saveSection(savedLine.getId(), upStation, downStation, request.getDistance());

        return getLineCreateResponse(savedLine, savedSection);
    }

    private Line saveLine(String name, String color) {
        Line line = new Line(name, color);
        return lineDao.save(line);
    }

    private Section saveSection(long lineId, Station upStation, Station downStation, int distance) {
        Section section = new Section(lineId, upStation, downStation, distance);
        return sectionDao.save(section);
    }

    private LineCreateResponse getLineCreateResponse(Line line, Section section) {
        List<StationResponse> stationResponses = Stream.of(section.getUpStation(), section.getDownStation())
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());

        return new LineCreateResponse(line, stationResponses);
    }
}
