package com.fr.swift.config.context;

import com.fr.config.BaseDBEnv;
import com.fr.finedb.FineDBProperties;
import com.fr.stable.db.DBContext;
import com.fr.stable.db.option.DBOption;
import com.fr.swift.config.dao.SwiftMetaDataDAO;
import com.fr.swift.config.dao.SwiftSegmentDAO;
import com.fr.swift.config.dao.SwiftServiceInfoDao;
import com.fr.swift.config.dao.impl.SwiftMetaDataDAOImpl;
import com.fr.swift.config.dao.impl.SwiftSegmentDAOImpl;
import com.fr.swift.config.dao.impl.SwiftServiceInfoDaoImpl;
import com.fr.swift.config.entity.SwiftMetaDataEntity;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.swift.log.SwiftLoggers;

/**
 * @author yee
 * @date 2018/5/24
 */
public class SwiftConfigContext {
    private static SwiftConfigContext self;
    private boolean initialized = false;

    private SwiftMetaDataDAO swiftMetaDataDAO;
    private SwiftSegmentDAO swiftSegmentDAO;
    private SwiftServiceInfoDao serviceInfoDao;

    private SwiftConfigContext() {
    }

    public static SwiftConfigContext getInstance() {
        if (null == self) {
            synchronized (SwiftConfigContext.class) {
                if (null == self) {
                    self = new SwiftConfigContext();
                }
            }
        }
        try {
            return self.init();
        } catch (Exception e) {
            SwiftLoggers.getLogger(SwiftConfigContext.class).error(e);
            return self;
        }
    }

    private SwiftConfigContext init() throws Exception {
        synchronized (this) {
            if (initialized) {
                return this;
            }
            DBOption option = FineDBProperties.getInstance().get();
            DBContext dbContext = BaseDBEnv.getDBContext();
            dbContext.addEntityClass(SwiftMetaDataEntity.class);
            dbContext.addEntityClass(SwiftSegmentEntity.class);
            dbContext.addEntityClass(SwiftServiceInfoEntity.class);
            dbContext.init(option);
            swiftMetaDataDAO = new SwiftMetaDataDAOImpl();
            swiftSegmentDAO = new SwiftSegmentDAOImpl();
            serviceInfoDao = new SwiftServiceInfoDaoImpl();
            BaseDBEnv.setDBContext(dbContext);
            this.initialized = true;
        }
        return this;
    }


    public SwiftMetaDataDAO getSwiftMetaDataDAO() {
        return swiftMetaDataDAO;
    }

    public SwiftSegmentDAO getSwiftSegmentDAO() {
        return swiftSegmentDAO;
    }

    public SwiftServiceInfoDao getServiceInfoDao() {
        return serviceInfoDao;
    }
}
