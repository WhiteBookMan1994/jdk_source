

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tuzhenxian
 * @date 19-12-30
 */
@StateConfig(descField = "desc")
public enum OrderStatusEnum {
    CLOSED(-10, "订单关闭"),
    INIT(10, "订单生成"){
        @Override
        public boolean cancelable(Integer openTicketNode) {
            return true;
        }
    },
    PRE_CHECK(15,"待平台预审"){
        @Override
        public boolean cancelable(Integer openTicketNode) {
            return true;
        }
    },
    WAITTING_TO_CHECK(20, "待机构审批"){
        @Override
        public boolean cancelable(Integer openTicketNode) {
            return true;
        }
    },

    WAITTING_TO_LOAN(70, "待机构放款"){
        @Override
        public boolean cancelable(Integer openTicketNode) {
            return true;
        }
    },

    LOANED(80, "已放款");

    private Integer code;
    private String desc;

    OrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public boolean cancelable(Integer openTicketNode){
        return false;
    }

}
