package com.banfftech.personerp;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
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
		Locale locale = (Locale) context.get("locale");
		String partyId = (String) context.get("partyId");
		//查询联系人基本信息
		GenericValue personInfo=null;
		try {
			personInfo = delegator.findByPrimaryKeyCache("PartyAndPerson", UtilMisc.toMap("partyId", partyId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//查找联系人手机号码
		EntityCondition telecomConditions=null;
		telecomConditions=EntityCondition.makeCondition(UtilMisc.toMap("partyId",partyId));
		List<GenericValue> telecomList=null;
		try {
			 telecomList = delegator.findList("PartyAndTelecomNumber", telecomConditions, UtilMisc.toSet("contactNumber"), null, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		//查找联系人地址
				EntityCondition addressConditions=null;
				addressConditions=EntityCondition.makeCondition(UtilMisc.toMap("partyId",partyId));
				List<GenericValue> addressList=null;
				try {
					addressList = delegator.findList("PartyAndContactMech", addressConditions, UtilMisc.toSet("paCountryGeoId","paCity"), null, null, false);
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
		List<String> personInfoList = FastList.newInstance();
		if(UtilValidate.isNotEmpty(personInfo)){
			
			String firstName=UtilValidate.isNotEmpty(personInfo.get("firstName"))?personInfo.get("firstName").toString():"";
			String lastName=UtilValidate.isNotEmpty(personInfo.get("lastName"))?personInfo.get("lastName").toString():"";
			String gender= UtilValidate.isNotEmpty(personInfo.get("gender"))?personInfo.get("gender").toString():"";
			String contactNumber =UtilValidate.isNotEmpty(telecomList)?telecomList.toString():"";
			String contactAddress =UtilValidate.isNotEmpty(addressList)?addressList.toString():"";
			personInfoList.add(firstName);
			personInfoList.add(lastName);
			personInfoList.add(gender);
			personInfoList.add(contactNumber);
			personInfoList.add(contactAddress);
		}
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("personInfoList", personInfoList);
		return result;
	}
	/**
	 * 查询联系人信息
	 * @param dctx
	 * @param context
	 * @return Map
	 */
	public static Map<String, Object> findContectsInfo(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dispatcher.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String partyId = (String) context.get("partyId");
		//查询联系人partyId
		EntityCondition findConditions=null;
		findConditions=EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo",partyId));
		List<GenericValue> contactPartyIdList=null;
		try {
			contactPartyIdList = delegator.findList("PartyRelationship", findConditions, UtilMisc.toSet("partyIdFrom"), null, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> partyIdList = FastList.newInstance();
		for(GenericValue contactParty:contactPartyIdList){
			Map<String, Object> faResult = null;
			Map<String, Object> serviceInMap = UtilMisc.toMap(); // Run Service req  Map
			try {
				
				serviceInMap.put("partyId", contactParty.get("partyIdFrom"));
				faResult=dispatcher.runSync("findPersonInfo", serviceInMap);
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			partyIdList.add(faResult.toString());
		}
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("partyIdList", partyIdList);
		return result;
	}
}
