package eScope;

import java.io.InputStream;
import java.util.*;
import java.util.Date;
import java.sql.*;
import java.text.SimpleDateFormat;


public class Solution {
    public void powerToThroughtout(int threshold,String sql) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String start_time = (df.format(new Date()));
        long currentTime = System.currentTimeMillis();
        float runtime;
        ServerUtl serverUtl = new ServerUtl(sql);
        Server[] bags = serverUtl.getservers();
        Arrays.sort(bags);
        DCProblem kp = new DCProblem(bags, threshold, serverUtl.getserverTypeNum(), 300000); //背包算法、

        kp.solve();
        System.out.println(" -------- 该背包问题实例的解: --------- ");
        System.out.println("最优值：" + kp.getBestValue());
        System.out.println("最优解【选取的背包】: ");
        ArrayList<Server> results = kp.getBestSolution();
        runtime = (System.currentTimeMillis() - currentTime) / 1000f;
        double jobs = 0;
        double power = 0;
        Collections.sort(results); //按利用率从高到低排序
        System.out.println("排序后：");
        System.out.println("共有服务器：" + results.size());
        for (int i = 0; i < results.size(); i++) {
            System.out.println("id=" + results.get(i).getId() + ",Load=" + results.get(i).getutl(results.get(i).choose_utl) + ",Power=" + results.get(i).getPower(results.get(i).choose_utl) + ",value=" + results.get(i).getJobs(results.get(i).choose_utl) + ",EE=" + results.get(i).getEE(results.get(i).choose_utl) + ",chooseNum" + results.get(i).choose_utl);
            jobs = jobs + results.get(i).getJobs(results.get(i).choose_utl);
            power = power + results.get(i).getPower(results.get(i).choose_utl);
        }
        System.out.println("总jobs数" + jobs);
        System.out.println("总Power数" + power);
        System.out.println("服务器总台数" + results.size());
        serverUtl.UpdateDB(results, start_time, threshold, power, jobs, serverUtl.serverTypeNum, runtime);//更新数据库
        System.out.println("执行耗时 : " + runtime + " 秒 ");
    }
    public void throughtoutToPower(ServerUtl serverUtl,double threshold){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String start_time = (df.format(new Date()));
        long currentTime = System.currentTimeMillis();
        float runtime;
        System.out.println("运行全部服务器共需要"+(int)serverUtl.getServersTotalPower());
        System.out.println("此数据中心的最大吞吐量"+serverUtl.getServerTotalThroughput());
        DCtoPower dctoPower = new DCtoPower(serverUtl.getservers(),threshold,serverUtl.getserverTypeNum(),1000000,(int)serverUtl.getServersTotalPower());
        dctoPower.solve();
        ArrayList<Server> results = dctoPower.getBestSolution();
        double jobs = 0,power = 0;
        runtime = (System.currentTimeMillis() - currentTime) / 1000f;
        for (int i = 0; i < results.size(); i++) {
            System.out.println("id=" + results.get(i).getId() + ",Load=" + results.get(i).getutl(results.get(i).choose_utl) + ",Power=" + results.get(i).getPower(results.get(i).choose_utl) + ",value=" + results.get(i).getJobs(results.get(i).choose_utl) + ",EE=" + results.get(i).getEE(results.get(i).choose_utl) + ",chooseNum" + results.get(i).choose_utl);
            jobs = jobs + results.get(i).getJobs(results.get(i).choose_utl);
            power = power + results.get(i).getPower(results.get(i).choose_utl);
        }
        ServerUtl.UpdateDB(results,start_time,threshold,power,jobs,serverUtl.serverTypeNum,runtime);
        System.out.println("总jobs数" + jobs);
        System.out.println("总Power数" + power);
        System.out.println("服务器总台数" + results.size());
    }
}
