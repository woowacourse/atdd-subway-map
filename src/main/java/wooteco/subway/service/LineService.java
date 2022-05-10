package wooteco.subway.service;

import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.LineDto;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.StationRepository;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final LineDao lineDao;

    public LineService(LineRepository lineRepository, StationRepository stationRepository,
                       LineDao lineDao) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.lineDao = lineDao;
    }

    public Line save(LineRequest lineRequest) {
        Section section = new Section(stationRepository.findById(lineRequest.getUpStationId()),
                stationRepository.findById(lineRequest.getDownStationId()), lineRequest.getDistance());
        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), new Sections(section));
        try {
            Long lindId = lineRepository.save(line);
            return lineRepository.findById(lindId);
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("이미 존재하는 노선 이름입니다.");
        }
    }

    public List<Line> findAll() {
        return lineRepository.findAll();
    }

    public Line findById(Long id) {
        try {
            return lineRepository.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new EmptyResultDataAccessException("존재하지 않는 노선입니다", 1);
        }
    }

    public void update(Long id, Line line) {
        try {
            lineDao.update(id, LineDto.from(line));
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("이미 존재하는 노선 이름입니다.");
        }
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
