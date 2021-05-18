package wooteco.subway.line;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.exception.LineError;
import wooteco.subway.line.exception.LineException;
import wooteco.subway.section.*;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.exception.StationError;
import wooteco.subway.station.exception.StationException;

import java.util.ArrayList;
import java.util.List;
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

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                      .stream()
                      .map(LineResponse::new)
                      .collect(Collectors.toList());
    }


    public LineResponse findById(Long id) {
        return new LineResponse(lineById(id));
    }

    private Line lineById(Long id) {
        try {
            LineEntity lineEntity = lineDao.findById(id)
                                           .orElseThrow(() -> new LineException(LineError.NOT_EXIST_LINE_ID));
            return new Line(lineEntity, sectionsByLineId(id));
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new LineException(LineError.INCORRECT_SIZE_LINE_FIND_BY_ID);
        }
    }

    private Sections sectionsByLineId(Long id) {
        List<SectionEntity> sectionEntities = sectionDao.findByLineId(id);
        List<Section> sections = new ArrayList<>();
        for (SectionEntity sectionEntity : sectionEntities) {
            Station upStation = stationById(sectionEntity.getUpStationId());
            Station downStation = stationById(sectionEntity.getDownStationId());
            sections.add(new Section(upStation, downStation, sectionEntity.getDistance()));
        }
        return new Sections(sections);
    }

    private Station stationById(Long id) {
        return stationDao.findById(id)
                         .orElseThrow(() -> new StationException(StationError.NO_STATION_BY_ID));
    }

    public LineResponse createLine(LineRequest lineRequest) {
        if (isLineExist(lineRequest.getName())) {
            throw new LineException(LineError.ALREADY_EXIST_LINE_NAME);
        }
        if (notExistingStation(lineRequest.getDownStationId()) || notExistingStation(lineRequest.getUpStationId())) {
            throw new LineException(LineError.NOT_EXIST_STATION_ON_LINE_REQUEST);
        }
        Long createdLineId = lineDao.save(lineRequest.getName(), lineRequest.getColor());
        SectionRequest sectionRequest = new SectionRequest(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        addSection(createdLineId, sectionRequest);
        return findById(createdLineId);
    }

    private boolean notExistingStation(Long stationId) {
        return !stationDao.findById(stationId)
                          .isPresent();
    }

    private boolean isLineExist(String name) {
        try {
            return lineDao.findByName(name)
                          .isPresent();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new LineException(LineError.INCORRECT_SIZE_LINE_FIND_BY_NAME);
        }
    }

    public void addSection(Long lineId, SectionRequest sr) {
        sectionDao.save(lineId, sr.getUpStationId(), sr.getDownStationId(), sr.getDistance());
    }

    public void modifyLine(Long id, LineRequest lineRequest) {
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }

    public void deleteLine(Long id) {
        lineDao.delete(id);
    }
}
