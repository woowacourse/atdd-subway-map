package wooteco.subway.service.line;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.controller.dto.request.line.LineCreateRequestDto;
import wooteco.subway.controller.dto.request.line.LineUpdateRequestDto;
import wooteco.subway.controller.dto.response.line.LineCreateResponseDto;
import wooteco.subway.controller.dto.response.line.LineResponseDto;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.section.Section;
import wooteco.subway.exception.HttpException;

@Transactional
@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public LineCreateResponseDto createLine(LineCreateRequestDto lineCreateRequestDto) {
        validateSection(lineCreateRequestDto.getUpStationId(), lineCreateRequestDto.getDownStationId());
        try {
            Line newLine = new Line(lineCreateRequestDto.getName(), lineCreateRequestDto.getColor());
            Line savedLine = lineDao.save(newLine);
            Section newSection = new Section(savedLine, lineCreateRequestDto.getUpStationId(), lineCreateRequestDto.getDownStationId(), lineCreateRequestDto.getDistance());
            Section savedSection = sectionDao.save(newSection);
            return new LineCreateResponseDto(savedLine, savedSection);
        } catch (DataIntegrityViolationException e) {
            throw new HttpException(BAD_REQUEST, "생성할 노선의 이름 또는 색깔이 중복되었거나, 상행 종점역 또는 하행 종점역이 존재하지 않습니다.");
        }
    }

    private void validateSection(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new HttpException(BAD_REQUEST, "상행 종점역과 하행 종점역은 같을 수 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponseDto> getAllLines() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
            .map(LineResponseDto::new)
            .collect(Collectors.toList());
    }

    public int updateLine(Long id, LineUpdateRequestDto lineUpdateRequestDto) {
        return lineDao.update(id, lineUpdateRequestDto.getName(), lineUpdateRequestDto.getColor());
    }

    public int deleteLineById(Long id) {
        sectionDao.deleteAllByLineId(id);
        return lineDao.deleteById(id);
    }
}
