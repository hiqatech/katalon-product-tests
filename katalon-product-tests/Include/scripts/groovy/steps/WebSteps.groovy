package steps

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKWF
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.keyword.internal.WebUIAbstractKeyword
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
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

import cucumber.api.java.Before
import cucumber.api.java.After

import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.testdata.ExcelData as ExcelData
import internal.GlobalVariable as GlobalVariable

import steps.CommonSteps


public class WebSteps {

	public static String Text1
	public static String Text2
	public static String Text3
	public static String Text4
	public static String Text5

	def qickWait = 300
	def waitTime = 4
	def maxTimeOut = 12


	def waitToAppear(String element) {
		Thread.sleep(qickWait)
		if(WebUI.waitForElementVisible(findTestObject(element), maxTimeOut)){
			moveToElement(element)
			WebUI.comment("Element " + element + " appeared")
		}
		else {
			assert "element appeared" == "true"
		}
	}

	def waitToDisAppear(String element) {
		Thread.sleep(qickWait)
		try{
			if(WebUI.verifyElementNotPresent(findTestObject(element), maxTimeOut))
				WebUI.comment("Element " + element + " not present")
			else if(WebUI.verifyElementNotVisible(findTestObject(element), maxTimeOut))
				WebUI.comment("Element " + element + " not visble")
			else assert "element not dissapeared" == "true"
		}
		catch(Exception ex){
			WebUI.comment("Element " + element + " disappeared")
		}
	}


	def waitToAppearIn(String element, String secs) {
		Thread.sleep(qickWait)
		if(WebUI.waitForElementVisible(findTestObject(element), secs as Integer)){
			moveToElement(element)
			WebUI.comment("Element " + element + " appeared")
		}
		else assert "element appeared" == "true"
	}


	def boolean elementClickable(String element) {
		if(WebUI.verifyElementClickable(findTestObject(element)))
			return true
		else return false
	}


	def waitToDisAppearIn(String element, String secs) {
		Thread.sleep(qickWait)
		try{
			if(WebUI.verifyElementNotPresent(findTestObject(element), maxTimeOut))
				WebUI.comment("Element " + element + " not present")
			else if(WebUI.verifyElementNotVisible(findTestObject(element), maxTimeOut))
				WebUI.comment("Element " + element + " not visble")
			else assert "element not dissapeared" == "true"
		}
		catch(Exception ex){
			WebUI.comment("Element " + element + " disappeared")
		}
	}


	def moveToElement(String element) {
		try{
			WebUI.scrollToElement(findTestObject(element), maxTimeOut)
		}
		catch(Exception ex){}
	}


	def String getElementText(String element) {
		waitToAppear(element)
		def text =  WebUI.getText(findTestObject(element))
		return text
	}


	def String getElementAttribute(String element, String attribute) {
		waitToAppear(element)
		def text =  WebUI.getAttribute(findTestObject(element), attribute)
		return text
	}

	//////////////////////////////////////////////////// StartUp Steps ///////////////////////////////////////////////////////////////////


	@Given('I wait "(.*)" secs for the "(.*)"')
	def IWaitXSecs(String time , String waitfor) {
		def secs = (time as Integer) * 1000
		Thread.sleep(secs)
	}


	@Given('I read data row "(.*)" from "(.*)"')
	def IReadRowDataFrom(String dataRow , String table) {
		CommonSteps.testData = findTestData(table)
		CommonSteps.testIndex = dataRow as Integer
		WebUI.comment(":) -> DataRow " + dataRow + " read from " + table + "data table")
	}


	@Given('I open the browser')
	def IOpenTheBrowser(String url) {
		WebUI.openBrowser(url)
		WebUI.maximizeWindow(FailureHandling.STOP_ON_FAILURE)
		WebUI.comment(":) -> Browser Opened")
	}


	@Given('I close the browser')
	def ICloseTheBrowser() {
		WebUI.closeBrowser()
		WebUI.comment(":) -> Browser Closed")
	}


	@Given('I open the browser on "(.*)" url')
	def IOpenTheBrowserOnUrl(String url) {
		IOpenTheBrowser(url)
		INavigateToURL(url)
		WebUI.comment(":) > Browser opened on " + url)
	}


