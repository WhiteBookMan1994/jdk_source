package util.concurrent.completablefuture;

import java.util.concurrent.CompletableFuture;

/**
 * @author dingchenchen
 * @since 2020/10/22
 */
public class CompletableFutureTest1 {
    public static void main(String[] args) {
        CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(CompletableFutureTest1::getMoney);
        cf.thenApply(CompletableFutureTest1::getMoney1).thenAccept(money-> System.out.println(money));
        CompletableFuture<Integer> cf1 = new CompletableFuture<>();
        cf1.supplyAsync(CompletableFutureTest1::getMoney).thenApply(CompletableFutureTest1::getMoney1);
        cf1.thenAccept(money-> System.out.println(money));
    }

    static Integer getMoney(){
        return 100 * 10000;
    }

    static Integer getMoney1(Integer i){
        return i / 10000;
    }
}
