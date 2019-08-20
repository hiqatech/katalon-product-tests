
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
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

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
import java.nio.file.Files;
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import com.kms.katalon.core.configuration.RunConfiguration

import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.testdata.ExcelData as ExcelData


class MyWebSteps {

	ExcelData testData
	ExcelData confData

	def testIndex = 0
	def confIndex = 0

	String Text1
	String Text2
	String Text3
	String Text4
	String Text5

	def qickWait = 300
	def waitTime = 4 * qickWait
	def maxTimeOut = 10

	Scenario myScenario

	//WebDriver driver = DriverFactory.getWebDriver()

	@Before
	public void setUp() {
		WebUI.comment("UI Automated Tests - " + getTimeStamp())
	}

	@After
	public void tearDown(Scenario scenario) {
		if(scenario.isFailed()) {
			String filePath = WebUI.takeScreenshot()
			File file = new File(filePath)
			scenario.embed(Files.readAllBytes(file.toPath()), "image/png")
			Path dest =  Paths.get(RunConfiguration.getReportFolder())
			Path source = Paths.get(filePath)

			try{
				Files.copy(source,dest,StandardCopyOption.REPLACE_EXISTING)}
			catch(Exception ex){WebUI.comment(ex.toString())}
		}
		ICloseTheBrowser()
	}

	def zipIt(String inputDir, String zipFileName){
		try{
			ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(zipFileName))
			new File(inputDir).eachFile() { file ->
				//check if file
				if (file.isFile()){
					zipFile.putNextEntry(new ZipEntry(file.name))
					def buffer = new byte[file.size()]
					file.withInputStream {
						zipFile.write(buffer, 0, it.read(buffer))
					}
					zipFile.closeEntry()
				}
			}
			zipFile.close()
		}
		catch(Exception ex){}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	def waitToAppear(String element) {
		Thread.sleep(qickWait)
		if(WebUI.waitForElementPresent(findTestObject(element), 10)){
			moveToElement(element)
			WebUI.comment("Element " + element + " appeared")
		}
		else if(WebUI.waitForElementVisible(findTestObject(element), 5) ){
			moveToElement(element)
			WebUI.comment("Element " + element + " appeared")}
		else {assert "element appeared" == "true"}
	}

	def moveToElement(String element) {
		try{WebUI.scrollToElement(findTestObject(element), maxTimeOut)}
		catch(Exception ex){}
	}

	def waitToDisAppear(String element) {
		Thread.sleep(qickWait)
		if(WebUI.waitForElementNotPresent(findTestObject(element), 5))
			WebUI.comment("Element " + element + " disappeared")
		else if(WebUI.waitForElementNotVisible(findTestObject(element), 10) )
			WebUI.comment("Element " + element + " disappeared")
		else {assert "element disappeared" == "false"}
	}

	def String getElementText(String element)
	{
		waitToAppear(element)
		def text =  WebUI.getText(findTestObject(element))
		return text
	}


