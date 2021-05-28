package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.LineEntity;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.exception.LineError;
import wooteco.subway.line.exception.LineException;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.service.StationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineDao lineDao;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, StationService stationService, SectionService sectionService) {
        this.lineDao = lineDao;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        LineEntity lineEntity = lineDao.findById(id)
                .orElseThrow(() -> new LineException(LineError.NOT_EXIST_LINE_ID));

        Sections sections = sectionService.sectionsByLineId(id);
        List<StationResponse> stationResponses = StationResponse.listOf(sections.path());
        return new LineResponse(lineEntity, stationResponses);
    }

    public Long createLine(LineRequest lineRequest) {
        checkDuplicatedName(lineRequest.getName());
        checkPassingStationsExist(lineRequest);

        Long createdLineId = lineDao.save(lineRequest.getName(), lineRequest.getColor());
        sectionService.initSection(createdLineId, SectionRequest.from(lineRequest));

        return createdLineId;
    }

    private void checkDuplicatedName(String name) {
        if (lineDao.findByName(name).isPresent()) {
            throw new LineException(LineError.ALREADY_EXIST_LINE_NAME);
        }
    }

    private void checkPassingStationsExist(LineRequest lineRequest) {
        if (!stationService.isPresent(lineRequest.getDownStationId()) || !stationService.isPresent(lineRequest.getUpStationId())) {
            throw new LineException(LineError.NOT_EXIST_STATION_ON_LINE_REQUEST);
        }
    }

    public void modifyLine(Long lineId, LineRequest lineRequest) {
        checkLineExist(lineId);
        lineDao.update(lineId, lineRequest.getName(), lineRequest.getColor());
    }

    public void deleteLine(Long lineId) {
        checkLineExist(lineId);
        lineDao.delete(lineId);
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        checkLineExist(lineId);
        sectionService.addSection(lineId, sectionRequest);
    }

    public void deleteSection(Long lineId, Long stationId) {
        checkLineExist(lineId);
        sectionService.deleteSection(lineId, stationId);
    }

    private void checkLineExist(Long lineId) {
        if (!lineDao.findById(lineId).isPresent()) {
            throw new LineException(LineError.NOT_EXIST_LINE_ID);
        }
    }
}
