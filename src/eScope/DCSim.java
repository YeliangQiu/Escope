package eScope;



import eScope.analysis.DataAnalysis;
import eScope.compara.ComparatorPeakEE;

import java.io.InputStream;
import java.util.*;
import java.util.Date;
import java.sql.*;
import java.text.SimpleDateFormat;

/**
 * @AUTHOR : QYL
 * @DATE : 2018/11/09
 * @TIME : 20:30
 * @VERSION : 1.2
 * @DESC : 增加了限定吞吐量求最小power的方法
 */

public class DCSim {
    public static void main(String[] args) {
        String sql = "select * from benchmark_results_summary where id in (select id from system_overview where (Hardware_Availability like '%2017' or Hardware_Availability like '%2018' or Hardware_Availability like '%2019') AND compliment='1')";
        //String sql = "SELECT * FROM benchmark_results_summary a, (SELECT id, max(replace(performance_to_Power_Ratio, ',', '') + 0) AS maxptpr from benchmark_results_summary group by id) b WHERE a.id = b.id AND (replace(a.performance_to_Power_Ratio, ',', '') + 0) = b.maxptpr and a.id in (SELECT id FROM pastehtml.system_overview where (Hardware_Availability like '%2014' or Hardware_Availability like '%2015' or Hardware_Availability like '%2016' or Hardware_Availability like '%2017' or Hardware_Availability like '%2018') and compliment='1')";
        //String sql = "select * from benchmark_results_summary where id in (select id from system_overview where (Hardware_Availability like '%2014' or Hardware_Availability like '%2015' or Hardware_Availability like '%2016' or Hardware_Availability like '%2017' or Hardware_Availability like '%2018') AND compliment='1' and Target_Load='100%');";
        //String sql = "select * from benchmark_results_summary where id in (select id from system_overview where (Hardware_Availability like '%2014' or Hardware_Availability like '%2015' or Hardware_Availability like '%2016' or Hardware_Availability like '%2017' or Hardware_Availability like '%2018') AND compliment='1' and (Target_Load='30%' or Target_load='20%'  or Target_load='10%' ));";
        //String sql = "select * from benchmark_results_summary where id in (select id from system_overview where (Hardware_Availability like '%2018') AND compliment='1');";
//        String sql = "select * from benchmark_results_summary where id in (select id from system_overview where (Hardware_Availability like '%2013') AND compliment='1')\n" +
//                "union all\n" +
//                "select * from benchmark_results_summary where id='2018082800855'\n" +
//                "union all\n" +
//                "select * from benchmark_results_summary where id='2017121900806'\n" +
//                "union all\n" +
//                "select * from benchmark_results_summary where id='2017121900805'\n" +
//                "union all\n" +
//                "select * from benchmark_results_summary where id='2018082800853'\n" +
//                "union all\n" +
//                "select * from benchmark_results_summary where id='2018062900822'";

//        ServerUtl serverUtl = new ServerUtl(sql);
//        serverUtl.setServerMaxEE();
//        double totalthroughput = serverUtl.getServerTotalThroughput();
//        Solution solution = new Solution();

//        LoadGenerate loadGenerate = new LoadGenerate();
//        double[] benchUtilization = loadGenerate.setLoad();
//        serverUtl = null;
//        for(int i=0;i<benchUtilization.length;i++) {
//            serverUtl = new ServerUtl(sql);
//            System.out.println("现在已经运行到"+ i +"轮");
//            System.out.println("本次需要达到的吞吐量"+ Math.floor(totalthroughput * benchUtilization[i]));
//            solution.throughtoutToPower(serverUtl,Math.floor(totalthroughput * benchUtilization[i]));
//        }
        //背包算法
//        solution.powerToThroughtout(100000,sql);

        //模拟退火方法
//        System.out.println("运行全部服务器在100%所需要的总功耗" + serverUtl.getServersTotalPower());
//        SAProblem saProblem = new SAProblem(serverUtl.servers,100000);
//        saProblem.init();
//        saProblem.solve();

        //贪心算法
//        Greed greed = new Greed(serverUtl.getservers(),100000);
//        List<Server> serverList = greed.getServerList();
//        ComparatorPeakEE comparatorPeakEE = new ComparatorPeakEE();
//        Collections.sort(serverList, comparatorPeakEE);
//        for(Server server:serverList)
//            System.out.println(server.getEE(server.getPeakee_utl()));
//        greed.solve();

//        DataAnalysis dataAnalysis = new DataAnalysis("2019041800956");
//        dataAnalysis.print();

        //solution.throughtoutToPower(4e9,sql);
        //solution.powerToThroughtout(Integer.parseInt(args[0]),Integer.parseInt(args[1]),sql);
        //solution.powerToThroughtout();

        Solution solution = new Solution();
        for(int i=100000;i<=1000000;i+=10000){
            solution.powerToThroughtout(i,sql);
        }
    }
}



