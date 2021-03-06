package top.idoknow.ghost.console.adapter.jrer;

import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.ioutil.FileIO;

import java.io.File;
import java.util.ArrayList;

public class JRERegister {
    /**
     * 保存每一个jre实体文件信息的类
     */
    public static class jreFile{
        String fileName;
        String filePath;
        long version;
        String tag="";
        public jreFile(String fileName,String filePath,long version){
            this.fileName=fileName;
            this.version=version;
            this.filePath=filePath;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFilePath() {
            return filePath;
        }

        public long getVersion() {
            return version;
        }
        public void setVersion(long version){
            this.version=version;
        }

        public String getTag() {
            return tag;
        }
        public void setTag(String tag){
            this.tag=tag;
        }
    }
    ArrayList<jreFile> files=new ArrayList<>();
    public ArrayList<jreFile> getFiles(){
        return files;
    }

    /**
     * 登记jre文件版本的文件的每个字段
     */
//	private LinkedHashMap fileFields=new LinkedHashMap<>();
    private ArrayList<jreFile> fileFields=new ArrayList<>();
    /**
     * 读取jreVer.txt
     */
    private void readJREVerTXT()throws Exception{
        if (!isEnable())
            return;
        //清空之前的登记添加新的记录
        fileFields.clear();
        if(!new File("jre"+File.separatorChar+"jreVer.txt").exists()){
            FileIO.write("jre"+File.separatorChar+"jreVer.txt","");
            return;
        }
        String[] verFields = FileIO.read("jre"+File.separatorChar+"jreVer.txt").replaceAll("\r","").split("\n");
        for(String fieldStr:verFields){
            //分割每个版本登记字段
            String[] spt =fieldStr.split(" ");
            if(spt.length>=3) {
                jreFile jreFile=new jreFile(spt[0], spt[1], Long.parseLong(spt[2]));
                //如果有附加参数
                if(spt.length>=4){
                    jreFile.tag=spt[3];
                }
                fileFields.add(jreFile);
            }
//				fileFields.put(fieldStr.split(" ")[0],Long.parseLong(fieldStr.split(" ")[1]));
        }
    }

    /**
     * 获取和参数匹配的jre文件字段
     * @param path
     * @param name
     * @return
     */
    private jreFile getFileField(String path,String name){
        if (!isEnable())
            return null;
        for(jreFile jreFile:fileFields){
//			Out.say("getField", path + " " + name + " "+jreFile.filePath+" "+jreFile.fileName);
            if(jreFile.filePath.equals(path)&&(jreFile.fileName).equals(name)) {
                return jreFile;
            }
        }
        return null;
    }
    /**
     * 与文件目录同步
     */
    public void sync()throws Exception{
        if (!isEnable())
            return;
        files.clear();
        readJREVerTXT();
        File jre=new File("jre");
        //不存在jre文件夹
        if(!jre.exists()||!jre.isDirectory()){
            jre.mkdir();
            return;
        }
        scanFile("jre");
    }
    private void scanFile(String dir){
        if (!isEnable())
            return;
        File[] files=new File(dir).listFiles();
        if (files==null){
            return;
        }
        for(File file:files){
            if(file.isDirectory()){
                scanFile(file.getPath());
            }else if(file.isFile()){
//				this.files.add(new jreFile(file.getPath(),fileFields.containsKey(file.getPath())?fileFields.get(file.getPath()):2100000000));
                try {
                    jreFile jreFile=getFileField(getFilePath(file.getPath()), file.getName());
                    jreFile toAdd=new jreFile(file.getName(), getFilePath(file.getPath()),  jreFile==null?0:jreFile.version);
                    if(jreFile!=null)
                        toAdd.tag=jreFile.tag;
                    this.files.add(toAdd);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 将实例中的登记输出到文件
     */
    public void writeToFile()throws Exception{
        if (!isEnable())
            return;
        StringBuffer text=new StringBuffer();
        for(jreFile file:files){
            text.append(file.fileName+" "+file.filePath+" "+file.version+" "+file.tag+"\n");
        }
        FileIO.write("jre"+File.separatorChar+"jreVer.txt",text.toString().replaceAll("/","\\\\"));
    }
    private String getFilePath(String wholePath){
        int loca=wholePath.lastIndexOf(File.separatorChar);
        return wholePath.substring(0,loca);
    }

    public static boolean isEnable(){
        return Boolean.parseBoolean(ConsoleMain.cfg.getString("enable-green-jre-management"));
    }
}
