package eScope;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class ComputeUtl { //此类用于计算利用率的各种平均数
    public static String medianutl(ArrayList<Server> servers){ //给定结果Servers，计算利用率的中位数
        DecimalFormat df= new DecimalFormat("######0.00");
        int lenth = servers.size();
        System.out.println("结果List的长度是："+lenth);
        if(lenth % 2 !=0){
            return servers.get(lenth/2).getutl(servers.get(lenth/2).choose_utl);
        }else{
            int temputl = Integer.parseInt(servers.get(lenth/2).getutl(servers.get(lenth/2).choose_utl).replaceAll("%",""));
            int temput2 = Integer.parseInt(servers.get(lenth/2-1).getutl(servers.get(lenth/2-1).choose_utl).replaceAll("%",""));
            double utl = (temputl + temput2)/2;
            return df.format(utl)+"%";
        }
    }
    public static String avgutl(ArrayList<Server> servers) { //给定结果Servers，计算利用率的平均数
        DecimalFormat df= new DecimalFormat("######0.00");
        int lenth = servers.size();
        double avgutl = 0;
        for (Server server : servers) {
            avgutl += Double.parseDouble(server.getutl(server.choose_utl).replaceAll("%", ""));
        }
        avgutl = avgutl / lenth;
        return df.format(avgutl)+"%";
    }
}
