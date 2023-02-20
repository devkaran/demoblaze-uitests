package com.demoblaze.ui.testng_listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import lombok.extern.log4j.Log4j2;
import org.testng.IClassListener;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ISuiteResult;
import org.testng.ITestClass;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

@Log4j2
public class ExtentTestNGListener implements IReporter, ITestListener, ISuiteListener, IClassListener {
    private ExtentReports extentReports;
    private static final String FILE_NAME = "target" + File.separator + "testNgReport" + File.separator + "AutomationReportResultsToModify.html";

    @Override
    public void generateReport(List<XmlSuite> list, List<ISuite> suites, String outdir) {
        extentReports = reportInit();
        for (ISuite suite : suites) {
            Map<String, ISuiteResult> tests = suite.getResults();
            for (ISuiteResult testResult : tests.values()) {
                ITestContext testContext = testResult.getTestContext();
                IResultMap skipped = testContext.getSkippedTests();
                IResultMap passed = testContext.getPassedTests();
                IResultMap failed = testContext.getFailedTests();

//                if something is common in passed and failed,  that means retryed methods are also added in failed, so remove those
                Set<ITestResult> testPassedButAddedInFailed = new HashSet<>(failed.getAllResults());
                testPassedButAddedInFailed.retainAll(passed.getAllResults());
                testPassedButAddedInFailed.stream().forEach(failed::removeResult);

                buildTestNodes(passed, Status.PASS);
                buildTestNodes(failed, Status.FAIL);
                buildTestNodes(skipped, Status.SKIP);
            }
        }

        extentReports.flush();
    }

    private void buildTestNodes(IResultMap tests, Status status) {
        ExtentTest test;
        if (tests.size() > 0) {
            for (ITestResult result : tests.getAllResults()) {

                String description = result.getMethod().getDescription() + "<br />Parameters : " + Arrays.toString(result.getParameters());
                test = extentReports.createTest(result.getMethod().getMethodName(), description);

                test.getModel().setStartTime(getTime(result.getStartMillis()));
                test.getModel().setEndTime(getTime(result.getEndMillis()));

                for (String group : result.getMethod().getGroups()) {
                    test.assignCategory(group);
                }

                String message = "Test " + status.toString().toLowerCase() + "ed";

                if (result.getThrowable() != null)
                    message = getStackTrace(result);

                test.log(status, message);

                extentReports.flush();
            }
        }
    }

    public ExtentReports reportInit() {
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter(FILE_NAME);
        htmlReporter.config().setTheme(Theme.DARK);
        htmlReporter.config().setCss(".green-text { color: #008000 !important; }");
        htmlReporter.config().setDocumentTitle("Automation Report");
        htmlReporter.config().setEncoding("utf-8");
        htmlReporter.config().setCss("#008000");
        htmlReporter.config().setReportName("Claims Api Tests Report");
        htmlReporter.config().setTimelineEnabled(false);
        htmlReporter
                .config()
                .setJs(
                        "document.querySelector('meta[name=\"description\"]').setAttribute(\"content\", \"Test Automation Report @Navi\");");
        extentReports = new ExtentReports();
        extentReports.attachReporter(htmlReporter);
        extentReports.setReportUsesManualConfiguration(true);
        return extentReports;
    }

    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.SHORT_IDS.get("IST")));
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }

    private String getStackTrace(ITestResult testResult) {
        String stackTrace = "";
        stackTrace += testResult.getThrowable().toString() + "<br />";
        for (StackTraceElement stackTraceElement : testResult.getThrowable().getStackTrace()) {
            stackTrace = stackTrace.concat(stackTraceElement.toString() + " <br /> ");
        }
        return stackTrace;
    }


    //    logging test names in the logs
    @Override
    public void onStart(ISuite suite) {
        log.info("*************************************** Start of suite : {} ***************************************", suite.getName());
    }

    @Override
    public void onFinish(ISuite suite) {
        log.info("*************************************** End of suite : {} ***************************************", suite.getName());
    }

    @Override
    public void onBeforeClass(ITestClass testClass) {
        log.info("*************************************** Start of test class : {} ***************************************", testClass.getName());
    }

    @Override
    public void onAfterClass(ITestClass testClass) {
        log.info("*************************************** End of test class : {} ***************************************", testClass.getName());
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {
        log.info("*************************************** Start of test : {} ***************************************", iTestResult.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        log.info("*************************************** Test Successful - End of test : {} ***************************************", iTestResult.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        log.info("*************************************** Test Failed - End of test : {} ***************************************", iTestResult.getMethod().getMethodName());
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        log.info("*************************************** Test Skipped - End of test : {} ***************************************", iTestResult.getMethod().getMethodName());
    }
}