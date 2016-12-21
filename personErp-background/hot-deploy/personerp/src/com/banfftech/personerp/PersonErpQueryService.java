package com.banfftech.personerp;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.batik.util.EventDispatcher.Dispatcher;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class PersonErpQueryService {
	public static final String module = PersonErpQueryService.class.getName();
	
	 /**
	 * 查询用户信息
	 * @param dctx
	 * @param context
	 * @return Map
	 */
	public static Map<String,Object> findPersonInfo(DispatchContext dctx,Map<String,Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dispatcher.getDelegator();
		String partyId =(String)context.get("partyId");
		
		GenericValue personInfo=null;
		try {
			personInfo = delegator.findByPrimaryKeyCache("PartyAndPerson", UtilMisc.toMap("partyId", partyId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("personInfo", personInfo);
		return result;
	}

}
