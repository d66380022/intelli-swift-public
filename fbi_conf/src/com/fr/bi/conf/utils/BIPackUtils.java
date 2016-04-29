package com.fr.bi.conf.utils;

import com.fr.bi.conf.base.pack.data.BIBasicBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIBusinessTable;
import com.fr.bi.conf.engine.CubeBuildStuffManager;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by GUY on 2015/3/24.
 */
public class BIPackUtils {

    public static BIBasicBusinessPackage getBusiPackByName(Set<BIBasicBusinessPackage> packs, String name) {
        for (BIBasicBusinessPackage pack : packs) {
            if (ComparatorUtils.equals(pack.getName().getValue(), name)) {
                return pack;
            }
        }
        return null;
    }

    public static Set<Table> getAllBusiTableKeys(Set<BIBusinessPackage> packs) {
        Set<Table> keys = new HashSet<Table>();
        Iterator<BIBusinessPackage> itPacks = packs.iterator();

        while (itPacks.hasNext()) {
            Set<BIBusinessTable> busiTable = itPacks.next().getBusinessTables();

            Iterator<BIBusinessTable> itTable = busiTable.iterator();
            while (itTable.hasNext()) {
                BIBusinessTable table = itTable.next();
                try {
                    keys.add(table);
                } catch (Exception e) {
                    BILogger.getLogger().error(e.getMessage(), e);
                }
            }
        }
        return keys;
    }

    public static boolean isNoPackageChange(long userId) {
        return getPackageChangeCounts(userId) == 0;
    }

    public static int getPackageChangeCounts(long userId) {
        int count = 0;
        if (BIConfigureManagerCenter.getPackageManager().isPackageDataChanged(userId)) {
            count++;
        }
        if (count > 0) {
            return count;
        }
        count += BIConfigureManagerCenter.getTableRelationManager().isChanged(userId) ? 1 : 0;
        if (count > 0) {
            return count;
        }
        count += BIConfigureManagerCenter.getUserLoginInformationManager().getUserInfoManager(userId).compareLoginUserInfo();
        return count;
    }

    public static boolean isNoGeneratingChange(long userId) {
        return getGeneratingChangeCounts(userId) == 0;
    }


    public static int getGeneratingChangeCounts(long userId) {
        int count = 0;
        CubeBuildStuffManager object = BIConfigureManagerCenter.getCubeManager().getGeneratingObject(userId);
        if (object == null) {
            return count;
        }
//        BIPackageSet set = new BIPackageSet(userId);
//        set.setAllPackages(object.getPacks());
//        set.setAllGroups(object.getUsedGroupMap());
        if (BIConfigureManagerCenter.getPackageManager().isPackageDataChanged(userId)) {
            return 1;
        }

        count += BIConfigureManagerCenter.getTableRelationManager().isChanged(userId) ? 1 : 0;
        if (count > 0) {
            return count;
        }
        count += BIConfigureManagerCenter.getUserLoginInformationManager().getUserInfoManager(userId).compareLoginUserInfo(object.getUserInfo());
        return count;
    }


}