package wooteco.subway.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import wooteco.subway.controller.dto.request.LineUpdateRequestDto;
import wooteco.subway.controller.dto.request.LineRequestDto;
import wooteco.subway.controller.dto.response.LineResponseDto;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private static final String NOT_FOUND_LINE_BY_ID_ERROR_MESSAGE = "Id에 해당하는 노선이 없습니다.";

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponseDto createLine(LineRequestDto lineRequestDto) {
        validateLineNameDuplicate(lineRequestDto);
        Line newLine = new Line(lineRequestDto.getName(), lineRequestDto.getColor());
        Long id = lineDao.save(newLine);
        return new LineResponseDto(id, newLine);
    }

    private void validateLineNameDuplicate(LineRequestDto lineRequestDto) {
        Optional<Line> lineFoundByName = lineDao.findByName(lineRequestDto.getName());
        lineFoundByName.ifPresent(foundLine -> {
            throw new IllegalArgumentException("이미 존재하는 노선 이름입니다.");
        });
    }

    public LineResponseDto getLineById(Long id) {
        Line foundLine = lineDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_LINE_BY_ID_ERROR_MESSAGE));
        return new LineResponseDto(foundLine);
    }

    public List<LineResponseDto> getAllLines() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineResponseDto::new)
                .collect(Collectors.toList());
    }

    public int updateLine(Long id, LineUpdateRequestDto lineUpdateRequestDto) {
        return lineDao.update(id, lineUpdateRequestDto.getColor(), lineUpdateRequestDto.getName());
    }

    public int deleteLineById(Long id) {
        return lineDao.deleteById(id);
    }
}
