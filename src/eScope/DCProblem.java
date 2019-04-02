package eScope;


import java.util.ArrayList;

public class DCProblem {
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
     * 前 n 个服务器，总功耗为 totalPower 的最优值矩阵
     */
    private double[][] bestValues;

    /**
     * 前 n 个服务器，总功耗为 totalPower 的最优值
     */
    private double bestValue;

    /**
     * 前 n 个服务器，总功耗为 totalPower 的最优解的服务器组成
     */
    private ArrayList<Server> bestSolution = new ArrayList<>();

    private int singleBigPower;
    /*在功耗j下，第i个服务器用了多少台*/
    private int[][] utl;
    private int bagNum = 1;

    public DCProblem(Server[] servers, double totalPower, int n,int singleBigPower) {
        this.servers = servers;
        this.totalPower = totalPower;
        this.n = n;
        this.singleBigPower = singleBigPower;  //分解背包的单位
        if(totalPower > singleBigPower)
            if(totalPower % singleBigPower > 0)
                this.bagNum = (int)(totalPower/singleBigPower) + 1;
            else
                this.bagNum = (int)(totalPower / singleBigPower);
//        if (bestValues == null) {
//            bestValues = new double[n + 1][(int) (totalPower/bagNum + 1)];
//        }
//        utl = new int[n + 1][(int) totalPower + 1]; //记录每种情况下的utl
        System.out.println("构造完成");
        System.out.println(n);
    }
    /**
     * 求解前 n 个服务器、给定总功耗为 totalPower 下的服务器问题
     */
    public void solve() {
        System.out.println("开始求解问题");
        System.out.println("给定总功耗: " + totalPower);
        // 求解最优值
        System.out.println("分解成背包数:"+bagNum);
        double remainPower = totalPower - singleBigPower * (bagNum - 1);
         for (int count = 0; count < bagNum; count++) {
            bestValues = new double[n + 1][singleBigPower + 1];
            System.out.println("bestValues.lenth:"+bestValues.length);
            utl = new int[n + 1][singleBigPower + 1];
            //bestValue = 0;
            if(count < bagNum - 1) { //之前的背包都是整数
                bestValues = new double[n + 1][singleBigPower + 1];
                utl = new int[n + 1][singleBigPower + 1];
            }
            else{
                singleBigPower = (int)remainPower;
                bestValues = new double[n + 1][singleBigPower + 1];
                utl = new int[n + 1][singleBigPower + 1];
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
                        } else {
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
            //接下来求解选择了哪些服务器
            bestValue = bestValues[n][singleBigPower];
            System.out.println("求解问题结束");
            System.out.print("bestValue="+bestValue);
            if (bestSolution == null) {
                bestSolution = new ArrayList<Server>();
            }
            double tempPower = singleBigPower;
            for (int i = n; i >= 1; i--) {
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
            System.out.println("bestSolution.size="+bestSolution.size());
        }
    }
    /**
     * 获得前  n 个服务器， 总功耗为 totalPower 的服务器问题的最优解值
     * 调用条件： 必须先调用 solve 方法
     */
    public double getBestValue() {
        return bestValue;
    }

    //
//    /**
//     * 获得前  n 个服务器， 总功耗为 totalPower 的服务器问题的最优解值矩阵
//     * 调用条件： 必须先调用 solve 方法
//     *
//     */
    public double[][] getBestValues(){
        return bestValues;
    }

    //
//    /**
//     * 获得前  n 个服务器， 总功耗为 totalPower 的服务器问题的最优解值矩阵
//     * 调用条件： 必须先调用 solve 方法
//     *
//     */
    public ArrayList<Server> getBestSolution(){
        return bestSolution;
    }
}


