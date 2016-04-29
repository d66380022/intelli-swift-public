package com.fr.bi.cluster.zookeeper.lock;import com.fr.bi.cluster.ClusterAdapter;import com.fr.bi.cluster.lock.DelaySemaphore;import com.fr.bi.cluster.lock.DistributedLock;import com.fr.bi.cluster.wrapper.ZooKeeperWrapper;import com.fr.bi.cluster.zookeeper.ZooKeeperConstant;import com.fr.bi.cluster.zookeeper.ZooKeeperException;import com.fr.bi.stable.utils.code.BILogger;import org.apache.zookeeper.KeeperException;/** * 基于ZooKeeper的分布式锁，lockName相同即为同一个锁 * Created by Hiram on 2015/2/26. */public class ZooKeeperLock implements DistributedLock {    private final static String LOCK_DIR = "/lock/";    protected ZooKeeperWrapper zooKeeper;    private String lockName;    private LockListener lockListener;    private WriteLock writeLock;    private DelaySemaphore signal = new DelaySemaphore();    private boolean isInit;    public ZooKeeperLock(String lockName) {        this.lockName = lockName;    }    @Override    public void lock() throws ZooKeeperException {        init();        try {            writeLock.lock();            //writeLock 已经绑定了事件，成功获取锁之后会通知signal            signal.await();        } catch (KeeperException e) {            throw new ZooKeeperException(e);        } catch (InterruptedException e) {            throw new ZooKeeperException(e);        }    }    @Override    public void unlock() {        init();        writeLock.unlock();    }    private ZooKeeperWrapper getZooKeeper() {        if (zooKeeper == null) {            zooKeeper = ClusterAdapter.getManager().getZooKeeperManager().getZooKeeper();        }        return zooKeeper;    }    public void setZooKeeper(ZooKeeperWrapper zooKeeper) {        this.zooKeeper = zooKeeper;    }    private void init() {        if (!isInit) {            isInit = true;            lockListener = new ZooKeeperLockListener();            writeLock = new WriteLock(getZooKeeper().getZookeeperHandler().getZooKeeper(), getLockPath(), ZooKeeperConstant.DEFAULT_ACL, lockListener);        }    }    private String getLockPath() {        return LOCK_DIR + this.lockName;    }    private class ZooKeeperLockListener implements LockListener {        @Override        public void lockAcquired() {            try {                signal.signal();            } catch (InterruptedException e) {                BILogger.getLogger().error(e.getMessage(), e);            }        }        @Override        public void lockReleased() {        }    }}