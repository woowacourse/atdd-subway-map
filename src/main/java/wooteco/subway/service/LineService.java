package wooteco.subway.service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.controller.dto.request.line.LineCreateRequestDto;
import wooteco.subway.controller.dto.request.line.LineUpdateRequestDto;
import wooteco.subway.controller.dto.response.line.LineCreateResponseDto;
import wooteco.subway.controller.dto.response.line.LineResponseDto;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.exception.HttpException;

@Transactional
@Service
public class LineService {
    private static final String LINE_NAME_OR_COLOR_DUPLICATE_ERROR_MESSAGE = "노선의 이름 또는 색깔이 이미 존재합니다.";

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public LineCreateResponseDto createLine(LineCreateRequestDto lineCreateRequestDto) {
        try {
            Line newLine = new Line(lineCreateRequestDto.getName(), lineCreateRequestDto.getColor());
            Line savedLine = lineDao.save(newLine);
            Section newSection = new Section(savedLine, lineCreateRequestDto.getUpStationId(), lineCreateRequestDto.getDownStationId(), lineCreateRequestDto.getDistance());
            Section savedSection = sectionDao.save(newSection);
            return new LineCreateResponseDto(savedLine, savedSection);
        } catch (DuplicateKeyException e) {
            throw new HttpException(BAD_REQUEST, LINE_NAME_OR_COLOR_DUPLICATE_ERROR_MESSAGE);
        }
    }

    @Transactional(readOnly = true)
    public LineResponseDto getLineById(Long id) {
        return lineDao.findById(id)
            .map(LineResponseDto::new)
            .orElseThrow(() -> new HttpException(BAD_REQUEST, "Id에 해당하는 노선이 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<LineResponseDto> getAllLines() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
            .map(LineResponseDto::new)
            .collect(Collectors.toList());
    }

    public int updateLine(Long id, LineUpdateRequestDto lineUpdateRequestDto) {
        try {
            return lineDao.update(id, lineUpdateRequestDto.getName(), lineUpdateRequestDto.getColor());
        } catch (DuplicateKeyException e) {
            throw new HttpException(BAD_REQUEST, LINE_NAME_OR_COLOR_DUPLICATE_ERROR_MESSAGE);
        }
    }

    public int deleteLineById(Long id) {
        sectionDao.deleteAllByLineId(id);
        return lineDao.deleteById(id);
    }
}
