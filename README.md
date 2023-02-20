# DemoBlaze Test Automation Framework

This is a Java-based test automation framework for the [DemoBlaze website](https://www.demoblaze.com/index.html). It uses TestNG as the test framework and Playwright to control the browser.
<br>
Uses extent report testng listener.
<br>
[TestCases can be found here](TestCases.md)

## Project Structure
The project is organized into the following directories:

* `core`: contains the TestNG listener classes.
* `pages`: contains page object classes.
* `tests`: contains the actual test classes.
  * `java`: contains the test classes. Pass the browser configuration in the data provider [TestDataProvider.java](tests%2Fsrc%2Ftest%2Fjava%2Fcom%2Fdemoblaze%2Fui%2FTestDataProvider.java).
  * `resources`: contains resources such as test data and configuration files.
  * `suites`: contains TestNG XML suite files that group test classes together.
  * `target`: contains compiled classes and reports generated during the build process.

## How to Run the Tests
1. Clone the repository to your local machine. 
2. Navigate to the project root directory. 
3. Run `mvn test` to execute all tests.

## How to Generate Test Reports
After running the tests, the test reports can be found in the `target/testNgReport` directory. Open the `AutomationReportResultsToModify.html` file in a web browser to view the report.
