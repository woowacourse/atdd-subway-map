package wooteco.subway.service;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.EmptyResultException;

@Service
@Transactional
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

        Station upStation = findStationById(lineRequest.getUpStationId());
        Station downStation = findStationById(lineRequest.getDownStationId());

        sectionDao.save(new Section(upStation, downStation, lineRequest.getDistance()), savedLineId);
        return LineResponse.from(savedLineId, line);
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        return lineDao.findById(id)
            .map(LineResponse::from)
            .orElseThrow(throwEmptyLineResultException());
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineDao.findAll().stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
    }

    public boolean deleteById(Long id) {
        return lineDao.deleteById(id);
    }

    public boolean updateById(Long id, LineRequest lineRequest) {
        Line line = findLineById(id);

        line.update(lineRequest.getName(), lineRequest.getColor());
        return lineDao.updateById(id, line);
    }

    public void insertSection(Long id, SectionRequest sectionRequest) {
        Line line = findLineById(id);
        Station upStation = findStationById(sectionRequest.getUpStationId());
        Station downStation = findStationById(sectionRequest.getDownStationId());
        Section section = new Section(upStation, downStation, sectionRequest.getDistance());

        line.insertSection(section);
        sectionDao.update(line.getSections());
        sectionDao.save(section, line.getId());
    }

    public void deleteStation(Long lineId, Long stationId) {
        Station station = findStationById(stationId);
        Line line = findLineById(lineId);
        Long sectionId = line.deleteSection(station);
        checkEmptyResult(sectionId);
        sectionDao.update(line.getSections());
        sectionDao.delete(sectionId);
    }

    private Station findStationById(Long id) {
        return stationDao.findById(id)
            .orElseThrow((throwEmptyStationException()));
    }

    private Line findLineById(Long id) {
        return lineDao.findById(id)
            .orElseThrow(throwEmptyLineResultException());
    }

    private void checkEmptyResult(Long sectionId) {
        if (sectionId == -1L) {
            throw new EmptyResultException("삭제할 구간을 찾지 못했습니다.");
        }
    }

    private Supplier<EmptyResultException> throwEmptyStationException() {
        return () -> new EmptyResultException("해당 역을 찾을 수 없습니다.");
    }

    private Supplier<EmptyResultException> throwEmptyLineResultException() {
        return () -> new EmptyResultException("해당 노선을 찾을 수 없습니다.");
    }
}
