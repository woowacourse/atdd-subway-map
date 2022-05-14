package wooteco.subway.domain;

import wooteco.subway.exception.ClientException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lines {

    private final List<Line> lines;

    public Lines(List<Line> lines) {
        this.lines = new ArrayList<>(lines);
    }

    public void add(Line line) {
        validateDuplicateLine(line);
        lines.add(line);
    }

    private void validateDuplicateLine(Line request) {
        boolean duplicate = lines.stream()
                .anyMatch(line -> line.isSameName(request.getName()));

        if (duplicate) {
            throw new ClientException("이미 등록된 지하철노선입니다.");
        }
    }

    public void validateCanModify(Line request) {
        boolean duplicate = lines.stream()
                .filter(line -> line.isSameName(request.getName()))
                .anyMatch(line -> line.getColor().equals(request.getColor()));

        if (duplicate) {
            throw new ClientException("해당 지하철 노선이 존재하고 있습니다.");
        }
    }

    public void validateExist(Long id) {
        boolean exist = lines.stream()
                .anyMatch(line -> line.getId() == id);

        if (!exist) {
            throw new ClientException("존재하지 않는 노선입니다.");
        }
    }

    public void validateCanDelete(Long id) {
        boolean exist = lines.stream()
                .anyMatch(line -> line.getId() == id);

        if (exist) {
            throw new ClientException("노선에 등록되어 있는 역은 제거할 수 없습니다.");
        }
    }

    public List<Line> getLines() {
        return Collections.unmodifiableList(lines);
    }
}
