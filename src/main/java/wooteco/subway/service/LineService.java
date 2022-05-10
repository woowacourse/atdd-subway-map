package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.line.LineDao;
import wooteco.subway.dao.section.SectionDao;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.line.LineResponse;
import wooteco.subway.dto.line.LineSaveRequest;
import wooteco.subway.dto.line.LineUpdateRequest;
import wooteco.subway.exception.NotFoundException;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(final LineDao lineDao, final StationDao stationDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public LineResponse save(final LineSaveRequest lineSaveRequest) {
        if (lineDao.existByName(lineSaveRequest.getName())) {
            throw new IllegalStateException("이미 존재하는 노선 이름입니다.");
        }
        long savedLineId = lineDao.save(lineSaveRequest.toLine());
        saveLineSection(savedLineId, lineSaveRequest);
        return findById(savedLineId);
    }

    private void saveLineSection(final long lineId, final LineSaveRequest lineSaveRequest) {
        Station upStation = stationDao.findById(lineSaveRequest.getUpStationId());
        Station downStation = stationDao.findById(lineSaveRequest.getDownStationId());
        Section section = new Section(lineId, upStation, downStation, lineSaveRequest.getDistance());
        sectionDao.save(section);
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(line -> LineResponse.of(line, findStationsByLineId(line.getId())))
                .collect(Collectors.toList());
    }

    public LineResponse findById(final Long lineId) {
        return LineResponse.of(lineDao.findById(lineId), findStationsByLineId(lineId));
    }

    private List<Station> findStationsByLineId(final long lineId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        return sections.calculateSortedStations();
    }

    @Transactional
    public void update(final long lineId, final LineUpdateRequest request) {
        checkExistLine(lineId);
        lineDao.update(request.toLineWithId(lineId));
    }

    @Transactional
    public void delete(final Long lineId) {
        checkExistLine(lineId);
        lineDao.delete(lineId);
    }

    private void checkExistLine(final Long lineId) {
        if (!lineDao.existById(lineId)) {
            throw new NotFoundException("존재하지 않는 Line입니다.");
        }
    }
}