	@Given('I navigate to "(.*)" url')
	def INavigateToURL(String url) {
		WebUI.navigateToUrl(url)
		WebUI.comment(":) -> Navigated to " + url)
	}


	////////////////////////////////////////////////// PEGA Specific StartUps /////////////////////////////////////////////////////////////////////


	@Given('I open the browser on "(.*)"')
	def IOpenTheBrowserOn(String env) {

		if(env.toLowerCase().contains("sda3"))
			CommonSteps.confData = findTestData("SDA3/TestConfig")
		else if(env.toLowerCase().contains("sda2"))
			CommonSteps.confData = findTestData("SDA2/TestConfig")

		if(!(env.toLowerCase().contains("dev") || env.toLowerCase().contains("uat")))
			env = GlobalVariable.environment + " " + env

		if(env.toLowerCase().contains("dev") && !env.toLowerCase().contains("sso"))
			CommonSteps.confIndex = 1
		else if(env.toLowerCase().contains("dev") && env.toLowerCase().contains("sso"))
			CommonSteps.confIndex = 2
		else if(env.toLowerCase().contains("dev") && env.toLowerCase().contains("portal"))
			CommonSteps.confIndex = 3
		else if(env.toLowerCase().contains("uat") && !env.toLowerCase().contains("sso"))
			CommonSteps.confIndex = 4
		else if(env.toLowerCase().contains("uat") && env.toLowerCase().contains("sso"))
			CommonSteps.confIndex = 5

		IOpenTheBrowserOnUrl(CommonSteps.getConfData("TestURL"))

		if(env.toLowerCase().contains("uat") && !env.toLowerCase().contains("sso") && env.toLowerCase().contains("sda2"))
			SwitchToAppPortalFromDev()
	}

	@Given('I login with "(.*)" user and password')
	def LoginTo(String user)
	{
		def usern
		def passw
		if(user.equals("agent")){
			usern = CommonSteps.getConfData("user")
			passw = CommonSteps.getConfData("passw")}
		ITypeIntoTheElement(usern, "PegaUI/PegaDevPage/user_name_field")
		ITypeIntoTheElement(passw, "PegaUI/PegaDevPage/password_field")
		ISelectTheElement("PegaUI/PegaDevPage/login_button")
	}


	def SwitchToAppPortalFromDev()
	{
		ISelectTheElement("PegaUI/PegaDevPage/launch_web_interface_dropdown")
		ISelectTheElement("PegaUI/PegaDevPage/adt_interaction_portal_option")
		ISwitchToWindowNumber("2")
	}


	@Given('I focus on the page "(.*)" details')
	def IFocusOnThePageContentDetails(String area) {
		ISwitchToTheFrame("default")

		if(area.equals("header"))
			ISwitchToTheFrame("PegaUI/PegaAppMainPage/header_frame")
		else if(area.equals("content") && WebUI.getUrl().contains("PRServlet"))
			ISwitchToTheFrame("PegaUI/PegaAppMainPage/middle_frame")
		else if (area.equals("top_content"))
			ISwitchToTheFrame("PegaUI/PegaAppMainPage/content_frame")
		else if (area.equals("sub_content"))
			ISwitchToTheFrame("PegaUI/PegaAppMainPage/work_frame")

		WebUI.comment(":) -> Detail " + area + " has been focused")
	}


	@Given('I search for the customer by "(.*)"')
	def ISearchForCustomerById(String searchType) {
		ISwitchToTheFrame("PegaUI/PegaAppMainPage/work_frame")

		if(searchType.toLowerCase().equals("customerid"))
		{
			def text = CommonSteps.getTestData(searchType);
			Thread.sleep(qickWait)
			ITypeIntoTheElement(text,"PegaUI/PegaAppMainPage/search_field")
			Thread.sleep(qickWait)
			ISelectTheElement("PegaUI/PegaAppMainPage/search_button")
			Thread.sleep(qickWait * 3)
		}
	}


	///////////////////////////////////////////////// Common UI Steps //////////////////////////////////////////////////////////////////////


	@Given('I close the "(.*)" window')
	def ICloseTheWindowX(String number) {
		def window = number as Integer
		WebUI.closeWindowIndex(window - 1)
		WebUI.comment(":) - Window " + number + " closed")
	}


