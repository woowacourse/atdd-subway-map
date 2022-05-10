package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionService sectionService;
    private final StationService stationService;

    public LineService(final LineDao lineDao, SectionService sectionService,
                       StationService stationService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    public LineResponse save(final LineRequest lineRequest) {
        if (lineDao.existByName(lineRequest.getName())) {
            throw new IllegalStateException("이미 존재하는 노선 이름입니다.");
        }

        Line savedLine = lineDao.save(lineRequest.toLine());
        sectionService.save(savedLine.getId(),
                new SectionRequest(lineRequest.getUpStationId(), lineRequest.getDownStationId(),
                        lineRequest.getDistance()));

        return new LineResponse(savedLine.getId(), savedLine.getName(), savedLine.getColor(),
                stationService.findByStationsId(sectionService.findAllStationByLineId(savedLine.getId())));
    }

    public List<LineResponse> findAll() {
        List<Line> all = lineDao.findAll();
        return all.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(),
                        stationService.findByStationsId(sectionService.findAllStationByLineId(line.getId()))))
                .collect(Collectors.toList());
    }

    public LineResponse findById(final Long lineId) {
        Line line = lineDao.findById(lineId);
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
                stationService.findByStationsId(sectionService.findAllStationByLineId(line.getId())));
    }

    public void update(final Line line) {
        lineDao.update(line);
    }

    public void delete(final Long lineId) {
        if (!lineDao.existById(lineId)) {
            throw new NoSuchElementException("없는 Line 입니다.");
        }
        lineDao.delete(lineId);
    }
}
