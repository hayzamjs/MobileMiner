package scala.androidminer.pools.api;

import scala.androidminer.pools.PoolItem;

public final class NodejsPool extends PoolTypeAbstract {

    public NodejsPool(PoolItem poolItem){
        super(poolItem);
    }

    @Override
    public String getBlockHeight() {
        return null;
    }
}
