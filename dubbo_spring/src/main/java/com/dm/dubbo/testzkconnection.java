package com.dm.dubbo;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author Administrator
 * @version V1.0
 * @ClassName:testzkconnection
 * @Description:TODO
 * @date 2019/12/5
 */
public class testzkconnection {
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        ZooKeeper zk = new ZooKeeper("122.51.157.42", 3000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    countDownLatch.countDown();
                }
                System.out.println("Watch =>" + event.getType());
            }
        });
        countDownLatch.await();

        System.out.println(zk.getState());
        String node = "/app1";
        Stat state = zk.exists(node, false);
        if (state == null) {
            System.out.println("创建节点");
            String createResult = zk.create(node, "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println(createResult);
        }


        byte[] b = zk.getData(node, false, state);
        System.out.println("获取data值 =》" + new String(b));

        state = zk.setData(node, "1".getBytes(), state.getVersion());
        System.out.println("after update, version changed to =>" + state.getVersion());
        zk.delete(node,state.getVersion());
        System.out.println("delete complete");

        zk.close();
    }
}