	@Given('I switch to the "(.*)" window')
	def ISwitchToWindowNumber(String number) {
		def window = number as Integer
		WebUI.switchToWindowIndex(window - 1)
		WebUI.switchToDefaultContent()
		WebUI.comment(":) -> Switched to " + number + " window")
	}


	@Given('I switch to the window with "(.*)" title')
	def SwitchToWindowWithTite(String title) {
		WebUI.switchToWindowTitle(title)
		WebUI.switchToDefaultContent()
		WebUI.comment(":) -> Switched to window with " + title + " title")
	}


	@Given('I switch to the "(.*)" content')
	def ISwitchToTheFrame(String frame) {
		if(frame.contains("default"))
			WebUI.switchToDefaultContent()
		else {	Thread.sleep(qickWait)
			try{
				waitToAppear(frame)
				WebUI.switchToFrame(findTestObject(frame), 5)}
			catch(Exception ex)
			{WebUI.comment("Could not switch to " + frame)
			}
		}
	}


	@Given('I focus on the "(.*)"')
	def IFocusOnTheElement(String element) {
		//WebUI.sendKeys(findTestObject(element),  Keys.chord(Keys.PAGE_UP))
		WebUI.scrollToElement(findTestObject(element), maxTimeOut)
		Thread.sleep(qickWait * 2)
		WebUI.comment(":) -> Element " + element + " has been focused")
	}


	@Given('I "(.*)" the alert')
	def IActionTheAlert(String action) {
		Thread.sleep(qickWait * 2)
		if(action.equals("accept"))
			WebUI.acceptAlert()
		else if(action.equals("dismiss"))
			WebUI.dismissAlert()
		Thread.sleep(qickWait * 2)
		WebUI.comment(":) -> Alert has been " + action + "ed")
	}


	@Given('I create "(.*)" string with "(.*)" into "(.*)"')
	def ICreateText(String textType, String name, String storeTo) {
		def payload = CommonSteps.getConfData(textType)
		def value = getTextStored(name)

		payload = payload.toString().replaceAll("IDString",value)
		setTextStored(payload,storeTo)
		WebUI.comment("Text  " + payload + " created with "  + value + " and stored to " + storeTo)
	}


	@And('I type "(.*)" into the "(.*)"')
	def ITypeIntoTheElement(String entry, String field) {
		waitToAppear(field)
		WebUI.sendKeys(findTestObject(field), Keys.chord(entry))
		Thread.sleep(300)
		WebUI.comment(":) -> Entry " + entry + " typed into " + field)
	}


	@And('I type "(.*)" stored text into the "(.*)"')
	def ITypeStoredTextIntoTheElement(String entry, String field) {
		waitToAppear(field)
		entry = getTextStored(entry)
		ITypeIntoTheElement(entry,field)
		WebUI.comment(":) -> Entry " + entry + " typed into " + field)
	}


	@And('I enter "(.*)" in form "(.*)" into the "(.*)"')
	def ITypeInFormIntoTheElement(String entry, String format, String field) {
		waitToAppear(field)
		if(entry.contains("current_date"))
			entry = CommonSteps.getDate(entry,format)
		if(entry.contains("current_time"))
			entry = CommonSteps.getTime(entry,format)

		ITypeIntoTheElement(entry,field)
		WebUI.comment(":) -> Entry " + entry + " typed into " + field)
	}


	@And('I type "(.*)" data into the "(.*)"')
	def ITypeDataIntoTheElement(String entry, String field) {
		def text = CommonSteps.getTestData(entry)
		ITypeIntoTheElement(text,field)
	}


	@And('I send "(.*)" key to the "(.*)"')
	def ISendKeyToElement(String entry, String element) {
		waitToAppear(element)
		if(entry.toUpperCase().equals("ENTER")){
			WebUI.sendKeys(findTestObject(element),  Keys.chord(Keys.ENTER))}
		WebUI.sendKeys(findTestObject(element),  Keys.chord(Keys.PAGE_UP))
		WebUI.comment(":) -> Key " + entry + " sent to " + element)
	}


	@And('I hover the "(.*)"')
	def IHoverTheElement(String element) {
		waitToAppear(element)
		WebUI.mouseOver(findTestObject(element))
		WebUI.comment(":) -> Element " + element + " has been hovered")
	}


