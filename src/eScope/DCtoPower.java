package eScope;

import java.util.ArrayList;

public class DCtoPower {
    /**
     * 指定服务器
     */
    private Server[] servers;

    /**
     * 总吞吐量
     */
    private double totalThroughput;

    /**
     *将服务器运行在100%下所需要的总功耗
     */
    private int totalPower;

    /**
     * 给定服务器类型数量
     */
    private int n;

    /**
     * 前 n 个服务器，总吞吐量为的最优值矩阵
     */
    private double[][] bestValues;

    /**
     * 前 n 个服务器，总吞吐量为totalThroughput的最优值
     */
    private double bestValue;

    /**
     * 前 n 个服务器，总功耗为 totalPower 的最优解的服务器组成
     */
    private ArrayList<Server> bestSolution = new ArrayList<>();
    /**
     * 在功耗j下，第i个服务器用了多少台*
     * */
    private int[][] utl;
    private int singleBigPower;
    private  int bagNum;
    public DCtoPower(Server[] servers, double totalThroughput, int n,int singleBigPower,int totalPower) {
        this.servers = servers;
        this.totalThroughput = totalThroughput;
        this.n = n;
        this.singleBigPower = singleBigPower;
        this.totalPower = totalPower;
        if(totalPower > singleBigPower){
            if(totalPower % singleBigPower > 0)
                this.bagNum = (int)(totalPower/singleBigPower)+1;
            else
                this.bagNum = (int)(totalPower/singleBigPower);
        }else this.bagNum = 1;
        System.out.println("构造完成");
        System.out.println(n);
    }
    public void solve(){
        System.out.println("开始求解问题");
        System.out.println("需要达到的吞吐量"+totalThroughput);
        System.out.println("将所有服务器运行在*利用率需要"+totalPower);
        System.out.println("SinglebagPower+"+singleBigPower);
        System.out.println("分解成背包"+bagNum);
        double remainPower = totalPower - singleBigPower * (bagNum - 1);
        double tmpThroughput = totalThroughput;
        double nowThroughput = 0;
        int flag = 0;//此背包是否已经达到吞吐量，1是，0不是
        for(int count = 0;count < bagNum;count++){
            if(count < bagNum - 1){
                bestValues = new double[n+1][singleBigPower+1];
                utl = new int[n+1][singleBigPower+1];
            }else if(bagNum == 1){
                bestValues = new double[n+1][singleBigPower+1];
                utl = new int[n+1][singleBigPower+1];
            }
            else{
                singleBigPower = (int)remainPower;
                bestValues = new double[n+1][singleBigPower+1];
                utl=new int[n+1][singleBigPower+1];
            }
            for (int j = 1; j <= singleBigPower; j++) {
                for (int i = 1; i <= n; i++) {
                    if (servers[i - 1].failureFlag == 0 || servers[i-1].selected) {
                        bestValues[i][j] = bestValues[i - 1][j];
                        continue;
                    }
                    for (int serverUtl = 0; serverUtl < 11; serverUtl++) { //遍历每一个利用率
                        if (j < servers[i - 1].getPower(serverUtl)) { //如果这个服务器加不进去，就取上一个服务器的最优解
                            bestValues[i][j] = bestValues[i - 1][j];
                        } else{
                            double ipower = servers[i - 1].getPower(serverUtl);
                            double ijobs = servers[i - 1].getJobs(serverUtl);
                            if (bestValues[i - 1][j] >= ijobs + bestValues[i - 1][(int) (j - ipower)] && bestValues[i - 1][j] > bestValues[i][j]) {
                                bestValues[i][j] = bestValues[i - 1][j];
                            } else if (bestValues[i - 1][j] < ijobs + bestValues[i - 1][(int) (j - ipower)] && ijobs + bestValues[i - 1][(int) (j - ipower)] > bestValues[i][j]) {
                                bestValues[i][j] = ijobs + bestValues[i - 1][(int) (j - ipower)];
                                utl[i - 1][j] = serverUtl;
                            }
                        }
                    }
                }
            }
            bestValue = bestValues[n][singleBigPower]; //当前背包的吞吐量最大值
            while(bestValue > tmpThroughput){ //当前的背包值已经超过了最大的吞吐量
                singleBigPower --;
                bestValue = bestValues[n][singleBigPower];
                flag = 1;
            }
            double tempPower;
            if(flag == 1) //如果已经超过了，找刚刚好超过的那个点
                tempPower = singleBigPower + 1;
            else//如果没有超过，按最大值计算
                tempPower = singleBigPower;
            System.out.println("singlebigpower:"+tempPower);
            System.out.println("totoalJobs"+bestValues[n][singleBigPower]);
            for (int i = n; i >= 1; i--) { //找选择路径
                if (bestValues[i][(int) tempPower] > bestValues[i - 1][(int) tempPower]) {
                    bestSolution.add(servers[i - 1]);  // servers[i-1] 表示第 i 个服务器
                    servers[i - 1].selected = true;
                    servers[i - 1].choose_utl = utl[i - 1][(int) tempPower];
                    tempPower = tempPower - servers[i - 1].getPower(utl[i - 1][(int) tempPower]);
                }
                if (tempPower == 0) {
                    break;
                }
            }
            System.out.println("tmpThroughoyt:"+tmpThroughput);
            tmpThroughput =  tmpThroughput  - bestValue;
            if(tmpThroughput < 0 || flag == 1)
                break;
        }

    }
    public double getBestValue() {
        return bestValue;
    }

    public ArrayList<Server> getBestSolution(){
        return bestSolution;
    }



}
