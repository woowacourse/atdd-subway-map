package wooteco.subway.line.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.common.exception.ExistsColorException;
import wooteco.subway.common.exception.ExistsNameException;
import wooteco.subway.common.exception.NotFoundException;
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
        Line line = lineDao.save(lineRequest.toEntity());
        line.addSection(saveSection(lineRequest, line));
        return new LineResponse(line);
    }

    private Section saveSection(final LineRequest lineRequest, final Line line) {
        if (lineRequest.empty()) {
            throw new NotFoundException("상행역과 하행역이 없음!!");
        }
        return sectionDao.save(toSection(line, lineRequest.getUpStationId(),
                lineRequest.getDownStationId(), lineRequest.getDistance()));
    }

    private Section toSection(final Line line, final Long upStationId, final Long downStationId, final int distance) {
        Station upStation = findStationById(upStationId);
        Station downStation = findStationById(downStationId);
        return new Section(line, upStation, downStation, distance);
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
        Section targetSection = toSection(line, sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(), sectionRequest.getDistance());

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
            throw new ExistsNameException("이미 있는 노선 이름임!!");
        }

        if (lineDao.existByColor(lineRequest.getColor())) {
            throw new ExistsColorException("이미 있는 컬러임!!");
        }
    }

    private Line findLineById(final Long id) {
        return lineDao.findById(id).orElseThrow(() -> new NotFoundException("찾을 수 없는 노선임!!"));
    }

    private Station findStationById(final Long stationId) {
        return stationDao.findById(stationId).orElseThrow(() -> new NotFoundException("없는 역임!"));
    }
}