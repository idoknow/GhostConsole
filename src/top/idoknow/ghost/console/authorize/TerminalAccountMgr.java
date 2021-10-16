package top.idoknow.ghost.console.authorize;

import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.ioutil.FileIO;
import top.idoknow.ghost.console.util.crypto.MD5Util;

import java.io.File;
import java.util.HashMap;

/**
 * Load terminal account information and authorize accounts.
 * @author Rock Chin
 */
public class TerminalAccountMgr {

    public static boolean isMultiAccountEnable(){
        return Boolean.parseBoolean(ConsoleMain.cfg.getString("enable-multi-account"));
    }

    private static final HashMap<String,String> accountMap=new HashMap<>();//name:password
    private static final Boolean accountMapSync=false;

    public static int countAccounts(){
        synchronized (accountMapSync) {
            return accountMap.size();
        }
    }

    public static void loadFromFile()throws Exception{
        if (!isMultiAccountEnable()){
            return;
        }
        if(!new File("accounts.list").exists()|| FileIO.read("accounts.list").equals("")){
            return;
        }

        String[] accounts=FileIO.read("accounts.list").split(";");
        synchronized (accountMapSync) {
            for (String account : accounts) {
                String[] acc = account.split(" ");
                if (acc.length < 2) {
                    throw new AccountOperationException("account field invalid:" + account);
                }
                accountMap.put(acc[0], acc[1]);
            }
        }
    }
    public static void syncToFile()throws Exception{
        StringBuilder file=new StringBuilder();
        synchronized (accountMapSync) {
            for (String name : accountMap.keySet()) {
                file.append(name).append(" ").append(accountMap.get(name)).append(";");
            }
        }
        FileIO.write("accounts.list",file.toString());
    }

    public static void register(String name,String password)throws Exception{
        if ("root".equals(name)){
            throw new AccountOperationException("cannot to register \"root\".");
        }
        String mask= MD5Util.stringToMD5(password);
        synchronized (accountMapSync) {
            accountMap.put(name, mask);
        }
        syncToFile();
    }

    public static boolean authorize(String name,String password)throws AccountOperationException{
        if ("root".equals(name)){
            if (password.equals(ConsoleMain.cfg.getString("root-token"))){
                return true;
            }else {
                throw new AccountOperationException("auth failed.");
            }
        }else {
            if(!isMultiAccountEnable()){
                throw new AccountOperationException("multi-account is unable.");
            }
            synchronized (accountMapSync) {
                if (!accountMap.containsKey(name)) {
                    throw new AccountOperationException("no such account:" + name);
                }
                if (accountMap.get(name).equals(MD5Util.stringToMD5(password))) {
                    return true;
                } else {
                    throw new AccountOperationException("auth failed.");
                }
            }
        }
    }
    public static boolean updatePassword(String name,String passwordPlainText) throws Exception {
        synchronized (accountMapSync) {
            if (accountMap.containsKey(name)) {
                accountMap.put(name, MD5Util.stringToMD5(passwordPlainText));
                syncToFile();
                return true;
            } else {
                return false;
            }
        }
    }
}
