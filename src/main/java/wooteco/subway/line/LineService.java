package wooteco.subway.line;

import org.springframework.stereotype.Service;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.exception.LineError;
import wooteco.subway.line.exception.LineException;
import wooteco.subway.section.Section;
import wooteco.subway.section.SectionDao;
import wooteco.subway.section.SectionRequest;
import wooteco.subway.section.Sections;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.exception.StationError;
import wooteco.subway.station.exception.StationException;

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
        LineEntity lineEntity = lineDao.findById(id)
                                       .orElseThrow(() -> new LineException(LineError.NOT_EXIST_LINE_ID));
        return new Line(lineEntity, sectionsByLineId(id));
    }

    private Sections sectionsByLineId(Long id) {
        return new Sections(sectionDao.findSections(id));
    }

    private Station stationById(Long id) {
        return stationDao.findById(id)
                         .orElseThrow(() -> new StationException(StationError.NO_STATION_BY_ID));
    }

    public LineResponse createLine(LineRequest lineRequest) {
        if (isExistingLineByName(lineRequest.getName())) {
            throw new LineException(LineError.ALREADY_EXIST_LINE_NAME);
        }

        if (notExistingStation(lineRequest.getDownStationId()) || notExistingStation(lineRequest.getUpStationId())) {
            throw new LineException(LineError.NOT_EXIST_STATION_ON_LINE_REQUEST);
        }
        Long createdLineId = lineDao.save(lineRequest.getName(), lineRequest.getColor());
        SectionRequest sectionRequest = new SectionRequest(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        saveSection(createdLineId, sectionRequest);
        return findById(createdLineId);
    }

    private boolean notExistingStation(Long stationId) {
        return !stationDao.findById(stationId)
                          .isPresent();
    }

    private boolean notExistingLine(Long id) {
        return !lineDao.findById(id)
                       .isPresent();
    }

    private boolean isExistingLineByName(String name) {
        return lineDao.findByName(name)
                      .isPresent();
    }

    public void modifyLine(Long id, LineRequest lineRequest) {
        if (notExistingLine(id)) {
            throw new LineException(LineError.NOT_EXIST_LINE_ID);
        }
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }

    public void deleteLine(Long id) {
        if (notExistingLine(id)) {
            throw new LineException(LineError.NOT_EXIST_LINE_ID);
        }
        lineDao.delete(id);
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        if (notExistingLine(lineId)) {
            throw new LineException(LineError.NOT_EXIST_LINE_ID);
        }
        Sections sections = sectionsByLineId(lineId);

        sections.add(sectionOf(sectionRequest));

        updateSections(lineId, sections);
    }

    private Section sectionOf(SectionRequest sectionRequest) {
        Station upStation = stationById(sectionRequest.getUpStationId());
        Station downStation = stationById(sectionRequest.getDownStationId());
        return new Section(upStation, downStation, sectionRequest.getDistance());
    }

    public void deleteSection(Long lineId, Long stationId) {
        if (notExistingLine(lineId)) {
            throw new LineException(LineError.NOT_EXIST_LINE_ID);
        }
        Sections sections = sectionsByLineId(lineId);
        sections.delete(stationById(stationId));

        updateSections(lineId, sections);
    }

    private void updateSections(Long lineId, Sections sections) {
        sectionDao.deleteSectionsOf(lineId);
        for (Section section : sections.getSections()) {
            saveSection(lineId, SectionRequest.of(section));
        }
    }

    private void saveSection(Long lineId, SectionRequest sr) {
        sectionDao.save(lineId, sr.getUpStationId(), sr.getDownStationId(), sr.getDistance());
    }
}
