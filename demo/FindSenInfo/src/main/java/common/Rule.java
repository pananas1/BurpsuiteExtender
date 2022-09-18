package common;

/**
 * 定义扫描规则
 */
public class Rule {
    // 规则名称
    private String ruleName;
    // 规则正则
    private String ruleRegex;

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleRegex() {
        return ruleRegex;
    }

    public void setRuleRegex(String ruleRegex) {
        this.ruleRegex = ruleRegex;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "ruleName='" + ruleName + '\'' +
                ", ruleRegex='" + ruleRegex + '\'' +
                '}';
    }


}