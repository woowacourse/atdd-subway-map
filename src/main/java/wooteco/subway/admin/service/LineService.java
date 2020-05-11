package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.repository.LineRepository;

import java.util.List;

@Service
public class LineService {
    private final LineRepository lineRepository;

    public LineService(final LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public Line save(Line line) {
        if(isDuplicateName(line)) {
            throw new IllegalArgumentException("중복된 이름입니다!");
        }
        return lineRepository.save(line);
    }

    public List<Line> showLines() {
        return lineRepository.findAll();
    }

    public Line updateLine(Long id, Line line) {
        Line persistLine = findById(id);
        persistLine.update(line);
        return lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private boolean isDuplicateName(Line line) {
        return lineRepository.findAllName().stream()
                .anyMatch(lineName -> lineName.equals(line.getName()));
    }

    public Line addLineStation(Long id, LineStation lineStation) {
        Line persistLine = findById(id);
        persistLine.addLineStation(lineStation);
        return lineRepository.save(persistLine);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = findById(lineId);
        line.removeLineStationById(stationId);
        updateLine(lineId, line);
    }

    public Line findById(Long id){
        return lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("노선을 찾을수 없습니다."));
    }
}
