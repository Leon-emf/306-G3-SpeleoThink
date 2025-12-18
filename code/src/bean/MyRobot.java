package api_robot7links_test.bean;

import java.io.Serializable;

/**
 *
 * @author AudergonV01
 */
public class MyRobot implements Serializable {
    
    private String ip;
    private String hostname;
    private int id;
    private int pw;

    public MyRobot(String ip, String hostname, int id, int pw) {
        this.ip = ip;
        this.hostname = hostname;
        this.id = id;
        this.pw = pw;
    }

    public MyRobot() {
        ip = "0.0.0.0";
        hostname = "Inconnu";
        id = 0;
        pw = 0;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPw() {
        return pw;
    }

    public void setPw(int pw) {
        this.pw = pw;
    }
    
    @Override
    public String toString(){
        return hostname + " @" + ip + " [id: " + id + ", pw: " + pw + "]";
    }
    
    
    
}
