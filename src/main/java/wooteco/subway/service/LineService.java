package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.service.dto.line.LineRequestDTO;
import wooteco.subway.service.dto.line.LineResponseDTO;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponseDTO create(LineRequestDTO lineRequestDTO) {
        validateDuplicate(lineRequestDTO);
        Line line = lineDao.save(new Line(lineRequestDTO.getName(), lineRequestDTO.getColor()));
        return new LineResponseDTO(line);
    }

    public LineResponseDTO findById(Long id) {
        findAll().stream()
                .filter(it -> it.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 해당 노선이 존재하지 않습니다."));

        return new LineResponseDTO(lineDao.findById(id));
    }

    @Transactional(readOnly = true)
    public List<LineResponseDTO> findAll() {
        var allLines = lineDao.findAll();

        return allLines.stream()
                .map(LineResponseDTO::new)
                .collect(Collectors.toList());
    }

    private void validateDuplicate(LineRequestDTO lineRequestDTO) {
        validateDuplicateName(lineRequestDTO.getName());
        validateDuplicateColor(lineRequestDTO.getColor());
    }

    private void validateDuplicateName(String name) {
        if (isDuplicatedName(name)) {
            throw new IllegalArgumentException("[ERROR] 중복된 이름이 존재합니다.");
        }
    }

    private boolean isDuplicatedName(String name) {
        return lineDao.findAll().stream()
                .anyMatch(it -> it.isName(name));
    }

    private void validateDuplicateColor(String color) {
        if (isDuplicatedColor(color)) {
            throw new IllegalArgumentException("[ERROR] 중복된 색이 존재합니다.");
        }
    }

    private boolean isDuplicatedColor(String color) {
        return lineDao.findAll().stream()
                .anyMatch(it -> it.isColor(color));
    }

    public void updateById(Long id, LineRequestDTO lineRequestDTO) {
        validateNonFoundId(id);
        validateExistName(id, lineRequestDTO.getName());
        validateExistColor(id, lineRequestDTO.getColor());

        lineDao.update(id, lineRequestDTO.getName(), lineRequestDTO.getColor());
    }

    public void deleteById(Long id) {
        validateNonFoundId(id);

        lineDao.deleteById(id);
    }

    private void validateNonFoundId(Long id) {
        lineDao.findAll().stream()
                .filter(it -> it.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 노선 입니다."));
    }

    private void validateExistName(Long id, String name) {
        lineDao.findAll().stream()
                .filter(it -> !it.getId().equals(id))
                .filter(it -> it.getName().equals(name))
                .findAny()
                .ifPresent(s -> {
                    throw new NoSuchElementException("[ERROR] 이미 존재하는 이름입니다.");
                });
    }

    private void validateExistColor(Long id, String color) {
        lineDao.findAll().stream()
                .filter(it -> !it.getId().equals(id))
                .filter(it -> it.getColor().equals(color))
                .findAny()
                .ifPresent(s -> {
                    throw new NoSuchElementException("[ERROR] 이미 존재하는 색상입니다.");
                });
    }
}