	def ISelectTheElementWithScript(String element)
	{
		try{
		WebElement myElement =  WebUiCommonHelper.findWebElement(findTestObject(element),maxTimeOut)
		WebUI.executeJavaScript("arguments[0].click()", Arrays.asList(myElement))
		}
		catch (Exception ex){}
	}


	@When('I select the "(.*)"')
	def ISelectTheElement(String element) {
		waitToAppear(element)
		WebUI.waitForElementClickable(findTestObject(element), waitTime)

		if(element.contains("page") ){
			Thread.sleep(qickWait * 2)
			WebUI.sendKeys(findTestObject(element),  Keys.chord(Keys.PAGE_UP))
			Thread.sleep(qickWait)
		}
		if(element.contains("search"))
			Thread.sleep(qickWait)
	
			if(element.contains("button")||element.contains("checkbox")||element.contains("field")||element.contains("tab")||element.contains("label")){
				IFocusOnTheElement(element)
				ISelectTheElementWithScript(element)}
			else try{WebUI.click(findTestObject(element))}
		catch(Exception ex){
			Thread.sleep(qickWait)
			ISelectTheElementWithScript(element)
			Thread.sleep(qickWait)
		}
		WebUI.comment(":) -> Element " + element + " has been selected ")
	}


	@When('I clear the "(.*)"')
	def IClearTheElement(String element) {
		waitToAppear(element)
		if(element.contains("date")){
			for(def i=0; i<7;i++)
			{WebUI.sendKeys(findTestObject(element),  Keys.chord(Keys.BACK_SPACE))}
		}
		WebUI.clearText(findTestObject(element))

	}


	@When('I choose the "(.*)" from the "(.*)"')
	def IChooseTheElementFromDropDown(String option, String dropdown) {
		ISelectTheElementWithScript(dropdown)
		IWaitXSecs("2","dropdown options")
		ISelectTheElement(option)
		WebUI.comment(":) -> Option " + option + " has been selected from " + dropdown)
	}


	@When('I choose the "(.*)" option from the "(.*)"')
	def IChooseTheOptionFromDropDown(String option, String dropdown) {
		Thread.sleep(1000)
		try{WebUI.selectOptionByValue(findTestObject(dropdown), option, false)}
		catch(Exception ex){WebUI.selectOptionByLabel(findTestObject(dropdown), option, false)}
		WebUI.comment(":) -> Option " + option + " has been selected from " + dropdown)
	}


	@When('I choose the "(.*)" data option from the "(.*)"')
	def IChooseTheDataFromDropDown(String option, String dropdown) {
		waitToAppear(dropdown)
		option = CommonSteps.getTestData(option)
		try{WebUI.selectOptionByValue(findTestObject(dropdown), option, false)}
		catch(Exception ex){WebUI.selectOptionByLabel(findTestObject(dropdown), option, false)}
		WebUI.comment(":) -> Option " + option + " has been selected from " + dropdown)
	}


	@Given('I store the "(.*)" text as "(.*)"')
	def ISaveTheText(String element, String textNumber) {
		def actual
		def number = textNumber
		if (element.equals("url"))
			actual = WebUI.getUrl()
		else actual = getElementText(element)
		setTextStored(actual,number)
		WebUI.comment(":) -> Text stored " + number + " : "  +  actual + " from element " + element)
	}



	////////////////////////////////////////////////// Verifications /////////////////////////////////////////////////////////////////////


	@Then('I should "(.*)" the "(.*)"')
	def IShouldSeeTheElement(String condition, String element) {
		if(!condition.equals("see") && !condition.equals("not see"))
		{ WebUI.comment(":( -> I can " + condition + " the " + element)
			assert condition == "see or not see"
		}
		if(condition.equals("see"))
			waitToAppear(element)
		else if(condition.equals("not see"))
			waitToDisAppear(element)

		WebUI.comment(":) -> I can " + condition + " the " + element)
	}


	@Then('I should "(.*)" the "(.*)" in "(.*)" secs')
	def IShouldSeeTheElementInSecs(String condition, String element, String secs) {
		if(!condition.equals("see") && !condition.equals("not see"))
		{ WebUI.comment(":( -> I can " + condition + " the " + element + " in " + secs + " secs")
			assert condition == "see or not see"
		}
		if(condition.equals("see"))
			waitToAppearIn(element, secs)
		else if(condition.equals("not see"))
			waitToDisAppearIn(element,secs)

		WebUI.comment(":) -> I can " + condition + " the " + element + " in " + secs + " secs")
	}


