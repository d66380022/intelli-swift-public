package com.fr.bi;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.common.factory.BIFactoryKeyDuplicateException;
import com.fr.bi.stable.utils.code.BILogger;
/**
* This code is generated by tool,Please don't edit it unless you sure 
* what you are doing very much.
**/
public class FbiConfFactory{
public static void registerModuleBeans(){
	try{
		IFactoryService xmlFactory =((IFactoryService) BIMateFactory.getInstance().getObject( IFactoryService.CONF_XML , new Object[]{}));
		try{
			xmlFactory.registerClass("com.fr.bi.conf.base.datasource.BIDataSource",com.fr.bi.conf.base.datasource.BIXMLDataSource.class);}
		catch(BIFactoryKeyDuplicateException ignore){
			 BILogger.getLogger().error(ignore.getMessage(),ignore);	
			}
		try{
			xmlFactory.registerClass("com.fr.bi.conf.base.pack.BIPackageConfigManager",com.fr.bi.conf.base.pack.BIPackageConfigManager.class);}
		catch(BIFactoryKeyDuplicateException ignore){
			 BILogger.getLogger().error(ignore.getMessage(),ignore);	
			}
		try{
			xmlFactory.registerClass("com.fr.bi.conf.base.pack.BIPackagesManagerService",com.fr.bi.conf.base.pack.BIPackageContainer.class);}
		catch(BIFactoryKeyDuplicateException ignore){
			 BILogger.getLogger().error(ignore.getMessage(),ignore);	
			}
		try{
			xmlFactory.registerClass("com.fr.bi.conf.base.pack.BIUserPackageConfigurationManager",com.fr.bi.conf.base.pack.BIUserPackageConfigurationManager.class);}
		catch(BIFactoryKeyDuplicateException ignore){
			 BILogger.getLogger().error(ignore.getMessage(),ignore);	
			}
		try{
			xmlFactory.registerClass("com.fr.bi.conf.base.pack.group.BIGroupTagsManagerService",com.fr.bi.conf.base.pack.group.BIGroupTagContainer.class);}
		catch(BIFactoryKeyDuplicateException ignore){
			 BILogger.getLogger().error(ignore.getMessage(),ignore);	
			}
		try{
			xmlFactory.registerClass("com.fr.bi.conf.base.relation.BITableRelationAnalysisService",com.fr.bi.conf.base.relation.BITableRelationAnalyser.class);}
		catch(BIFactoryKeyDuplicateException ignore){
			 BILogger.getLogger().error(ignore.getMessage(),ignore);	
			}
		try{
			xmlFactory.registerClass("com.fr.bi.conf.base.relation.BIUserTableRelationManager",com.fr.bi.conf.base.relation.BIUserTableRelationManager.class);}
		catch(BIFactoryKeyDuplicateException ignore){
			 BILogger.getLogger().error(ignore.getMessage(),ignore);	
			}
		try{
			xmlFactory.registerClass("com.fr.bi.conf.manager.userInfo.manager.LoginUserInfoManager",com.fr.bi.conf.manager.userInfo.manager.LoginUserInfoManager.class);}
		catch(BIFactoryKeyDuplicateException ignore){
			 BILogger.getLogger().error(ignore.getMessage(),ignore);	
			}}
		catch(Exception ignoreE){
		 BILogger.getLogger().error(ignoreE.getMessage(),ignoreE);
		}}
}