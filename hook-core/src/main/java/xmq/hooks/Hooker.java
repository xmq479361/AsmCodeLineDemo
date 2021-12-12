package xmq.hooks;

/**
 * @author xmqyeah
 * @CreateDate 2021/12/10 0:17
 */
public abstract class Hooker extends IMatcher.MethodPatternMatcher implements IMethodInvoker {
    public Hooker(String regex) {
        super(regex);
    }

}
