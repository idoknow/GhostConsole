package top.idoknow.ghost.test;

import top.idoknow.ghost.console.ioutil.log.LogMySQL;

public class InitDataBaseTest {
    public static void main(String[] args) {
        try {
            LogMySQL.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
