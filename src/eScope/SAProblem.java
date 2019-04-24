package eScope;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : qiuyeliang
 * create at:  2019/4/15  21:04
 * @description: 模拟退火算法搜索较优组合解
 */
public class SAProblem {

    Server[] servers; //有这些服务器可供选择
    int[] bestChoiceWay;
    int[] nowChoiceWay;
    private double totalPower; //总功耗，也是背包的容量
    private int serverNum;//总服务器数量
    private double bestValue;//最优值
    private double bestWeight;
    private double nowValue = -1;
    private double nowWeight = -1;
    private double af; //退火率
    private double T; //温度
    private int balance; //一次迭代的平衡次数
    private int iterations;
    private Map<String,Integer> severNumMap = new HashMap<>();


    public SAProblem(Server[] servers, double totalPower) {
        this.servers = servers;
        this.totalPower = totalPower;
        this.serverNum = servers.length;
        this.bestChoiceWay = new int[serverNum];
        this.nowChoiceWay = new int[serverNum];
    }

    public void init(){ //初始化各参数以及产生随机解
        this.af = 0.95; //退火率
        this.T = 10000; //温度
        this.balance = serverNum * 10; //一次迭代的平衡次数
        this.iterations = 1000; //迭代次数
        for(Server server:servers) //每种类型的服务器有多少台
            severNumMap.put(server.getId(),server.getnum());
        int value = 0;
        int weight = 0;
        for(int i=0;i<servers.length;i++){ //初始化最优解，全部都取EE最高的情况
            Server server = servers[i];

//            if(weight + server.getPower(server.getPeakee_utl()) >= totalPower)
//                break;
//            weight += server.getPower(server.getPeakee_utl());
//            value += server.getEE(server.getPeakee_utl());
//            server.choose_utl = server.getPeakee_utl();
//            nowChoiceWay[i] = 1;

            if(weight + server.getPower(0) >= totalPower) //此台服务器是否能进入背包
                break;
            weight += server.getPower(0);
            value += server.getEE(0);
            server.choose_utl = 0;
            nowChoiceWay[i] = 1;
        }
        bestValue = value;
        System.arraycopy(nowChoiceWay,0,bestChoiceWay,0,nowChoiceWay.length); //拷贝数组
    }

    //计算当前背包中物品的总价值
    private double calcValue(){
        double valueSum = 0;
        double weightSum = 0;
        for(int i=0;i<servers.length;i++){
            if(nowChoiceWay[i] == 0)
                continue;
            weightSum += servers[i].getPower(servers[i].getChoose_utl());
            valueSum += servers[i].getJobs(servers[i].getChoose_utl());
        }
        nowWeight = weightSum;
        return valueSum;
    }

    //将物品拿出
    private void get(){
        while(true){
            int ob = (int)(Math.random() * serverNum);
            if(nowChoiceWay[ob] == 1)
                nowChoiceWay[ob] = 0;
                break;
        }
    }

    //将物品放入
    private void put(){
        while(true){
            int ob = (int)(Math.random() * serverNum);
            if(nowChoiceWay[ob] == 0){
                nowChoiceWay[ob] = 1;
               // servers[ob].choose_utl = (int)(Math.random() * 4);
                servers[ob].choose_utl = servers[ob].getPeakee_utl();
                break;
            }
        }
    }

    //一次迭代
    private void oneSolve(){
        int[] tmpWay = new int[servers.length];
        nowValue = 0;
        for(int i=0;i<balance;i++){ //要达到能平衡的次数
            nowValue = calcValue(); //计算当前背包的最优值，也就是操作前的。
            System.arraycopy(nowChoiceWay,0,tmpWay,0,nowChoiceWay.length);
            int ob = (int)(Math.random()*serverNum); //随机拿一个
            if(nowChoiceWay[ob] == 1){ //如果在里面，将这个物品取出并且放一个其他的
                put();
                nowChoiceWay[ob] = 0;
            }else{ //如果次物品没被选中，直接放入，或者拿一个出来再放
                if(Math.random() < 0.5){ //如果不用拿出来就能放入，就放进去
                    nowChoiceWay[ob] = 1;
                    //servers[ob].choose_utl = (int)(Math.random() * 4);
                    servers[ob].choose_utl = servers[ob].getPeakee_utl();
                }
                else { //如果不能直接放入，就拿出一个再放
                    get();
                    nowChoiceWay[ob] = 1;
//                    servers[ob].choose_utl = (int) (Math.random() * 4);
                    servers[ob].choose_utl = servers[ob].getPeakee_utl();
                }
            }
            double tmpValue = calcValue(); //操作后的值
            System.out.println("tmpValue" + tmpValue);
            System.out.println("nowValue" + nowValue);
            System.out.println("nowWeight" + nowWeight);
            if(nowWeight > totalPower) { //操作后的重量，如果重量超过了上限
                System.arraycopy(bestChoiceWay,0,nowChoiceWay,0,nowChoiceWay.length); //把刚刚的变化都复制回去
                continue;
            }
            if(tmpValue > bestValue){ //如果往好的方向发展
                bestValue = tmpValue;
                bestWeight = nowWeight;
                System.arraycopy(nowChoiceWay,0,bestChoiceWay,0,nowChoiceWay.length);
            }
            if(tmpValue > nowValue) //如果已操作完的比未操作的好
                System.arraycopy(nowChoiceWay,0,bestChoiceWay,0,nowChoiceWay.length);
            else{//已一定概率接收差解
                double g=1.0*(tmpValue-nowValue)/T;
                if(Math.random() < Math.exp(g))
                    System.arraycopy(nowChoiceWay,0,bestChoiceWay,0,nowChoiceWay.length);
            }
        }
    }

    public void solve(){
        for(int i=0;i<iterations;i++){
            oneSolve();
            T = T * af;
            System.out.println("现在是第"+i+"次迭代"+"最优值是"+bestValue+"总功耗是"+ bestWeight);
        }
        double value = 0;
        double power = 0;
        double num = 0;
        for(int i=0;i<servers.length;i++){
            if(bestChoiceWay[i] == 1){
                num ++;
                value += servers[i].getJobs(servers[i].choose_utl);
                power += servers[i].getPower(servers[i].choose_utl);
                System.out.println("选择的利用率" + servers[i].choose_utl);
            }
        }
        System.out.println("服务器的总数为:" + servers.length);
        System.out.println("总限制功耗为:" + totalPower);
        System.out.println("选择的服务器总数为" + num);
        System.out.println("用服务器计算的最优值为" +value);
        System.out.println("用服务器计算的大功耗为" +power);
        System.out.println("当前温度为" + T);
   }
}