	@Then('I should "(.*)" the "(.*)" with "(.*)" data')
	def ISeeTheElementWithData(String condition,String element, String expected) {
		if(!condition.equals("see") && !condition.equals("not see"))
		{ WebUI.comment(":( -> I can " + condition + " the " + element)
			assert condition == "see or not see"
		}

		expected = CommonSteps.getTestData(expected)

		if(condition.equals("see"))
			waitToAppearIn(element + expected)
		else if(condition.equals("not see"))
			waitToDisAppearIn(element + expected)

		WebUI.comment(":) -> I can " + condition + " the " + element +  " " + expected)
	}


	@Then('The "(.*)" text should "(.*)" the "(.*)" text')
	def TheElementTextShould(String element,String condition, String expected) {
		if(!condition.equals("equal") && !condition.equals("contain") && !condition.equals("not contain") && !condition.equals("not equal"))
		{ WebUI.comment(":( -> Element " + element + " text " + condition + " " + expected)
			assert condition == "equal or contain"
		}
		def ext = CommonSteps.getExtension(expected)
		expected = CommonSteps.getExpected(expected)
		expected = expected + ext.toString()

		def actual = getElementText(element)
		CommonSteps.verifyTexts(actual,condition,expected)
		WebUI.comment(":) -> Element " + element + " text : "  +  actual + " " + condition + " " + expected)
	}


	@Then('The "(.*)" date time should "(.*)" the "(.*)" in form "(.*)"')
	def TheElementTextShouldDateTime(String element,String condition, String expected, String format) {
		if(!condition.equals("equal") && !condition.equals("contain") && !condition.equals("not contain") && !condition.equals("not equal"))
		{ WebUI.comment(":( -> Element " + element + " text " + condition + " " + expected)
			assert condition == "equal or contain"
		}
		def actual = getElementText(element)
		expected = CommonSteps.getDate(expected, format)
		CommonSteps.verifyTexts(actual,condition,expected)
		WebUI.comment(":) -> Element " + element + " text "  +  actual + " " + condition + " " + expected)
	}


	@Then('The "(.*)" text should "(.*)" the "(.*)" data')
	def TheElementTextShouldData(String element,String condition, String expected) {
		if(!condition.equals("equal") && !condition.equals("contain") && !condition.equals("not contain") && !condition.equals("not equal"))
		{ WebUI.comment(":( -> Element " + element + " text " + condition + " " + expected + " data")
			assert condition == "equal or contain"
		}

		def ext = CommonSteps.getExtension(expected)
		expected = CommonSteps.getExpected(expected)
		expected = CommonSteps.getTestData(expected) + ext.toString()
		element = getPegaSpecElement(element, expected)

		def actual
		if(element.contains("dropdown") && condition.contains("equal")){
			WebUI.verifyOptionSelectedByValue(findTestObject(element), expected, false, maxTimeOut)
			actual = expected}
		else actual = getElementText(element)
		CommonSteps.verifyTexts(actual,condition,expected)

		WebUI.comment(":) -> Element " + element + " text "  +  actual + " " + condition + " " + expected + " data")
	}


	@Then('The "(.*)" "(.*)" attribute should "(.*)" the "(.*)" text')
	def TheElementAttributeShould(String element, String attribute, String condition, String expected) {
		if(!condition.equals("equal") && !condition.equals("contain") && !condition.equals("not contain") && !condition.equals("not equal"))
		{ WebUI.comment(":( -> Element " + element + " attribute "  +  attribute + " " + condition + " " + expected)
			assert condition == "equal or contain"
		}

		def ext = CommonSteps.getExtension(expected)
		expected = CommonSteps.getExpected(expected)
		element = getPegaSpecElement(element, expected)

		def actual = getElementAttribute(element, attribute)
		CommonSteps.verifyTexts(actual,condition,expected)
		WebUI.comment(":) -> Element " + element + " attribute "  +  actual + " " + condition + " " + expected)
	}


