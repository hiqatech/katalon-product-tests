package steps

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.CheckpointFactory
import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository

import cucumber.api.Scenario
import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import cucumber.api.java.Before
import cucumber.api.java.After

import com.kms.katalon.core.configuration.RunConfiguration

import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.RestRequestObjectBuilder
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import groovy.json.JsonSlurper
import org.json.JSONArray
import org.json.JSONObject

import steps.WebSteps
import steps.CommonSteps

public class APISteps {

	public static ResponseObject respObj1
	public static ResponseObject respObj2
	public static ResponseObject respObj3
	public static ResponseObject respObj4
	public static ResponseObject respObj5

	public static JSONObject jsonObj1
	public static JSONObject jsonObj2
	public static JSONObject jsonObj3
	public static JSONObject jsonObj4
	public static JSONObject jsonObj5

	public static String devURL = "https://jit-api-internal-libertyglobal-com.upc.biz:443"


	@When('I request the "(.*)" into "(.*)"')
	def IRequestByMessageID(String requestName, String storeX) {
		ResponseObject response
		String token = getAkanaToken()
		def customerID = CommonSteps.getTestData("CustomerId");
		if(requestName.equals("getCustomerCases"))
			response = getCustomerCases(token, customerID)
		setResponseStored(response, storeX)
		WebUI.comment(":) -> Requested " + requestName + " into "  +  storeX)
	}

	@When('I store the "(.*)" into "(.*)" by "(.*)"')
	def IStoreTheJSonDetailsInto(String storeX, String storeY, String by ) {

		ResponseObject response = getResponseStored(storeX)
		String message = WebSteps.getTextStored(by)
		String caseID = message.findAll( /\d+/ ).replaceAll("[","").toString().replaceAll("]","")

		JSONObject myJson = getResponseDetails(response,caseID)
		setJSonStored(myJson, storeY)
		WebUI.comment(":) -> Json details from " + storeX + " stored into "  +  storeY + " by " + by)
	}

