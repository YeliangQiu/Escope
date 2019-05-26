package eScope;

import eScope.compara.ComparatorPeakEE;

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
    /**
     * 指定服务器
     */
    private Server[] servers;

    /**
     * 总功耗
     */
    private double totalPower;

    /**
     * 给定服务器类型数量
     */
    private int n;

    /**
     * 前 n 个服务器，总功耗为 totalPower 的最优值
     */
    private double bestValue;

    /**
     * 前 n 个服务器，总功耗为 totalPower 的最优解的服务器组成
     */
    private ArrayList<Server> bestSolution = new ArrayList<>();


    public Greed(Server[] servers,double totalPower) {
        this.servers = servers;
        this.totalPower = totalPower;
    }

    public void solution(){
        Arrays.sort(servers,new ComparatorPeakEE());
        double weight = 0;
        double value = 0;
        for(Server server:servers){
            if(server.getPower(server.getPeakee_utl())+weight <= totalPower) {
                weight += server.getPower(server.getPeakee_utl());
                value += server.getEE(server.getChoose_utl());
                bestSolution.add(server);
            }
        }
        bestValue = value;
        totalPower = weight;
    }

    public ArrayList<Server> getBestSolution() {
        return bestSolution;
    }

    public double getBestValue() {
        return bestValue;
    }
}
