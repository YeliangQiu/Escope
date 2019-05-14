package eScope.compara;

import eScope.Server;

import java.util.Comparator;

/**
 * @author : qiuyeliang
 * create at:  2019/4/25  11:12
 * @description: 比较器
 */
public class ComparatorPeakEE implements Comparator<Server> {
    @Override
    public int compare(Server o1,Server o2){
        if(o1.getEE(o1.getPeakee_utl()) >= o2.getEE(o2.getPeakee_utl()))
            return -1;
        else
            return 1;
    }
}
