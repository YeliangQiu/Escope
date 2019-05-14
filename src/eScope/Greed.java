package eScope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author : qiuyeliang
 * create at:  2019/4/25  10:08
 * @description: 贪心算法求解
 */
public class Greed {
    Server[] servers; //有这些服务器可供选择
    double totalPower;
    private List<Server> serverList;


    public Greed(Server[] servers,double totalPower) {
        this.servers = servers;
        this.totalPower = totalPower;
        serverList = Arrays.asList(servers);
    }

    public List<Server> getServerList() {
        return serverList;
    }

    public void solve(){
        double value = 0;
        double weight = 0;
        for(Server server:serverList){
            if(weight + server.getPower(server.getPeakee_utl()) > totalPower)
                break;
            value += server.getJobs(server.getPeakee_utl());
            weight += server.getPower(server.getPeakee_utl());
        }
        System.out.println("weight=" + weight);
        System.out.println("value=" + value);
    }
}