	@When('The "(.*)" "(.*)" value should "(.*)" "(.*)" data')
	def TheJSonKeyShouldTheData(String jsonStore, String key, String condition, String expect){
		JSONObject myJson = getJSonStored(jsonStore)
		String value = getJsonObjectValueByKey(myJson, 0)
		String expected = CommonSteps.getTestData(expect)

		CommonSteps.verifyTexts(value, condition, expected)
		WebUI.comment(":) -> Json " + jsonStore + " "  +  key + " " + condition + " " + expected)
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	public static String getAkanaToken(){
		String url = devURL + "/oauth/oauth20/token?client_id=ADT_Pega_CH&client_secret=d5c0eb86b8e1320e6001b736c564594919c66bba&grant_type=client_credentials"
		TestObjectProperty header1 = new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/x-www-form-urlencoded")
		ArrayList defaultHeaders = Arrays.asList(header1)
		RequestObject request = new RequestObject("objectId")
		request.setRestUrl(url)
		request.setHttpHeaderProperties(defaultHeaders)
		request.setRestRequestMethod("POST")
		WebUI.comment("Get Akana Token")
		ResponseObject respObj = WS.sendRequest(request)
		WebUI.comment(respObj.statusCode.toString())
		WebUI.comment(respObj.getResponseBodyContent().toString())
		return getJsonResponseValueByKey(respObj,"access_token" )
	}


	

	public static ResponseObject getCustomerCases(String token ,String customerID ) {
		String endpoint = devURL + "/CH/Peal/Care/Customers/v1.0/customers/"+ customerID +"/cases?cty=CH&chl=ADT"
		TestObjectProperty header1 = new TestObjectProperty("Authorization", ConditionType.EQUALS, "Bearer " + token)
		ArrayList defaultHeaders = Arrays.asList(header1)
		RequestObject request = new RequestObject("objectId")
		request.setRestUrl(endpoint)
		request.setHttpHeaderProperties(defaultHeaders)
		request.setRestRequestMethod("GET")
		WebUI.comment("Get customer cases")
		ResponseObject respObj = WS.sendRequest(request)
		WebUI.comment(respObj.statusCode.toString())
		return respObj
	}


	public static ResponseObject postRequest(String endpoint, String authHeader , body) {
		TestObjectProperty header1 = new TestObjectProperty("Authorization", ConditionType.EQUALS, authHeader)
		TestObjectProperty header2 = new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/json")
		TestObjectProperty header3 = new TestObjectProperty("Accept", ConditionType.EQUALS, "application/json")
		ArrayList defaultHeaders = Arrays.asList(header1, header2, header3)
		RequestObject request = new RequestObject("objectId")
		request.setRestUrl(endpoint)
		request.setHttpHeaderProperties(defaultHeaders)
		request.setRestRequestMethod("POST")
		request.setBodyContent(new HttpTextBodyContent(body))
		ResponseObject respObj = WS.sendRequest(request)
		return respObj
	}

	public static JSONObject getResponseDetails(ResponseObject response, String key){
		
				def JSONArray jsonArray = new JSONArray(response.getResponseBodyContent().toString());
		
				ArrayList<JSONObject> jsonObjects = new ArrayList<>()
				jsonObjects = getJSONObjectListFromJSONArray(jsonArray)
		
				WebUI.comment(jsonObjects.size().toString())
		
				for (int i = 0; i < jsonObjects.size(); i++)   {
					if(jsonObjects.get(i).toString().contains(key)){
						WebUI.comment(jsonObjects.get(i).toString())
						return jsonObjects.get(i)
					}
				}
			}
		
		
	public static List<JSONObject> getJSONObjectListFromJSONArray(JSONArray array){
		ArrayList<JSONObject> jsonObjects = new ArrayList<>();
		for (int i = 0;i < (array != null ? array.length() : 0);jsonObjects.add(array.getJSONObject(i++)));
			return jsonObjects;
	}
	
		
	public static String getJsonResponseValueByKey(ResponseObject respObj, String key){
		JsonSlurper slurper = new JsonSlurper()
		Map parsedJson = slurper.parseText(respObj.responseBodyContent.toString())
		return parsedJson.get(key)
	}

	public static String getJsonObjectValueByKey(JSONObject jsonObj, String key){
		JsonSlurper slurper = new JsonSlurper()
		Map parsedJson = slurper.parseText(jsonObj.toString())
		return parsedJson.get(key)
	}


	public static ResponseObject getResponseStored(String operand)
	{
		def op = operand.toLowerCase()
		ResponseObject response

		if(op.equals("response1"))
			response = respObj1
		else if(op.equals("response2"))
			response = respObj2
		else if(op.equals("response3"))
			response = respObj3
		else if(op.equals("response4"))
			response = respObj4
		else if(op.equals("response5"))
			response = respObj5

		return response
	}


	public static void setResponseStored(ResponseObject response, String operand)
	{
		def op = operand.toLowerCase()

		if(op.equals("response1"))
			respObj1 = response
		else if(op.equals("response2"))
			respObj2 = response
		else if(op.equals("response3"))
			respObj3 = response
		else if(op.equals("response4"))
			respObj4 = response
		else if(op.equals("response5"))
			respObj5 = response
	}


	public static JSONObject getJSonStored(String operand)
	{
		def op = operand.toLowerCase()
		JSONObject json

		if(op.equals("jsonobj1"))
			json = jsonObj1
		else if(op.equals("jsonobj2"))
			json = jsonObj2
		else if(op.equals("jsonobj3"))
			json = jsonObj3
		else if(op.equals("jsonobj4"))
			json = jsonObj4
		else if(op.equals("jsonobj5"))
			json = jsonObj5

		return json
	}


	public static void setJSonStored(JSONObject json, String operand)
	{
		def op = operand.toLowerCase()

		if(op.equals("jsonobj1"))
			jsonObj1 = json
		else if(op.equals("jsonobj2"))
			jsonObj2 = json
		else if(op.equals("jsonobj3"))
			jsonObj3 = json
		else if(op.equals("jsonobj4"))
			jsonObj4 = json
		else if(op.equals("jsonobj5"))
			jsonObj5 = json
	}


}