	def String getElementAttribute(String element, String attribute)
	{
		waitToAppear(element)
		def text =  WebUI.getAttribute(findTestObject(element), attribute)
		return text
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	@Given('I wait "(.*)" secs for the "(.*)"')
	def IWaitXSec(String time , String waitfor) {
		def secs = (time as Integer) * 1000
		Thread.sleep(secs)
	}


	@Given('I read data row "(.*)" from "(.*)"')
	def IReadRowDataFrom(String dataRow , String table) {
		testData = findTestData(table)
		testIndex = dataRow as Integer
		WebUI.comment(":) -> DataRow " + dataRow + " read from " + table)
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


	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	@Given("I open the browser on ")
	def IOpenTheBrowserOn() {
		confData = findTestData("Config/URLs")
		confIndex = 1
		IOpenTheBrowserOnUrl(getConfData("TestURL"))
		LoginTo(getConfData("user"),getConfData("passw"))
	}

	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


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
		else {	Thread.sleep(waitTime)
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
		WebUI.sendKeys(findTestObject(element),  Keys.chord(Keys.PAGE_UP))
		WebUI.scrollToElement(findTestObject(element), maxTimeOut)
		Thread.sleep(waitTime * 2)
		WebUI.comment(":) -> Element " + element + " has been focused")
	}


	@Given('I "(.*)" the alert')
	def IActionTheAlert(String action) {
		Thread.sleep(waitTime * 2)
		if(action.equals("accept"))
			WebUI.acceptAlert()
		else if(action.equals("dismiss"))
			WebUI.dismissAlert()
		Thread.sleep(waitTime * 2)
		WebUI.comment(":) -> Alert has been " + action + "ed")
	}


	@Given('I create "(.*)" string with "(.*)" into "(.*)"')
	def ICreateText(String textType, String name, String storeTo) {
		def payload = getConfData(textType)
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
			entry = getDate(entry,format)
		if(entry.contains("current_time"))
			entry = getTime(entry,format)

		ITypeIntoTheElement(entry,field)
		WebUI.comment(":) -> Entry " + entry + " typed into " + field)
	}


	@And('I type "(.*)" data into the "(.*)"')
	def ITypeDataIntoTheElement(String entry, String field) {
		def text = getTestData(entry)
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
		WebElement myElement =  WebUiCommonHelper.findWebElement(findTestObject(element),maxTimeOut)
		WebUI.executeJavaScript("arguments[0].click()", Arrays.asList(myElement))
	}


	@When('I select the "(.*)"')
	def ISelectTheElement(String element) {
		waitToAppear(element)
		WebUI.waitForElementClickable(findTestObject(element), waitTime)

		if(element.contains("tab") || element.contains("page") ){
			Thread.sleep(waitTime * 2)
			WebUI.sendKeys(findTestObject(element),  Keys.chord(Keys.PAGE_UP))
			Thread.sleep(waitTime)
			
		}
		if(element.contains("search"))
			Thread.sleep(waitTime)
		try{
			if(element.contains("button")||element.contains("checkbox")||element.contains("field"))
				IFocusOnTheElement(element)
			WebUI.click(findTestObject(element))}
		catch(Exception ex){
			Thread.sleep(waitTime)
			ISelectTheElementWithScript(element)
			Thread.sleep(waitTime)
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
		ISelectTheElement(dropdown)
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
		option = getTestData(option)
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


	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Checks
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	@Then('I should "(.*)" the "(.*)"')
	def IShouldSeeTheElement(String condition, String element) {
		if(!condition.equals("see") && !condition.equals("not see"))
		{ WebUI.comment(":( -> I can " + condition + " the " + element)
			assert condition == "see or not see"
		}
		if(condition.equals("see")){
			waitToAppear(element)
			WebUI.comment(":) -> I can " + condition + " the " + element)}
		else if(condition.equals("not see")){
			waitToDisAppear(element)
			WebUI.comment(":) -> I can " + condition + " the " + element)}

		WebUI.comment(":) -> I can " + condition + " the " + element)
	}


	@Then('I should "(.*)" the "(.*)" with "(.*)" data')
	def ISeeTheElementWithData(String condition,String element, String expected) {
		if(!condition.equals("see") && !condition.equals("not see"))
		{ WebUI.comment(":( -> I can " + condition + " the " + element)
			assert condition == "see or not see"
		}
		expected = getTestData(expected)
		if(condition.equals("see"))
		{waitToAppear(element)
			WebUI.verifyElementPresent(findTestObject(element + expected), 10)}
		else if(condition.equals("not see"))
		{
			WebUI.verifyElementNotPresent(findTestObject(element + expected), 10)}

		WebUI.comment(":) -> I can " + condition + " the " + element +  " " + expected)
	}


	@Then('The "(.*)" text should "(.*)" the "(.*)"')
	def TheElementTextShould(String element,String condition, String expected) {
		if(!condition.equals("equal") && !condition.equals("contain") && !condition.equals("not contain") && !condition.equals("not equal"))
		{ WebUI.comment(":( -> Element " + element + " text " + condition + " " + expected)
			assert condition == "equal or contain"
		}
		def ext = getExtension(expected)
		expected = getExpected(expected)
		expected = expected + ext.toString()

		def actual = getElementText(element)
		verifyTexts(actual,condition,expected)
		WebUI.comment(":) -> Element " + element + " text : "  +  actual + " " + condition + " " + expected)
	}


	@Then('The "(.*)" date time should "(.*)" the "(.*)" in form "(.*)"')
	def TheElementTextShouldDateTime(String element,String condition, String expected, String format) {
		if(!condition.equals("equal") && !condition.equals("contain") && !condition.equals("not contain") && !condition.equals("not equal"))
		{ WebUI.comment(":( -> Element " + element + " text " + condition + " " + expected)
			assert condition == "equal or contain"
		}
		def actual = getElementText(element)
		expected = getDate(expected, format)
		verifyTexts(actual,condition,expected)
		WebUI.comment(":) -> Element " + element + " text "  +  actual + " " + condition + " " + expected)
	}


	@Then('The "(.*)" text should "(.*)" the "(.*)" data')
	def TheElementTextShouldData(String element,String condition, String expected) {
		if(!condition.equals("equal") && !condition.equals("contain") && !condition.equals("not contain") && !condition.equals("not equal"))
		{ WebUI.comment(":( -> Element " + element + " text " + condition + " " + expected + " data")
			assert condition == "equal or contain"
		}

		def ext = getExtension(expected)
		expected = getExpected(expected)
		expected = getTestData(expected) + ext.toString()
		element = getSpecElement(element, expected)

		def actual
		if(element.contains("dropdown") && condition.contains("equal")){
			WebUI.verifyOptionSelectedByValue(findTestObject(element), expected, false, maxTimeOut)
			actual = expected}
		else actual = getElementText(element)
		verifyTexts(actual,condition,expected)

		WebUI.comment(":) -> Element " + element + " text "  +  actual + " " + condition + " " + expected + " data")
	}


	@Then('The "(.*)" "(.*)" attribute should "(.*)" the "(.*)"')
	def TheElementAttributeShould(String element, String attribute, String condition, String expected) {
		if(!condition.equals("equal") && !condition.equals("contain") && !condition.equals("not contain") && !condition.equals("not equal"))
		{ WebUI.comment(":( -> Element " + element + " attribute "  +  attribute + " " + condition + " " + expected)
			assert condition == "equal or contain"
		}

		def ext = getExtension(expected)
	getSpecElementected = getExpected(expected)
		element = getSpecElement(element, expected)

		def actual = getElementAttribute(element, attribute)
		verifyTexts(actual,condition,expected)
		WebUI.comment(":) -> Element " + element + " attribute "  +  actual + " " + condition + " " + expected)
	}


	@Then('The "(.*)" "(.*)" attribute should "(.*)" the "(.*)" data')
	def TheElementAttributeShouldData(String element, String attribute, String condition, String expected) {
		if(!condition.equals("equal") && !condition.equals("contain") && !condition.equals("not contain") && !condition.equals("not equal"))
		{ WebUI.comment(":( -> Element " + element + " attribute " + attribute + " = " +  attribute + " " + condition + " " + expected + " data")
			assert condition == "equal or contain"
		}

		def ext = getExtension(expected)
		expected = getTestData(expected) + ext.toString()
		element = getSpecElement(element, expected)

		def actual = getElementAttribute(element, attribute)
		verifyTexts(actual,condition,expected)
		WebUI.comment(":) -> Element " + element + " attribute " + attribute + " = " +  actual + " " + condition + " " + expected + " data")
	}


	@Given('The "(.*)" stored text should "(.*)" "(.*)" stored text')
	def TheTextShouldText(String operand1, String condition, String operand2) {
		if(!condition.equals("equal") && !condition.equals("contain") && !condition.equals("not contain") && !condition.equals("not equal"))
		{ WebUI.comment(":( -> -> Stored text " + operand1 + " " + condition + " " + operand2)
			assert condition == "equal or contain"
		}
		def actual = getTextStored(operand1)
		def expected = getTextStored(operand2)
		verifyTexts(actual,condition,expected)
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
		verifyDates(actual,condition,expected)
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
		verifyValues(actual,condition,expected)
		WebUI.comment(":) -> Stored value " + actual + " " + condition + " " + expected)
	}


	@Given('The "(.*)" text should "(.*)" the "(.*)" text')
	def TheElementTextShouldElementText(String element1, String condition, String element2) {
		if(!condition.equals("equal") && !condition.equals("contain") && !condition.equals("not contain") && !condition.equals("not equal"))
		{ WebUI.comment(":( -> Element1 " + element1 + " text " + condition + " Element2 " + element2  + " text ")
			assert condition == "equal or contain or not equal or not contain"
		}
		def actual = getElementText(element1)
		def expected = getElementText(element2)
		verifyTexts(actual,condition,expected)
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
		verifyDates(actual,condition,expected)
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
		verifyValues(actual,condition,expected)
		WebUI.comment(":) -> Element1 " + element1 + " value "  +  actual + " " + condition + " Element2 " + element2  + " value ")
	}


	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//NotCucumbers
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	def String getSpecElement(String element, String expected){
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


	def String getExtension(String expected){
		def ext = ""
		if(expected.contains("+"))
		{
			ext = expected.substring(expected.indexOf("+"), expected.length())
			expected = expected.replace(ext,"")
			ext = ext.replace("+","")
		}
		return ext
	}


	def String getExpected(String expected){
		def ext = ""
		if(expected.contains("+"))
		{
			ext = expected.substring(expected.indexOf("+"), expected.length())
			expected = expected.replace(ext,"")
			ext = ext.replace("+","")
		}
		return expected
	}


	def String getDate(String entry, String format)
	{
		def offset
		def ext = entry.replace("current_date", "")
		Date date = new Date()
		if(ext.contains("-"))
		{
			ext = ext.replace("-","")
			offset = ext as Integer
			date = date.minus(offset)
		}
		if(ext.contains("+"))
		{
			ext = ext.replace("+","")
			offset = ext as Integer
			date = date.plus(offset)
		}
		return date.format(format).toString()
	}

	def String getTimeStamp()
	{
		Date date = new Date()
		return date.format("hh:mm:ss - dd.MM.YYYY").toString()
	}


	def String getTime(String entry)
	{
		Date date = new Date()
		return date.format("hh:mm").toString()
	}


	def getTestData(String field)
	{
		def text = testData.internallyGetValue(field, testIndex-1)
		if(text.equals("")) text = ""
		return text
	}

	def getConfData(String field)
	{
		def text = confData.internallyGetValue(field, confIndex-1)
		if(text.equals("")) text = ""
		return text
	}


	def String getTextStored(String operand)
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


	def setTextStored(String value, String operand)
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


	def verifyDates(String op1, String condition, String op2)
	{
		def df = "dd.MM.yyyy"
		def cond = condition
		def date1 = new Date().parse(df, op1)
		def date2 = new Date().parse(df, op2)

		WebUI.comment("verifying dates -> " + date1 + "' " + cond + " " + date2)

		if(cond.equals("younger")){
			if(date1 < date2 || date1 == date2 )
			{
				assert date1 > date2
			}}
		else if(cond.equals("not younger")){
			if(date1 > date2 || date1 == date2 )
			{
				assert date1 < date2
			}}

		else if(cond.equals("older")){
			if (date1 > date2 || date1 == date2 )
			{
				assert date1 < date2
			}}
		else if(cond.equals("not older")){
			if(date1 < date2 || date1 == date2 )
			{
				assert date1 > date2
			}}

		else if(cond.contains("equal")){
			if (date1 < date2 || date1 > date2 )
			{
				assert date1 == date2
			}}
		else if(cond.equals("not equal")){
			if(date1 == date2 )
			{
				assert date1 < date2 || date1 > date2
			}}
	}


	def verifyValues(String actual, String condition, String expected)
	{
		def cond = condition
		def val1 = actual
		def val2 = expected

		WebUI.comment("verifying values -> '" + val1 + "'  " + cond + " '" + val2 + "'")

		if(cond.equals("below")){
			if(val1 < val2 || val1 == val2 )
			{
				assert val1 > val2
			}}
		else if(cond.equals("not below")){
			if(val1 > val2 || val1 == val2 )
			{
				assert val1 < val2
			}}

		else if(cond.equals("above")){
			if (val1 > val2 || val1 == val2 )
			{
				assert val1 < val2
			}}
		else if(cond.equals("not above")){
			if(val1 < val2 || val1 == val2 )
			{
				assert val1 > val2
			}}

		else if(cond.contains("equal")){
			if (val1 < val2 || val1 > val2 )
			{
				assert val1 == val2
			}}
		else if(cond.equals("not equal")){
			if(val1 == val2 )
			{
				assert val1 != val2
			}}
	}


	def verifyTexts(String actual, String condition, String expected)
	{
		def cond = condition
		def val1 = actual
		def val2 = expected

		WebUI.comment("verifying texts -> '" + val1 + "' " + cond + " '" + val2 + "'")

		if(cond.contains("equal")){
			if (val1 != val2)
			{
				assert val1 == val2
			}}
		else if(cond.equals("not equal")){
			if (val1 == val2)
			{
				assert val1 != val2
			}}

		else if(cond.equals("contain")){
			if (!val1.contains(val2))
			{
				assert val1.contains(val2)
			}}
		else if(cond.equals("not contain")){
			if (val1.contains(val2))
			{
				assert !val1.contains(val2)
			}}
	}





}