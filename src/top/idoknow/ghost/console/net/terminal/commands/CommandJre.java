package top.idoknow.ghost.console.net.terminal.commands;

import top.idoknow.ghost.console.adapter.jrer.JRERAdapter;
import top.idoknow.ghost.console.adapter.jrer.JRERegister;
import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.ioutil.log.LogMgr;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.terminal.CommandSyntaxException;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

/**
 * Manage green jre registration.
 * @author Rock Chin
 */
public class CommandJre extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) throws Exception {

        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }

        if (!Boolean.parseBoolean(ConsoleMain.cfg.getString("enable-green-jre-management"))){
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("JRE Management is unable.").flush();
            return;
        }

        if (params.length<2){
            throw new CommandSyntaxException("!jre <operation>");
        }

        switch (params[1]){
            case "view":{
                JRERAdapter.jreRegister.sync();
                int i=0;
                for (JRERegister.jreFile jreFile:JRERAdapter.jreRegister.getFiles()){
                    ((IHasWrapper)handler).getWrapper().wrapTimeLn(i+++"\t"+jreFile.getVersion()
                            +"\t"+jreFile.getFileName()+"\t"+jreFile.getFilePath()+"\t"+jreFile.getTag());
                }
                ((IHasWrapper)handler).getWrapper().wrapTimeLn("index\tversion\tname\tpath\ttag");
                ((IHasWrapper)handler).getWrapper().wrapTimeLn("List all exist file.");
                ((IHasWrapper)handler).getWrapper().flush();
                break;
            }
            case "reg":{
                if (params.length<4){
                    throw new CommandSyntaxException("!jre reg <version:long> <index0,index1,index2...index n> ");
                }

                long version=0;
                //check if version is a long
                try {
                    version=Long.parseLong(params[2]);
                }catch (Exception e){
                    throw new CommandSyntaxException("!jre reg <version:long> <index0,index1,index2...index n>");
                }

                //parse indexes
                String[] indexes=params[3].split(",");
                for (String index:indexes){
                    if (index.equals("all")){//set all
                        for (JRERegister.jreFile jreFile:JRERAdapter.jreRegister.getFiles()){
                            jreFile.setVersion(version);
                            ((IHasWrapper)handler).getWrapper().wrapTimeLn("Reg:"+params[2]+"\t"+jreFile.getFilePath()+"\t"+jreFile.getFileName()+"\t"+jreFile.getTag());
                            LogMgr.logMessage(handler.getSubject(),"JREReg", "Reg:"+params[2]+"\t"+jreFile.getFilePath()+"\t"+jreFile.getFileName()+"\t"+jreFile.getTag());
                        }
                    }else {//a index
                        int indexNum=0;
                        //Check if this is a integer
                        try {
                            indexNum=Integer.parseInt(index);
                        }catch (Exception e){
                            throw new CommandSyntaxException("Index should be Integer.");
                        }
                        //check index valid
                        if (indexNum>=JRERAdapter.jreRegister.getFiles().size()||indexNum<0){
                            throw new CommandSyntaxException("No such index:"+indexNum);
                        }
                        //set the version of single file
                        JRERegister.jreFile jreFile=JRERAdapter.jreRegister.getFiles().get(indexNum);
                        jreFile.setVersion(version);
                        ((IHasWrapper)handler).getWrapper().wrapTimeLn("Reg:"+params[2]+"\t"+jreFile.getFilePath()+"\t"+jreFile.getFileName()+"\t"+jreFile.getTag());
                        LogMgr.logMessage(handler.getSubject(),"JREReg", "Reg:"+params[2]+"\t"+jreFile.getFilePath()+"\t"+jreFile.getFileName()+"\t"+jreFile.getTag());
                    }
                    ((IHasWrapper)handler).getWrapper().flush();
                }
                JRERAdapter.jreRegister.writeToFile();
                ((IHasWrapper)handler).getWrapper().wrapTimeLn("Write registration to file.");
                LogMgr.logMessage(handler.getSubject(),"JREReg", "Write registration to file.");
                ((IHasWrapper)handler).getWrapper().flush();
                break;
            }
            case "tag":{
                if (params.length<4){
                    throw new CommandSyntaxException("!jre tag <tag> <index0,index1,index2...index n> ");
                }

                //parse indexes
                String[] indexes=params[3].split(",");
                for (String index:indexes){
                    if (index.equals("all")){//set all
                        for (JRERegister.jreFile jreFile:JRERAdapter.jreRegister.getFiles()){
                            jreFile.setTag(params[2]);
                            ((IHasWrapper)handler).getWrapper().wrapTimeLn("Tag:"+params[2]+"\t"+jreFile.getFilePath()+"\t"+jreFile.getFileName()+"\t"+jreFile.getTag());
                            LogMgr.logMessage(handler.getSubject(),"JREReg", "Tag:"+params[2]+"\t"+jreFile.getFilePath()+"\t"+jreFile.getFileName()+"\t"+jreFile.getTag());
                        }
                    }else {//a index
                        int indexNum=0;
                        //Check if this is a integer
                        try {
                            indexNum=Integer.parseInt(index);
                        }catch (Exception e){
                            throw new CommandSyntaxException("Index should be Integer.");
                        }
                        //check index valid
                        if (indexNum>=JRERAdapter.jreRegister.getFiles().size()||indexNum<0){
                            throw new CommandSyntaxException("No such index:"+indexNum);
                        }
                        //set the version of single file
                        JRERegister.jreFile jreFile=JRERAdapter.jreRegister.getFiles().get(indexNum);
                        jreFile.setTag(params[2]);
                        ((IHasWrapper)handler).getWrapper().wrapTimeLn("Tag:"+params[2]+"\t"+jreFile.getFilePath()+"\t"+jreFile.getFileName()+"\t"+jreFile.getTag());
                        LogMgr.logMessage(handler.getSubject(),"JREReg", "Tag:"+params[2]+"\t"+jreFile.getFilePath()+"\t"+jreFile.getFileName()+"\t"+jreFile.getTag());
                    }
                    ((IHasWrapper)handler).getWrapper().flush();
                }
                JRERAdapter.jreRegister.writeToFile();
                ((IHasWrapper)handler).getWrapper().wrapTimeLn("Write registration to file.");
                LogMgr.logMessage(handler.getSubject(),"JREReg", "Write registration to file.");
                ((IHasWrapper)handler).getWrapper().flush();
                break;
            }
            case "rmtag":{
                ((IHasWrapper)handler).getWrapper().wrapTimeLn("Expired operation.");
                break;
            }
            default:{
                throw new CommandSyntaxException("!jre <operation>");
            }
        }
    }
}
