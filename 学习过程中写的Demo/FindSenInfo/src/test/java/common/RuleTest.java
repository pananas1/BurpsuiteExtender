package common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleTest {

    @Test
    public void test1(){
        Rule rule = new Rule();
        String ruleName = rule.getRuleName();
    }

}