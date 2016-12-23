package com.banfftech.personerp;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class PersonErpService {
	public static final String module = PersonErpQueryService.class.getName();

	/**
	 * 添加联系人
	 * @param dctx
	 * @param context
	 * @return Map
	 */
	public static Map<String, Object> addContects(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dispatcher.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String firstName = (String) context.get("firstName");
		String lastName = (String) context.get("lastName");
		String gender = (String) context.get("gender");
		String contactNumber = (String) context.get("contactNumber");
		String contactAddress = (String) context.get("contactAddress");
		String contactEmail = (String) context.get("contactEmail");
		String contactGroup = (String) context.get("contactGroup");
		String contactCompany = (String) context.get("contactCompany");
		String userLoginId    = "admin";
		
		GenericValue userLogin;
		try {
			userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId",userLoginId),false);
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return ServiceUtil.returnError(e1.getMessage());
		}
		
		Map<String, Object> inputFieldMap = FastMap.newInstance();
		inputFieldMap.put("firstName", firstName);
		inputFieldMap.put("lastName", lastName);
		inputFieldMap.put("gender", gender);
		
		//添加联系人party
		Map<String, Object> createPerson = null;
		try {
			createPerson=dispatcher.runSync("createUpdatePerson", inputFieldMap);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		
		//添加联系人手机号码
		
		if(UtilValidate.isNotEmpty(createPerson)){
			String partyId=createPerson.get("partyId").toString();
			Map<String, Object> inputTelecom = FastMap.newInstance();
			inputTelecom.put("partyId", partyId);
			inputTelecom.put("contactNumber", contactNumber);
			inputTelecom.put("userLogin",userLogin);
			inputTelecom.put("contactMechPurposeTypeId", "PHONE_WORK");
			Map<String, Object> createTelecom = null;
			try {
				createTelecom=dispatcher.runSync("createUpdatePartyTelecomNumber", inputTelecom);
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ServiceUtil.returnError(e.getMessage());
			}
			//添加联系人email
			if(UtilValidate.isEmpty(createTelecom)){
				String contactMechTypeId="EMAIL_ADDRESS";
				Map<String, Object> inputContactMechTypeId = FastMap.newInstance();
				inputContactMechTypeId.put("contactMechTypeId", contactMechTypeId);
				Map<String, Object> createContactMechTypeId = null;
				contactMechTypeId = dispatcher.runSync("createContactMech", inputContactMechTypeId);
				
				
				Map<String, Object> inputEmail = FastMap.newInstance();
				inputEmail.put("emailAddress", contactEmail);
				inputEmail.put("userLogin",userLogin);
				inputEmail.put("partyId", partyId);
				inputEmail.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
				Map<String, Object> createEmail = null;
				try {
					createEmail = dispatcher.runSync("createUpdatePartyEmailAddress", inputEmail);
				} catch (GenericServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//return ServiceUtil.returnError(e.getMessage());
				}
			}
		}
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("partyId", createPerson.get("partyId"));
		return result;
	}
}
