package eScope.analysis;

import eScope.MyConnection;
import eScope.Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author : qiuyeliang
 * create at:  2019/5/15  11:00
 * @description: 用于给出图表结果
 */
public class DataAnalysis {
    private String serverId;
    private String publishedYear;
    private String hardwareYear;
    private int score;
    private double EP;
    private int peak_EE;
    private double peak_EE_power;
    private int EE100;
    private double power100;
    private double idle;
    private double DR;
    private double PEEP;
    private String peakeeUtl;
    private double distance;
    private double slope;
    private int totalnodes;
    private String CPUinfo;

    public DataAnalysis(String serverId) {
        this.serverId = serverId;
    }

    public void analysisOverView(){
        Connection connection = MyConnection.getConn();//数据库链接
        PreparedStatement preparedStatement = null;
        try {
            String sql = "SELECT * FROM pastehtml.system_overview where id = " + serverId;
            System.out.println(sql);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                publishedYear = rs.getString(1);
                hardwareYear = rs.getString(10);
                System.out.println(hardwareYear);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void analysisBenchResult(){
        Connection connection = MyConnection.getConn();//数据库链接
        PreparedStatement preparedStatement = null;
        int[] EE = new int[11];
        double[] power = new double[11];
        String[] utl = new String[11];
        try {
            String sql = "SELECT * FROM pastehtml.benchmark_results_summary where id = " + serverId;
            System.out.println(sql);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int i=0;
            while(rs.next()){
                EE[i] = Integer.parseInt(rs.getString(6).replaceAll(",",""));
                utl[i] = rs.getString(2);
                power[i] = Double.parseDouble(rs.getString(5));
                if(i == 0){
                    EE100 = Integer.parseInt(rs.getString(6).replaceAll(",",""));
                    power100 = power[i];
                }
                if(i == 10){
                    score = Integer.parseInt(rs.getString(7).replaceAll(",",""));
                    EP = Double.parseDouble(rs.getString(9));
                    idle = Double.parseDouble(rs.getString(8));
                }
                i++;
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int max_EE = Integer.MIN_VALUE;
        int max_utl = -1;
        for(int i=0;i<EE.length;i++){
            if(max_EE < EE[i]) {
                max_EE = EE[i];
                max_utl = i;
            }
        }
        peak_EE = max_EE;
        peak_EE_power = power[max_utl];
        DR = 1-idle;
        PEEP = peak_EE*1.0 /EE100;
        peakeeUtl = utl[max_utl];
        distance = 1 - Integer.parseInt(utl[max_utl].replaceAll("%",""))*1.0 / 100;
        slope = (PEEP-1)/distance;
//        System.out.println("Score:"+peak_EE);
//        System.out.println("PEAK_EE:"+peak_EE);
//        System.out.println("Power@PeakEE:"+peak_EE_power);
//        System.out.println("EE 100%utl:"+EE100);
//        System.out.println("Power@100%utl:"+power100);
//        System.out.println("IDLE:"+idle);
//        System.out.println("DR:"+DR);
//        System.out.println("UtilizationPEAKEE:"+peak_EE_power);
//        System.out.println("Distance:"+distance);
//        System.out.println("Slope:"+slope);
    }

    public void analysisSut(){
        Connection connection = MyConnection.getConn();//数据库链接
        PreparedStatement preparedStatement = null;
        try {
            String sql = "SELECT * FROM pastehtml.sut where id = " + serverId;
            System.out.println(sql);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                totalnodes = Integer.parseInt(rs.getString(4));
                CPUinfo = rs.getString(12);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void print(){
        analysisBenchResult();
        analysisOverView();
        analysisSut();
        System.out.println("serverId"+serverId);
        System.out.println("serverId"+publishedYear);
        System.out.println("serverId"+hardwareYear);
        System.out.println("Score:"+peak_EE);
        System.out.println("PEAK_EE:"+peak_EE);
        System.out.println("Power@PeakEE:"+peak_EE_power);
        System.out.println("EE 100%utl:"+EE100);
        System.out.println("Power@100%utl:"+power100);
        System.out.println("IDLE:"+idle);
        System.out.println("DR:"+DR);
        System.out.println("UtilizationPEAKEE:"+peak_EE_power);
        System.out.println("Distance:"+distance);
        System.out.println("Slope:"+slope);
    }
}