	@Then('The "(.*)" "(.*)" attribute should "(.*)" the "(.*)" data')
	def TheElementAttributeShouldData(String element, String attribute, String condition, String expected) {
		if(!condition.equals("equal") && !condition.equals("contain") && !condition.equals("not contain") && !condition.equals("not equal"))
		{ WebUI.comment(":( -> Element " + element + " attribute " + attribute + " = " +  attribute + " " + condition + " " + expected + " data")
			assert condition == "equal or contain"
		}

		def ext = CommonSteps.getExtension(expected)
		expected = CommonSteps.getTestData(expected) + ext.toString()
		element = getPegaSpecElement(element, expected)

		def actual = getElementAttribute(element, attribute)
		CommonSteps.verifyTexts(actual,condition,expected)
		WebUI.comment(":) -> Element " + element + " attribute " + attribute + " = " +  actual + " " + condition + " " + expected + " data")
	}

	@Then('The "(.*)" should "(.*)" clickable')
	def TheElementClickable(String element , String condition) {
		if(!condition.equals("be") && !condition.equals("not be"))
		{ WebUI.comment(":( -> The " + element + " should " + condition + " clickable")
			assert condition == "be or not be"
		}
		waitToAppear(element)
		if(condition.equals("see")){
			if (!elementClickable(element))
				assert	"see clickable" == "not see clickable"}
		else if(condition.equals("not see")){
			if (!elementClickable(element))
				assert "not see clickable" == "see clickable"}

		WebUI.comment(":) -> The " + element + " should " + condition + " clickable")
	}


	@Given('The "(.*)" stored text should "(.*)" "(.*)" stored text')
	def TheTextShouldText(String operand1, String condition, String operand2) {
		if(!condition.equals("equal") && !condition.equals("contain") && !condition.equals("not contain") && !condition.equals("not equal"))
		{ WebUI.comment(":( -> -> Stored text " + operand1 + " " + condition + " " + operand2)
			assert condition == "equal or contain"
		}
		def actual = getTextStored(operand1)
		def expected = getTextStored(operand2)
		CommonSteps.verifyTexts(actual,condition,expected)
		WebUI.comment(":) -> Stored text " + actual + " " + condition + " " + expected)
	}


	@Given('The "(.*)" stored date should be "(.*)" than "(.*)" stored date')
	def TheDateShouldDate(String operand1, String condition, String operand2) {
		if(!condition.equals("younger") && !condition.equals("older") && !condition.equals("equal") && !condition.equals("not younger") && !condition.equals("not older") && !condition.equals("not equal"))
		{ WebUI.comment(":( -> Stored date " + operand1 + " " + condition + " " + operand2)
			assert condition == "younger or older"
		}
		def actual = getTextStored(operand1)
		def expected = getTextStored(operand2)
		CommonSteps.verifyDates(actual,condition,expected)
		WebUI.comment(":) -> Stored date " + actual + " " + condition + " " + expected)
	}


	@Given('The "(.*)" stored value should be "(.*)" than "(.*)" stored value')
	def TheValueShouldValue(String operand1, String condition, String operand2) {
		if(!condition.equals("below") && !condition.equals("above") && !condition.equals("equal") && !condition.equals("not below") && !condition.equals("not above") && !condition.equals("not equal"))
		{ WebUI.comment(":( -> Stored value " + operand1 + " " + condition + " " + operand2)
			assert condition == "below or above or equal"
		}
		def actual = getTextStored(operand1)
		def expected = getTextStored(operand2)
		CommonSteps.verifyValues(actual,condition,expected)
		WebUI.comment(":) -> Stored value " + actual + " " + condition + " " + expected)
	}


	@Given('The "(.*)" text should "(.*)" the text of "(.*)"')
	def TheElementTextShouldElementText(String element1, String condition, String element2) {
		if(!condition.equals("equal") && !condition.equals("contain") && !condition.equals("not contain") && !condition.equals("not equal"))
		{ WebUI.comment(":( -> Element1 " + element1 + " text " + condition + " Element2 " + element2  + " text ")
			assert condition == "equal or contain or not equal or not contain"
		}
		def actual = getElementText(element1)
		def expected = getElementText(element2)
		CommonSteps.verifyTexts(actual,condition,expected)
		WebUI.comment(":) -> Element1 " + element1 + " text "  +  actual + " " + condition + " Element2 " + element2  + " text ")
	}


