package basic.grammar;

/**
 * 数组作为参数传递给方法，会修改原数组
 *
 * @author dingchenchen
 * @since 2021/3/5
 */
public class ArrayAsParamTest {


    public static void main(String[] args) {
        int []nums = {-2,1,-3,4,-1,2,1,-5,4};
        int result = maxSubArray(nums);
        for(int i = 0; i<nums.length;i++){
            System.out.print(nums[i] + ",");
        }
        System.out.println();
        //nums[]数组被改变了
    }

    /*
    输入: nums = [-2,1,-3,4,-1,2,1,-5,4]
    输出: 6
    解释: 连续子数组 [4,-1,2,1] 的和最大，为 6。
    */

    public static int maxSubArray(int[] nums) {
        int res = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (nums[i-1] < 0) {
                nums[i] = nums[i];
            } else {
                nums[i] += nums[i-1];
            }
            System.out.println(nums[i]);
            res = Math.max(nums[i], res);
        }

        return res;
    }

    /**
     * 不改变原数组的解决方法
     */
    public static int maxSubArrayNoChangeArray(int[] nums) {
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
