package eScope;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class Server implements Comparable<Server>{
    /** Server power at one energy efficiency  */
    private double[] power = new double[11]; //10个挡位

    /** ssj jobs completed at one energy efficiency with power  , jobs= power* energy_efficiency */
    private int[] jobs = new int [11];//10个挡位
    private String id;
    private String[] utl = new String[11];//10个挡位
    private int num;//此型号服务器的总数
    public double[] EE = new double[11];
    public int choose_utl = -1; //此台服务器所运行的utl
    public int failureFlag = 1; //此台服务器是否失效，1表示正常,可以用bool型替代
    public Boolean selected = false;
    /***
     * 构造器
     */
    public Server(String id,double[] power,String[] utl,int[] jobs,double[] EE,int num){
        this.id = id;
        this.jobs = jobs;
        this.power = power;
        this.utl = utl;
        this.num = num;
        this.EE = EE;
    }
    public double getPower(int i) {
        return power[i];
    }

    public int getJobs(int i) {

        return jobs[i];
    }

    public String getId() {
        return id;
    }

    public String getutl(int i)
    {
        return utl[i];
    }

    public int getnum() {
        return num;
    }

    public int getChoose_utl(int i) {
        return choose_utl;
    }

    public int compareTo(Server comparestu) {
        if(comparestu.choose_utl<this.choose_utl)
           return 1;
        else if(comparestu.choose_utl>this.choose_utl)
            return -1;
        else
            return 0;
    }
//    public int compareTo(Server compareEE){
//        if(compareEE.getEE(0) > this.getEE(0))
//            return 1;
//        else
//            return -1;
//    }
    public double getEE(int i){
        return EE[i];
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null)
            return false;
        if(this.getClass() != obj.getClass())
            return false;
        Server server = (Server)obj;
        return id.equals(server.id) && choose_utl == server.choose_utl;
    }

    @Override
    public int hashCode(){
        return (id + choose_utl).hashCode();
    }

}
