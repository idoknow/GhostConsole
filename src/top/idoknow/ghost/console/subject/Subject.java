package top.idoknow.ghost.console.subject;

/**
 * Defines a subject(slave,terminal) in GhostJ system.
 * Handler of terminal or slave will include a Subject to describes itself
 * ,and this subject instance will be used to make log or other operations.
 * @author Rock Chin
 */
public final class Subject {

    public static final int UNDEFINED=0,SLAVE=1,TERMINAL=2,CONSOLE=3;

    private static long suidIndex=0;
    private final long SUID=suidIndex++;
    public long getSUID(){
        return SUID;
    }

    //unchangeable field
    private final String token;
    private int identity=UNDEFINED;

    private boolean available=false;

    public Subject(String token,int identity){
        this.token=token;
        this.identity=identity;
    }

    public String getToken() {
        return token;
    }

    public int getIdentity() {
        return identity;
    }

    public void setAvailable(boolean available){
        this.available=available;
    }

    public boolean isAvailable() {
        return available;
    }
    //e.g. CONSOLE:console
    public String getText(){
        String idText="";
        switch (identity){
            case 0:
                idText="Undefined";
                break;
            case 1:
                idText="Slave";
                break;
            case 2:
                idText="Terminal";
                break;
            case 3:
                idText="Console";
                break;
        }
        return "(SUID:"+SUID+")"+idText+":"+token;
    }
}
