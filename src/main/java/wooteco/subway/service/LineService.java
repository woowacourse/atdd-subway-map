package wooteco.subway.service;

import java.util.List;
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
import wooteco.subway.exception.EmptyResultException;

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
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Long savedLineId = lineDao.save(line);

        Station upStation = stationDao.findById(lineRequest.getUpStationId())
            .orElseThrow(() -> new EmptyResultException("해당 역을 찾을 수 없습니다."));
        Station downStation = stationDao.findById(lineRequest.getDownStationId())
            .orElseThrow(() -> new EmptyResultException("해당 역을 찾을 수 없습니다."));

        sectionDao.save(Section.from(upStation, downStation, lineRequest.getDistance()), savedLineId);
        return LineResponse.from(savedLineId, line);
    }

    public LineResponse findById(Long id) {
        return lineDao.findById(id)
            .map(LineResponse::from)
            .orElseThrow(() -> new EmptyResultException("해당 노선을 찾을 수 없습니다."));
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll().stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
    }

    public boolean deleteById(Long id) {
        return lineDao.deleteById(id);
    }

    public boolean updateById(Long id, LineRequest lineRequest) {
        Line line = lineDao.findById(id)
            .orElseThrow(() -> new EmptyResultException("해당 노선을 찾을 수 없습니다."));

        line.update(lineRequest.getName(), lineRequest.getColor());
        return lineDao.updateById(id, line);
    }
}
