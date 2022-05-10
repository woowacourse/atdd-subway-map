package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.domain.line.Line;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.service.dto.ServiceDtoAssembler;
import wooteco.subway.service.dto.line.LineRequestDto;
import wooteco.subway.service.dto.line.LineResponseDto;

@Service
public class LineService {

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponseDto create(String name, String color) {
        Long lineId = lineRepository.save(new Line(name, color));
        Line line = lineRepository.findById(lineId);
        return ServiceDtoAssembler.lineResponseDto(line);
    }

    public List<LineResponseDto> findAll() {
        return lineRepository.findAll()
                .stream()
                .map(ServiceDtoAssembler::lineResponseDto)
                .collect(Collectors.toUnmodifiableList());
    }

    public LineResponseDto findOne(Long id) {
        return ServiceDtoAssembler.lineResponseDto(lineRepository.findById(id));
    }

    @Transactional
    public void update(Long id, LineRequestDto lineRequestDto) {
        Line line = lineRepository.findById(id);
        line.update(lineRequestDto.getName(), lineRequestDto.getColor());
        lineRepository.update(line);
    }

    @Transactional
    public void remove(Long id) {
        lineRepository.remove(id);
    }
}
