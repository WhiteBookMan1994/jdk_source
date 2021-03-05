import org.junit.Test;

/**
 * @author dingchenchen
 * @since 2021/3/5
 */
public class TempTest {
/*  输入: nums = [-2,1,-3,4,-1,2,1,-5,4]
    输出: 6
    解释: 连续子数组 [4,-1,2,1] 的和最大，为 6。*/

    public int maxSubArray(int[] nums) {
        int res = nums[0],a = nums[0], b= 0;
        for (int i = 1; i < nums.length; i++) {
            b = nums[i];
            if (a < 0) {
                b = nums[i];
            } else {
                b += a;
            }
            a = b;
            res = Math.max(b, res);
        }

        return res;
    }
}
