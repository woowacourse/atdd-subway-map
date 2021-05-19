package wooteco.subway.domain.line.section.rule.SectionAddRule;

import java.util.Arrays;
import java.util.List;

public class SectionAddRuleFactory {

    public static List<SectionAddRule> create() {
        return Arrays.asList(
                new RegisterTerminal(),
                new UpStationExists(),
                new DownStationExists()
        );
    }

}
