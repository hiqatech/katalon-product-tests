package runners

import org.junit.runner.RunWith

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber

@RunWith(Cucumber.class)
@CucumberOptions(features="Include/features/Regression", glue="", plugin = ["pretty",
	"html:Reports/CucumberReport",
	"json:Reports/CucumberReport/cucumber.json"])
public class RegressionRunner {
}


