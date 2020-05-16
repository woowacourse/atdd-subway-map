package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineStationRequest;
import wooteco.subway.admin.dto.LineWithStationsResponse;
import wooteco.subway.admin.dto.domain.LineDto;
import wooteco.subway.admin.repository.LineRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineDto save(LineDto lineDto) {
        Line save = lineRepository.save(lineDto.toLine());
        return LineDto.of(save);
    }

    public List<LineWithStationsResponse> showLines() {
        List<Line> lines = lineRepository.findAll();

        List<List<Long>> stationIdsPerLines = lines.stream()
                .map(Line::getLineStationsId)
                .collect(Collectors.toList());

        List<List<Station>> collect = stationIdsPerLines.stream()
                .map(stationIds -> stationService.findAllById(stationIds))
                .collect(Collectors.toList());

        List<LineWithStationsResponse> result = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            result.add(LineWithStationsResponse.of(lines.get(i), collect.get(i)));
        }

        return result;
    }

    @Transactional
    public LineDto updateLine(Long id, LineRequest line) {
        Line persistLine = lineRepository.findById(id)
                .orElseThrow(RuntimeException::new);

        persistLine.update(line.toLine());
        return LineDto.of(lineRepository.save(persistLine));
    }

    @Transactional
    public void deleteLineBy(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addLineStation(LineStationRequest view) {
        Line persistLine = lineRepository.findById(view.getLineId())
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 line이 없습니다."));

        Station station = stationService.findByName(view.getStationName());

        if (view.getPreStationName().isEmpty()) {
            LineStation lineStation = new LineStation(null, station.getId());
            persistLine.addLineStation(lineStation);
            lineRepository.save(persistLine);
            return;
        }

        Station preStation = stationService.findByName(view.getPreStationName());
        LineStation lineStation = new LineStation(preStation.getId(), station.getId());
        persistLine.addLineStation(lineStation);
        lineRepository.save(persistLine);
    }

//    @Transactional
//    public void addLineStation(Long lineId, LineStationCreateRequest lineStationCreateRequest) {
//        Line persistLine = lineRepository.findById(lineId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 id의 line이 없습니다."));
//
//        if (lineStationCreateRequest.getPreStationId() == null) {
//            persistLine.addLineStation(lineStationCreateRequest.toLineStation());
//            lineRepository.save(persistLine);
//            return;
//        }
//
//        persistLine.addLineStation(lineStationCreateRequest.toLineStation());
//        lineRepository.save(persistLine);
//    }

    @Transactional
    public void removeLineStation(Long lineId, Long stationId) {
        Line persistLine = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 line이 없습니다."));

        persistLine.removeLineStationById(stationId);
        lineRepository.save(persistLine);
    }

    @Transactional
    public LineWithStationsResponse findLineWithStationsBy(Long lineId) {
        Line persistLine = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 line이 없습니다."));

        List<Long> stationIds = persistLine.getLineStationsId();

        List<Station> stations = stationService.findAllById(stationIds);

        return LineWithStationsResponse.of(persistLine, stations);
    }
}
