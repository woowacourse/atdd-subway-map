package wooteco.subway.domain;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class Lines {
    private List<Line> lines;

    public Line save(Line line) {
        checkDuplication(line);
        lines.add(line);

        return line;
    }

    private void checkDuplication(Line newLine) {
        boolean existName = lines.stream()
                .anyMatch(line -> line.hasSameName(newLine));

        if(existName){
            throw new IllegalArgumentException("이미 존재하는 노선 이름입니다.");
        }
    }
}
