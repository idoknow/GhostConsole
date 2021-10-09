package top.idoknow.ghost.console.adapter.jrer;

public class JRERAdapter {
    public static JRERegister jreRegister;

    public synchronized static void init()throws Exception{
        JRERAdapter.jreRegister=new JRERegister();
        JRERAdapter.jreRegister.sync();
    }
}
