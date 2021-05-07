package wooteco.subway.service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import wooteco.subway.controller.dto.request.line.LineCreateRequestDto;
import wooteco.subway.controller.dto.request.line.LineUpdateRequestDto;
import wooteco.subway.controller.dto.response.line.LineResponseDto;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.HttpException;

@Service
public class LineService {
    private static final String LINE_NAME_OR_COLOR_DUPLICATE_ERROR_MESSAGE = "노선의 이름 또는 색깔이 이미 존재합니다.";

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponseDto createLine(LineCreateRequestDto lineCreateRequestDto) {
        Line newLine = new Line(lineCreateRequestDto.getName(), lineCreateRequestDto.getColor());
        try {
            Long id = lineDao.save(newLine);
            return new LineResponseDto(id, newLine);
        } catch (DuplicateKeyException e) {
            throw new HttpException(BAD_REQUEST, LINE_NAME_OR_COLOR_DUPLICATE_ERROR_MESSAGE);
        }
    }

    public LineResponseDto getLineById(Long id) {
        return lineDao.findById(id)
            .map(LineResponseDto::new)
            .orElseThrow(() -> new HttpException(BAD_REQUEST, "Id에 해당하는 노선이 없습니다."));
    }

    public List<LineResponseDto> getAllLines() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
            .map(LineResponseDto::new)
            .collect(Collectors.toList());
    }

    public int updateLine(Long id, LineUpdateRequestDto lineUpdateRequestDto) {
        try {
            return lineDao.update(id, lineUpdateRequestDto.getColor(), lineUpdateRequestDto.getName());
        } catch (DuplicateKeyException e) {
            throw new HttpException(BAD_REQUEST, LINE_NAME_OR_COLOR_DUPLICATE_ERROR_MESSAGE);
        }
    }

    public int deleteLineById(Long id) {
        return lineDao.deleteById(id);
    }
}
