package com.fr.bi.cal.generate.index;

import com.fr.bi.base.BIUser;
import com.fr.bi.cal.loader.CubeGeneratingTableIndexLoader;
import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.cal.stable.index.*;
import com.fr.bi.conf.log.BIRecord;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.stable.index.CubeGenerator;
import com.fr.bi.stable.log.CubeGenerateStatusProvider;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.general.DateUtils;

import java.util.Arrays;

/**
 * Created by GUY on 2015/3/13.
 */
public class IndexGenerator implements CubeGenerator, java.util.concurrent.Callable<Object>, CubeGenerateStatusProvider {

    protected ITableSource source;

    protected TableCubeFile cube;

    protected int version;
    protected BIUser biUser;
    private transient int percent;

    public IndexGenerator(ITableSource source, long userId, int version) {
        biUser = new BIUser(userId);
        this.source = source;
        createTableCube();
        this.version = version;
    }

    protected void createTableCube() {
        cube = new TableCubeFile(BIPathUtils.createTableTempPath(source.fetchObjectCore().getID().getIdentityValue(), biUser.getUserId()));
    }

    @Override
    public Object call() throws Exception {
        try {
            BILogger.getLogger().info("now start" + source.toString() + "loading data");
            long start = System.currentTimeMillis();
            BIConfigureManagerCenter.getLogManager().infoTableReading(new BITable(source.fetchObjectCore().getID().getIdentityValue()), 0, biUser.getUserId());
            generateSimpleCube();
            BIConfigureManagerCenter.getLogManager().infoTableReading(new BITable(source.fetchObjectCore().getID().getIdentityValue()),
                    System.currentTimeMillis() - start, biUser.getUserId());
            BILogger.getLogger().info("loading table data:" + source.toString() + "cost:" + DateUtils.timeCostFrom(start));
        } catch (Throwable e) {
            wrong(e);
            BILogger.getLogger().info("generate:" + source.toString() + " failed");
        }
        return null;
    }

    @Override
    public int getPercent() {
        return percent;
    }

    @Override
    public void setPercent(int percent) {
        this.percent = percent;
    }

    protected void release() {
        setPercent(0);
    }

    protected void wrong(Throwable e) {
        BILogger.getLogger().error(e.getMessage(), e);
        BIConfigureManagerCenter.getLogManager().errorTable(new BITable(source.fetchObjectCore().getID().getIdentityValue()), e.getClass().getName() + ":" + e.getMessage() + ":" + Arrays.toString(e.getStackTrace()), biUser.getUserId());
        release();
        cube.delete();
    }

    private void generateSimpleCube() {
        try {
            BIRecord log = BIConfigureManagerCenter.getLogManager().getBILog(biUser.getUserId());
            AbstractIndexGenerator generator = new BeforeIndexGenerator(cube, source,
                    BIConfigureManagerCenter.getCubeManager().getGeneratingObject(biUser.getUserId()).getSources(), log);
            BIConfigureManagerCenter.getLogManager().infoTable(new BITable(source.fetchObjectCore().getID().getIdentityValue()), 0, biUser.getUserId());
            generator.generateCube();
            generator = createSimpleIndexGenerator();
            generator.generateCube();
            new FinishIndexGenerator(cube, log).generateCube();
        } catch (Throwable e) {
            wrong(e);
            BILogger.getLogger().info("generate:" + source.toString() + " failed");
        } finally {
            this.release();
        }
    }

    protected AbstractIndexGenerator createSimpleIndexGenerator() {
            return new SimpleIndexGenerator(cube, source, BIConfigureManagerCenter.getCubeManager().getGeneratingObject(biUser.getUserId()).getSources(), version, BIConfigureManagerCenter.getLogManager().getBILog(biUser.getUserId()), CubeGeneratingTableIndexLoader.getInstance(biUser.getUserId()));
    }

    public void generateIndex() {
        try {
            BILogger.getLogger().info("now start" + source.toString() + "indexing");
            BIRecord log = BIConfigureManagerCenter.getLogManager().getBILog(biUser.getUserId());
            long start = System.currentTimeMillis();
            CubeGenerator generator = new GroupIndexGenerator(cube, source, CubeGeneratingTableIndexLoader.getInstance(biUser.getUserId()), log);
            generator.generateCube();
            BILogger.getLogger().info("indexing completed, cost:" + DateUtils.timeCostFrom(start));
        } catch (Throwable e) {
            wrong(e);
            BILogger.getLogger().info("generate:" + source.toString() + " failed");
        } finally {
            this.release();
        }
    }

    @Override
    public void generateCube() {
        long start = System.currentTimeMillis();
        BILogger.getLogger().info("now generating:" + source.toString() + " Cube:");
        setPercent(0);
        cube.delete();
        generateSimpleCube();
        setPercent(50);
        generateIndex();
        setPercent(100);
        BILogger.getLogger().info("generating:" + source.toString() + "cost：" + DateUtils.timeCostFrom(start));
    }

    @Override
    public String getDetail() {
        return null;
    }

    @Override
    public void setDetail(String s) {

    }
}