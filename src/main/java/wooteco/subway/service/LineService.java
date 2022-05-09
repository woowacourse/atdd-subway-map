package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.service.dto.ServiceDtoAssembler;
import wooteco.subway.service.dto.line.LineRequestDto;
import wooteco.subway.service.dto.line.LineResponseDto;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponseDto create(String name, String color) {
        validateNameNotDuplicated(name);
        validateColorNotDuplicated(color);
        Long lineId = lineDao.save(new Line(name, color));
        Line line = lineDao.findById(lineId);
        return ServiceDtoAssembler.lineResponseDto(line);
    }

    private void validateNameNotDuplicated(String name) {
        if (lineDao.existsByName(name)) {
            throw new IllegalArgumentException("해당 이름의 지하철 노선이 이미 존재합니다");
        }
    }

    private void validateColorNotDuplicated(String color) {
        if (lineDao.existsByColor(color)) {
            throw new IllegalArgumentException("해당 색상의 지하철 노선이 이미 존재합니다");
        }
    }

    public List<LineResponseDto> findAll() {
        return lineDao.findAll()
                .stream()
                .map(ServiceDtoAssembler::lineResponseDto)
                .collect(Collectors.toUnmodifiableList());
    }

    public LineResponseDto findOne(Long id) {
        return ServiceDtoAssembler.lineResponseDto(lineDao.findById(id));
    }

    public void update(Long id, LineRequestDto lineRequestDto) {
        lineDao.update(id, lineRequestDto.getName(), lineRequestDto.getColor());
    }

    public void remove(Long id) {
        lineDao.remove(id);
    }
}
