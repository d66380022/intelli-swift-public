package com.fr.swift.service.handler.history;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.event.base.AbstractHistoryRpcEvent;
import com.fr.swift.event.base.EventResult;
import com.fr.swift.event.history.SegmentLoadRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.HistoryService;
import com.fr.swift.service.handler.base.AbstractHandler;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/8
 */
@SwiftBean
public class SwiftHistoryEventHandler extends AbstractHandler<AbstractHistoryRpcEvent> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftHistoryEventHandler.class);
    private HistoryDataSyncManager historyDataSyncManager = SwiftContext.get().getBean(HistoryDataSyncManager.class);
    private SwiftClusterSegmentService clusterSegmentService = SwiftContext.get().getBean(SwiftClusterSegmentService.class);

    @Override
    public <S extends Serializable> S handle(AbstractHistoryRpcEvent event) {
        try {
            ProxyFactory factory = ProxySelector.getInstance().getFactory();
            switch (event.subEvent()) {
                case LOAD_SEGMENT:
                case TRANS_COLLATE_LOAD:
                    return historyDataSyncManager.handle((SegmentLoadRpcEvent) event);
//                case COMMON_LOAD:
//                    String source = event.getSourceClusterId();
//                    handleCommonLoad(event, 0);
//                    return (S) EventResult.success(source);
//                case MODIFY_LOAD:
//                    String sourceId = event.getSourceClusterId();
//                    if (handleCommonLoad(event, 1)) {
//                        return (S) EventResult.success(sourceId);
//                    } else {
//                        return (S) EventResult.failed(sourceId, "load failed");
//                    }
                case COMMON_LOAD:
                case MODIFY_LOAD:
                    //需要load的seg
                    Pair<SourceKey, Map<SegmentKey, List<String>>> pair = (Pair<SourceKey, Map<SegmentKey, List<String>>>) event.getContent();
                    HistoryService service = factory.getProxy(HistoryService.class);
                    try {
                        service.commonLoad(pair.getKey(), pair.getValue());
                        return (S) EventResult.success(event.getSourceClusterId());
                    } catch (Exception e) {
                        return (S) EventResult.failed(event.getSourceClusterId(), "load failed");
                    }
                case CHECK_LOAD:
                    checkLoad(event.getSourceClusterId());
                    break;
                default:
                    return null;
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }


    private void checkLoad(final String clusterId) {
        // TODO 新节点加入处理
    }

//    private boolean handleCommonLoad(AbstractHistoryRpcEvent event, int wait) throws Exception {
//        Map<String, ClusterEntity> services = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.HISTORY);
//        if (null == services || services.isEmpty()) {
//            throw new RuntimeException("Cannot find history service");
//        }
//        Pair<String, Map<String, List<String>>> pair = (Pair<String, Map<String, List<String>>>) event.getContent();
//        Iterator<Map.Entry<String, ClusterEntity>> iterator = services.entrySet().iterator();
//        Map<String, List<String>> uris = pair.getValue();
//        final CountDownLatch latch = wait > 0 ? new CountDownLatch(wait) : null;
//        final AtomicBoolean success = new AtomicBoolean(true);
//        while (iterator.hasNext()) {
//            Map.Entry<String, ClusterEntity> entry = iterator.next();
//            Map<String, List<SegmentKey>> map = clusterSegmentService.getOwnSegments(entry.getKey());
//            List<SegmentKey> list = map.get(pair.getKey());
//            Set<String> needLoad = new HashSet<String>();
//            if (!list.isEmpty()) {
//                for (SegmentKey segmentKey : list) {
//                    String segKey = segmentKey.toString();
//                    if (uris.containsKey(segKey)) {
//                        needLoad.addAll(uris.get(segKey));
//                    }
//                }
//            }
//            if (!needLoad.isEmpty()) {
//                Map<String, Set<String>> load = new HashMap<String, Set<String>>();
//                load.put(pair.getKey(), needLoad);
//                runAsyncRpc(entry.getKey(), entry.getValue().getServiceClass(), "load", load, false)
//                        .addCallback(new AsyncRpcCallback() {
//                            @Override
//                            public void success(Object result) {
//                                LOGGER.info("load success");
//                                success.set(true);
//                                if (null != latch) {
//                                    latch.countDown();
//                                }
//                            }
//
//                            @Override
//                            public void fail(Exception e) {
//                                LOGGER.error("load error! ", e);
//                                if (null != latch) {
//                                    latch.countDown();
//                                }
//                            }
//                        });
//            }
//        }
//        if (null != latch) {
//            latch.await(30, TimeUnit.SECONDS);
//        }
//        return success.get();
//    }
}
