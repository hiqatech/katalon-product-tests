
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

class MyAPISteps {

	def testIndex = 0
	def confIndex = 0

	String Text1
	String Text2
	String Text3
	String Text4
	String Text5


	@When('I request the "(.*)" by the SuccessMessage ID')
	def IRequestByMessageID(String requestName) {

		//getCustomerCases

		def token = getAkanaToken()

		ResponseObject response = getRequest(endpoint, token)

		WebUI.comment(response.toString())
	}


	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	public String getAkanaToken(){

		
		TestObjectProperty header2 = new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/json")
		TestObjectProperty header3 = new TestObjectProperty("Accept", ConditionType.EQUALS, "application/json")
		ArrayList defaultHeaders = Arrays.asList(header2, header3)

		String body = '{"client_id":""}'

		RequestObject request = new RequestObject("objectId")
		request.setRestUrl(url)
		request.setHttpHeaderProperties(defaultHeaders)
		request.setRestRequestMethod("POST")
		request.setBodyContent(new HttpTextBodyContent(body))
		ResponseObject respObj = WS.sendRequest(request)

		JsonSlurper slurper = new JsonSlurper()
		Map parsedJson = slurper.parseText(respObj.getResponseBodyContent())
		WebUI.comment("token is  " + parsedJson.get("access_token"))
		return parsedJson.get("access_token")

	}


	public ResponseObject getRequest(String endpoint, String authHeader ) {

		TestObjectProperty header1 = new TestObjectProperty("Authorization", ConditionType.EQUALS, authHeader)
		TestObjectProperty header2 = new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/json")
		TestObjectProperty header3 = new TestObjectProperty("Accept", ConditionType.EQUALS, "application/json")
		ArrayList defaultHeaders = Arrays.asList(header1, header2, header3)

		RequestObject request = new RequestObject("objectId")
		request.setRestUrl(endpoint)
		request.setHttpHeaderProperties(defaultHeaders)
		request.setRestRequestMethod("GET")

		ResponseObject respObj = WS.sendRequest(request)
		return respObj

	}


	public ResponseObject postRequest(String endpoint, String authHeader , body) {

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


}