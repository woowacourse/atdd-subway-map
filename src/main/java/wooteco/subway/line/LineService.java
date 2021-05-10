package wooteco.subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.line.exception.DuplicateLineNameException;
import wooteco.subway.station.StationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    @Autowired
    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        String name = lineRequest.getName();
        String color = lineRequest.getColor();
        Long upStationId = lineRequest.getUpStationId();
        Long downStationId = lineRequest.getDownStationId();
        int distance = lineRequest.getDistance();

        validateDuplicateLineName(name);

        Line save = this.lineRepository.save(new Line(name, color, stationRepository.findById(upStationId), stationRepository.findById(downStationId), distance));
        LineResponse lineResponse = new LineResponse(save.getId(), save.getName(), save.getColor());

        return lineResponse;
    }

    private void validateDuplicateLineName(String name) {
        this.lineRepository.findByName(name).ifPresent(line -> {
            throw new DuplicateLineNameException(name);
        });
    }

    public List<LineResponse> findAllLines() {
        return this.lineRepository.findAll().stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(long id) {
        // TODO section 레포지토리로 부터 불러오고, 정렬시키기

        return LineResponse.from(this.lineRepository.findById(id));
    }

    public LineResponse updateLine(long id, String name, String color) {
        Line line = lineRepository.update(id, new Line(name, color));
        return LineResponse.from(line);
    }
}
