package com.fr.bi;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.stable.utils.code.BILogger;
/**
* This code is generated by tool,Please don't edit it unless you sure 
* what you are doing very much.
**/
public class FbiWebConfFactory{
public static void registerModuleBeans(){
	try{
		IFactoryService xmlFactory =((IFactoryService) BIMateFactory.getInstance().getObject( IFactoryService.CONF_XML , new Object[]{}));}
		catch(Exception ignoreE){
		 BILogger.getLogger().error(ignoreE.getMessage(),ignoreE);
		}}
}