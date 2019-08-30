package steps

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.ExcelData
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.CheckpointFactory
import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository

import cucumber.api.Scenario
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
import com.kms.katalon.core.testdata.ExcelData as ExcelData

import java.nio.file.Files;
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import com.kms.katalon.core.configuration.RunConfiguration

import steps.WebSteps

public class CommonSteps {

	Scenario myScenario

	public static ExcelData testData
	public static ExcelData confData

	public static int testIndex = 0
	public static int confIndex = 0

	public static String evironment = ""

	@Before
	public void setUp() {
		WebUI.comment("PEGA UI Automated Tests - " + getTimeStamp())
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
				Files.copy(source,dest,StandardCopyOption.REPLACE_EXISTING)
			}
			catch(Exception ex){
				WebUI.comment(ex.toString())
			}
		}
		WebUI.closeBrowser()
	}

	public void zipIt(String inputDir, String zipFileName){
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



	public static  String getExtension(String expected){
		def ext = ""
		if(expected.contains("+"))
		{
			ext = expected.substring(expected.indexOf("+"), expected.length())
			expected = expected.replace(ext,"")
			ext = ext.replace("+","")
		}
		return ext
	}


	public static  String getExpected(String expected){
		def ext = ""
		if(expected.contains("+"))
		{
			ext = expected.substring(expected.indexOf("+"), expected.length())
			expected = expected.replace(ext,"")
			ext = ext.replace("+","")
		}
		return expected
	}


	public static String getDate(String entry, String format)
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

	public static String getTimeStamp()
	{
		Date date = new Date()
		return date.format("hh:mm:ss - dd.MM.YYYY").toString()
	}


	public static String getTime(String entry)
	{
		Date date = new Date()
		return date.format("hh:mm").toString()
	}


	public static String getTestData(String field)
	{
		def text = testData.internallyGetValue(field, testIndex-1)
		if(text.equals("")) text = ""
		return text
	}

	public static String getConfData(String field)
	{
		def text = confData.internallyGetValue(field, confIndex-1)
		if(text.equals("")) text = ""
		return text
	}


	public static void verifyDates(String op1, String condition, String op2)
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


	public static void verifyValues(String actual, String condition, String expected)
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


	public static void verifyTexts(String actual, String condition, String expected)
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