	@Given('The "(.*)" date should be "(.*)" than "(.*)" date')
	def TheElementDateShouldElementDate(String element1, String condition, String element2) {
		if(!condition.equals("younger") && !condition.equals("older") && !condition.equals("equal") && !condition.equals("not younger") && !condition.equals("not older") && !condition.equals("not equal"))
		{ WebUI.comment(":( -> Element1 " + element1 + " date " + condition + " Element2 " + element2  + " date ")
			assert condition == "younger or older"
		}
		def actual = getElementText(element1)
		def expected = getElementText(element2)
		CommonSteps.verifyDates(actual,condition,expected)
		WebUI.comment(":) -> Element1 " + element1 + " date "  +  actual + " " + condition + " Element2 " + element2  + " date ")
	}


	@Given('The "(.*)" value should be "(.*)" than "(.*)" value')
	def TheElementValueShouldElementValue(String element1, String condition, String element2) {
		if(!condition.equals("below") && !condition.equals("above") && !condition.equals("equal") && !condition.equals("not below") && !condition.equals("not above") && !condition.equals("not equal"))
		{ WebUI.comment(":( -> Element1 " + element1 + " value " + condition + " Element2 " + element2  + " value ")
			assert condition == "below or above or equal"
		}
		def actual = getElementText(element1)
		def expected = getElementText(element2)
		CommonSteps.verifyValues(actual,condition,expected)
		WebUI.comment(":) -> Element1 " + element1 + " value "  +  actual + " " + condition + " Element2 " + element2  + " value ")
	}

	public static  String getPegaSpecElement(String element, String expected){
		String elm = element
		String exp = expected

		if(elm.contains("icon")){
			if(expected.contains("Paid")){
				elm = elm + "_green"}
			else if(expected.contains("OverDue")){
				elm = elm + "_red"}
		}
		return elm
	}


	public static String getTextStored(String operand)
	{
		def op = operand.toLowerCase()
		def value

		if(op.equals("text1"))
			value = Text1
		else if(op.equals("text2"))
			value = Text2
		else if(op.equals("text3"))
			value = Text3
		else if(op.equals("text4"))
			value = Text4
		else if(op.equals("text5"))
			value = Text5

		return value
	}


	public static void setTextStored(String value, String operand)
	{
		def op = operand.toLowerCase()

		if(op.equals("text1"))
			Text1 = value
		else if(op.equals("text2"))
			Text2 = value
		else if(op.equals("text3"))
			Text3 = value
		else if(op.equals("text4"))
			Text4 = value
		else if(op.equals("text5"))
			Text5 = value

	}


	///////////////////////////////////////////////// Fill The Forms Steps //////////////////////////////////////////////////////////////////////////////



	@And('I fill the form all the inputs by the excel')
	def IFillTheFormFromExcel() {

		String folder = "PEGAUI/CustomerPage/Forms/AllForms/"

		for(int i=1 ; i <  CommonSteps.testData.columnNumbers - 3 ; i++)
		{
			String step = "Step" + i.toString()
			String action = CommonSteps.getTestData("Step"+i)
			String element = ""
			String option = ""

			try{ element = action.substring(action.indexOf('-') + 2 , action.length())
				if(element.contains("-"))
					option = element.substring(action.indexOf('-') + 2 , action.length())
			}
			catch(Exception ex){WebUI.comment(ex.toString())}

			String elementPath = folder + element
			option = folder + option

			if(action.contains("clear"))
				IClearTheElement(elementPath)

			else if(action.contains("enter") && action.contains("date"))
				ITypeInFormIntoTheElement("current_date+14","M/d/yyyy",elementPath)
			else if(action.contains("enter") && action.contains("phone"))
				ITypeIntoTheElement("0761234567",elementPath)
			else if(action.contains("enter") )
				ITypeIntoTheElement(element,elementPath)

			else if(action.contains("choose") && action.contains("time"))
				IChooseTheOptionFromDropDown("17:00", elementPath)
			else if(action.contains("choose") && action.contains("salutation"))
				IChooseTheOptionFromDropDown("Mr.", elementPath)
			else if(action.contains("choose"))
				IChooseTheOptionFromDropDown(option, elementPath)

			else if(action.contains("select"))
				ISelectTheElement(elementPath)

		}


	}



}