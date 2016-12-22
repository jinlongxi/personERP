package com.banfftech.personerp;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;

public class PersonErpQueryService {
	public static final String module = PersonErpQueryService.class.getName();
	
	/**
	 * 查询用户信息
	 * @param dctx
	 * @param context
	 * @return Map
	 */
	public static Map<String, Object> findPersonInfo(DispatchContext dctx, Map<String, Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dispatcher.getDelegator();
		String partyId = (String) context.get("partyId");
		GenericValue personInfo=null;
		try {
			personInfo = delegator.findByPrimaryKeyCache("PartyAndPerson", UtilMisc.toMap("partyId", partyId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<String> personInfoList = FastList.newInstance();
		String firstName= personInfo.get("firstName").toString();
		String lastName= personInfo.get("lastName").toString();
		String gender= personInfo.get("gender").toString();
		personInfoList.add(firstName);
		personInfoList.add(lastName);
		personInfoList.add(gender);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("personInfoList", personInfoList);
		return result;

	}
}
