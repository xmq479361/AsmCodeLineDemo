package xmq.hooks;

import java.util.regex.Pattern;

/**
 * @author xmqyeah
 * @CreateDate 2021/12/9 23:48
 */
public interface IMatcher {

    boolean match(String statement);

    public static class MethodPatternMatcher implements IMatcher {
        public MethodPatternMatcher(String regex) {
            pattern =  Pattern.compile(regex);
        }
        Pattern pattern;

        @Override
        public boolean match(String statement) {
            return pattern.matcher(statement).find();
        }
    }
}

