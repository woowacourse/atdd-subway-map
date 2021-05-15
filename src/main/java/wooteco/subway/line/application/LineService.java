package wooteco.subway.line.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineDao;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.SectionDao;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.LineUpdateRequest;
import wooteco.subway.line.dto.SectionRequest;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationDao;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao, final StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public LineResponse save(final LineRequest lineRequest) {
        validateDuplication(lineRequest);
        Line line = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        line.addSection(addSection(lineRequest, line));
        return new LineResponse(line);
    }

    private Section addSection(final LineRequest lineRequest, final Line line) {
        if (Objects.nonNull(lineRequest.getUpStationId()) && Objects.nonNull(lineRequest.getDownStationId())) {
            return sectionDao.save(toSection(line, lineRequest));
        }
        return null;
    }

    private Section toSection(final Line line, final LineRequest lineRequest) {
        Station upStation = findStationById(lineRequest.getUpStationId());
        Station downStation = findStationById(lineRequest.getDownStationId());
        return new Section(line, upStation, downStation, lineRequest.getDistance());
    }

    private Section toSection(final Line line, final SectionRequest sectionRequest) {
        Station upStation = findStationById(sectionRequest.getUpStationId());
        Station downStation = findStationById(sectionRequest.getDownStationId());
        return new Section(line, upStation, downStation, sectionRequest.getDistance());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(final Long lineId) {
        return new LineResponse(findLineById(lineId));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void update(final Long id, final LineUpdateRequest lineUpdateRequest) {
        Line line = findLineById(id);
        line.changeName(lineUpdateRequest.getName());
        line.changeColor(lineUpdateRequest.getColor());
        lineDao.update(line);
    }

    @Transactional
    public void delete(final Long id) {
        Line line = findLineById(id);
        lineDao.delete(line.id());
    }

    @Transactional
    public void addSection(final Long lineId, final SectionRequest sectionRequest) {
        Line line = findLineById(lineId);
        Section targetSection = toSection(line, sectionRequest);

        line.addSection(targetSection);

        sectionDao.deleteByLineId(line.id());
        sectionDao.batchInsert(line.sortedSections());
    }

    @Transactional
    public void deleteSectionByStationId(final Long lineId, final Long stationId) {
        Line line = findLineById(lineId);
        Station targetStation = findStationById(stationId);

        line.deleteStation(targetStation);

        sectionDao.deleteByLineId(line.id());
        sectionDao.batchInsert(line.sortedSections());
    }

    private void validateDuplication(final LineRequest lineRequest) {
        if (lineDao.existByName(lineRequest.getName())) {
            throw new IllegalStateException("이미 등록된 이름임!");
        }

        if (lineDao.existByColor(lineRequest.getColor())) {
            throw new IllegalStateException("이미 있는 색깔임!");
        }
    }

    private Line findLineById(final Long id) {
        return lineDao.findById(id).orElseThrow(() -> new IllegalArgumentException("없는 노선임!"));
    }

    private Station findStationById(final Long stationId) {
        return stationDao.findById(stationId).orElseThrow(() -> new IllegalStateException("없는 역임!"));
    }
}