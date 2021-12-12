package xmq.hooks;

/**
 * @author xmqyeah
 * @CreateDate 2021/12/9 23:45
 */
public interface IMethodInvoker {

    void insertBefore(String statement);

    void insertAfter(String statement);

    void replace(String statement);

    void replaceParameter(int index, String statement);

    void calcParameter(int index, String statement);

}
