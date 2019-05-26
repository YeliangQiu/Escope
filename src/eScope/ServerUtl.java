package eScope;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerUtl {
    Server[] servers;
    int serverTypeNum;//根据sql计算出服务器类型
    public double getServersTotalPower(){  //得到运行这些服务器最多需要多少功耗
        double serverTotalPower = 0;
        for(Server server:servers){
            serverTotalPower += server.getPower(0);
        }
        return serverTotalPower;
    }

    public double getServerTotalThroughput(){
        double serverTotalThroughput = 0;
        for(Server server:servers){
            serverTotalThroughput += server.getJobs(0);
        }
        return serverTotalThroughput;
    }

    public void setServerMaxEE(){
        int utl= -1;
        double maxEE = -1;
        for(Server server:servers){
            for(int i=0;i<11;i++){
                if(server.getEE(i) > maxEE){
                    maxEE = server.getEE(i);
                    utl = i;
                }
            }
            server.setPeakee_utl(utl);
        }
    }

    public ServerUtl (String sql){ //创建Server对象,参数为数组大小，也就是服务器总个数
        Connection connection = MyConnection.getConn();//数据库链接
        PreparedStatement preparedStatement = null;
        this.serverTypeNum = correctServerNum(sql);
        Server[] bags = new Server[serverTypeNum];
        Map<String,Integer> resultMap = resultSetToMap(sql);
        /*将数据库表中的数据提出，构造成对象*/
        try {
            //String sql1 = "SELECT * FROM benchmark_results_summary a, (SELECT id, max(replace(performance_to_Power_Ratio, ',', '') + 0) AS maxptpr from benchmark_results_summary group by id) b WHERE a.id = b.id AND (replace(a.performance_to_Power_Ratio, ',', '') + 0) = b.maxptpr and a.id in (SELECT id FROM pastehtml.system_overview where (Hardware_Availability like '%2014' or Hardware_Availability like '%2015' or Hardware_Availability like '%2016' or Hardware_Availability like '%2017' or Hardware_Availability like '%2018') and compliment='1');";
            //String sql = "SELECT id,Target_Load,ssj_ops,Average_Active_Power,Performance_to_Power_Ratio,server_num FROM pastehtml.benchmark_results_summary;";
            //String sql = "select * from benchmark_results_summary where id in (select id from system_overview where (Hardware_Availability like '%2014' or Hardware_Availability like '%2015' or Hardware_Availability like '%2016' or Hardware_Availability like '%2017' or Hardware_Availability like '%2018') AND compliment='1');";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int server_type_num = 0;//循环用
            while (rs.next()) {
                double[] power = new double[11];
                int[] jobs = new int[11];
                String[] utl = new String[11];
                int serverNum = rs.getInt(10);
                //System.out.println("xxxxxxxxxxxxxx"+serverNum);
                double[] EE = new double[11];
                String id = rs.getString(1);
                int utlNum = resultMap.get(id);
                for (int i = 0; i < utlNum; i++) {
                    int tmp_ssj = Integer.parseInt(rs.getString(4).replaceAll(",", ""));
                    double tmp_power = Double.parseDouble(rs.getString(5).replaceAll(",", ""));
                    double tmp_EE = Double.parseDouble(rs.getString(6).replaceAll(",", ""));
                    //n += rs.getInt(8); //n是选择的服务器数量
                    power[i] = tmp_power;
                    jobs[i] = tmp_ssj;
                    utl[i] = rs.getString(2);//Target_load;
                    EE[i] = tmp_EE;
                    if (i != (utlNum - 1))
                        rs.next();
                }
                for (int i = 0; i < serverNum; i++) { //有几台同类型的服务器就构造几台Server
                    bags[server_type_num] = new Server(rs.getString(1), power, utl, jobs, EE, serverNum);
                    server_type_num++;
                }
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("总共有服务器：" + bags.length);
        //randFailure(bags,10,serverTypeNum);
        this.servers = bags;
    }

    public Server[] getservers(){
        return servers;
    }

    public int getserverTypeNum(){
        return serverTypeNum;
    }

    public static void UpdateDB(ArrayList<Server> results, String taskID, double threshold, double totalPower, double totaljobs, int optionalNum, float runtime){ //将结果写入数据库
        Connection connection = MyConnection.getConn();//数据库链接
        PreparedStatement preparedStatement = null;
        Map<Server,Integer> map = new HashMap<Server,Integer>();
        for(Server server:results){
            if(map.containsKey(server))
                map.put(server,map.get(server) + 1);
            else
                map.put(server,1);
        }
        System.out.println("mapSize="+map.size());
        for(Map.Entry<Server,Integer> entry:map.entrySet()){ //result表
            System.out.println("id="+entry.getKey().getId()+",Value"+entry.getValue());
            try {
                String sql="INSERT INTO result(machine_id,server_Load,Power,jobs,EE,task_id,total_num) VALUES('" + entry.getKey().getId() + "','" + entry.getKey().getutl(entry.getKey().choose_utl) + "','" + entry.getKey().getPower(entry.getKey().choose_utl) + "','" + entry.getKey().getJobs(entry.getKey().choose_utl) + "','" + entry.getKey().getEE(entry.getKey().choose_utl) + "','" + taskID + "','" + entry.getValue() + "')";
                preparedStatement = connection.prepareStatement(sql); //运行SQL语句
                preparedStatement.executeUpdate();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        try { //summary表
            String sql="INSERT INTO summary(task_id,threshold,totalpower,totaljobs,optional_num,online_num,avg_utilization,median_utilization,runtime) VALUES('" + taskID + "','" + threshold + "','" + totalPower + "','" + totaljobs + "','" +optionalNum+ "','" +results.size()+ "','" + ComputeUtl.avgutl(results) + "','" + ComputeUtl.medianutl(results) + "','" + runtime + "')";
            preparedStatement = connection.prepareStatement(sql); //运行SQL语句
            preparedStatement.executeUpdate();
        }catch(Exception e) {
            e.printStackTrace();
        }
        /*处理Hardware_Threads Memory_Amount_GB Memory_of_THreads*/
        try {
            for(int i=0;i<results.size();i++) {
                String sql = "select Hardware_Threads,Memory_Amount_GB,of_Identical_Nodes,CPU_Enabled_s,Power_Management,Filesystem,JVM_Vendor,JVM_Version,JVM_Commandline_Options,JVM_Affinity,JVM_Instances,JVM_Initial_Heap_MB,JVM_Maximum_Heap_MB,JVM_Address_Bits from sut where id='" + results.get(i).getId() + "'";
                //String sql = "select Hardware_Threads,Memory_Amount_GB from sut where id='" + results.get(i).getId() + "'";
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()) {
                    int Memory_Amount_GB = rs.getInt(2) * 1024;
                    double Hardwre_Threads = Double.parseDouble(rs.getString(1).substring(0,3).replaceAll("\\(", "").trim()) / 2;
                    double Memory_of_Threads = Memory_Amount_GB / Hardwre_Threads;
                    String sql2 = "UPDATE result SET Hardware_Threads=" + Hardwre_Threads + ",Memory_Amount_MB=" + Memory_Amount_GB + ",Memory_of_Threads=" + Memory_of_Threads + ",of_Identical_Nodes='" + rs.getString(3) + "',CPU_Enabled_s='" + rs.getString(4) + "',Power_Management='" + rs.getString(5) + "',Filesystem='" + rs.getString(6) + "',JVM_Vendor='" + rs.getString(7) + "',JVM_Version='" + rs.getString(8) + "',JVM_Commandline_Options='" + rs.getString(9) + "',JVM_Affinity='" + rs.getString(10) + "',JVM_Instances='" + rs.getString(11) + "',JVM_Initial_Heap_MB='" + rs.getString(12) + "',JVM_Maximum_Heap_MB='" + rs.getString(13) + "',JVM_Address_Bits='" + rs.getString(14) + "' where machine_id='" + results.get(i).getId() + "' AND task_id='" + taskID + "' ";
                    preparedStatement = connection.prepareStatement(sql2); //运行SQL语句
                    preparedStatement.executeUpdate();
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        try{
            for(int i=0;i<results.size();i++){
                String sql = "select powerNorm from benchmark_results_summary where id='" + results.get(i).getId() + "' and ssj_ops=0;";
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()) {
                    String sql2 = "UPDATE result SET EP=" + rs.getDouble(1) + " where machine_id=" + results.get(i).getId() + "";
                    preparedStatement = connection.prepareStatement(sql2); //运行SQL语句
                    preparedStatement.executeUpdate();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void randFailure(Server[] bags,int failureRatio,int server_type_num){ //随机使机器故障
        int failureServerNum = server_type_num * failureRatio / 100;
        int failureServer;
        for(int i=0;i<failureServerNum;i++){
            failureServer = (int)(Math.random()*server_type_num);
            bags[failureServer].failureFlag = 0;
        }
    }

    private int correctServerNum(String sql){
        Connection connection = MyConnection.getConn();//数据库链接
        PreparedStatement preparedStatement = null;
        List<String> resultList = new ArrayList<>();
        int serverNum = 0;
        System.out.println();
        try{
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                String id = rs.getString(1);
                if(!resultList.contains(id)){
                    resultList.add(rs.getString(1));
                    serverNum +=  rs.getInt("server_num");
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return serverNum;
    }
    private Map<String,Integer> resultSetToMap(String sql){
        HashMap<String,Integer> resultMap = new HashMap<>();
        Connection connection = MyConnection.getConn();
        PreparedStatement preparedStatement = null;
        try{
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                String id = rs.getString(1);
                if(resultMap.containsKey(id))
                    resultMap.put(id,resultMap.get(id) + 1);
                else
                    resultMap.put(id,1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return resultMap;
    }

}
