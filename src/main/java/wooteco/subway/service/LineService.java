package wooteco.subway.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.UpdatedSection;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
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

        Station upStation = findStationById(lineRequest.getUpStationId());
        Station downStation = findStationById(lineRequest.getDownStationId());

        sectionDao.save(new Section(upStation, downStation, lineRequest.getDistance()), savedLineId);
        return LineResponse.from(savedLineId, line);
    }

    public LineResponse findById(Long id) {
        return lineDao.findById(id)
            .map(LineResponse::from)
            .orElseThrow(throwEmptyLineResultException());
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
        Line line = findLineById(id);

        line.update(lineRequest.getName(), lineRequest.getColor());
        return lineDao.updateById(id, line);
    }

    public void insertSection(Long id, SectionRequest sectionRequest) {
        Line line = findLineById(id);
        Station upStation = findStationById(sectionRequest.getUpStationId());
        Station downStation = findStationById(sectionRequest.getDownStationId());

        Section section = new Section(upStation, downStation, sectionRequest.getDistance());
        Optional<Section> updatedSection = line.insertSection(section);
        updatedSection.ifPresent(sectionDao::update);
        sectionDao.save(section, line.getId());
    }

    public void deleteStation(Long lineId, Long stationId) {
        Station station = findStationById(stationId);
        Line line = findLineById(lineId);
        UpdatedSection updatedSection = line.deleteStation(station);
        if (updatedSection.hasUpdatedSection()) {
            sectionDao.update(updatedSection.getUpdatedSection());
        }
        sectionDao.delete(updatedSection.getDeletedSectionId());
    }

    private Station findStationById(Long id) {
        return stationDao.findById(id)
            .orElseThrow((throwEmptyStationException()));
    }

    private Line findLineById(Long id) {
        return lineDao.findById(id)
            .orElseThrow(throwEmptyLineResultException());
    }

    private Supplier<EmptyResultException> throwEmptyStationException() {
        return () -> new EmptyResultException("해당 역을 찾을 수 없습니다.");
    }

    private Supplier<EmptyResultException> throwEmptyLineResultException() {
        return () -> new EmptyResultException("해당 노선을 찾을 수 없습니다.");
    }
}
