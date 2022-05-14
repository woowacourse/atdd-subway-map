package wooteco.subway.service;

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

        Line line = new Line(request.getName(), request.getColor());
        Line savedLine = lineDao.save(line);

        Section section = new Section(savedLine.getId(), upStation, downStation, request.getDistance());
        Section savedSection = sectionDao.save(section);

        return new LineCreateResponse(savedLine.getId(), savedLine.getName(), savedLine.getColor(),
                Stream.of(savedSection.getUpStation(), savedSection.getDownStation())
                        .map(it -> new StationResponse(it.getId(), it.getName()))
                        .collect(Collectors.toList()));
    }
}
