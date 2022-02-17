package uiautomation;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import uiautomation.objects.User;

public class TestClass {

	WebDriver driver;
	WebDriverWait wait;

	public final String url = "https://the-internet.herokuapp.com/";
	public final String chromeDriverPath = System.getProperty("user.dir") + "\\src\\test\\resources\\chromedriver.exe";

	String sucessMessage = "You logged into a secure area!";
	String failureMessageUsername = "Your username is invalid!";
	String failureMessagePassword = "Your password is invalid!";

	@BeforeClass
	public void setUp() {
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		driver.get(url);
		driver.manage().window().maximize();
		System.out.println(System.getProperty("user.dir") + "\\src\\test\\resources");

	}

	@AfterClass
	public void tearDown() {
		driver.close();
	}

	@BeforeMethod
	public void beforeMethod() {
		driver.navigate().to(url);
	}

	@Test
	public void testInputs() {
		driver.findElement(By.xpath("//li/a[@href='/inputs']")).click();
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("div[class='example']")));
		Assert.assertEquals(driver.findElement(By.xpath("//input[@type='number']")).getAttribute("value"), "",
				"Text box is empty when page is loaded");
		driver.findElement(By.xpath("//input[@type='number']")).sendKeys("1234");
		Assert.assertEquals(driver.findElement(By.xpath("//input[@type='number']")).getAttribute("value"), "1234",
				"Entered Text is present");

	}

	@Test
	public void testCheckBoxes() {
		driver.findElement(By.xpath("//a[@href='/checkboxes']")).click();
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("div[class='example']")));
		WebElement checkbox1 = driver.findElement(By.xpath("//div/div/form/input[1]"));
		WebElement checkbox2 = driver.findElement(By.xpath("//div/div/form/input[2]"));

		Assert.assertFalse(checkbox1.isSelected());
		Assert.assertTrue(checkbox2.isSelected());

		if (checkbox1.isEnabled() && checkbox1.isSelected()) {
			checkbox1.click();
		} else if (checkbox1.isEnabled()) {
			checkbox1.click();
		}
		if (checkbox2.isEnabled() && checkbox2.isSelected()) {
			checkbox2.click();
		} else if (checkbox2.isEnabled()) {
			checkbox2.click();
		}
		Assert.assertTrue(checkbox1.isSelected());
		Assert.assertFalse(checkbox2.isSelected());
	}

	@Test
	public void testDropdown() {
		driver.findElement(By.cssSelector("a[href='/dropdown']")).click();
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("dropdown"))));
		WebElement webEleDropDown = driver.findElement(By.id("dropdown"));
		Select select = new Select(webEleDropDown);
		List<WebElement> options = select.getOptions();
		Assert.assertTrue(options.size() > 0);
		select.selectByVisibleText("Option 2");
		Assert.assertEquals(select.getFirstSelectedOption().getText(), "Option 2");

	}

	@Test
	public void testTables() {
		driver.findElement(By.cssSelector("a[href='/tables']")).click();
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//table[@id='table1']")));
		List<WebElement> tableHeaders = driver.findElements(By.xpath("//table[@id='table1']/thead/tr"));
		Assert.assertEquals(tableHeaders.size(), 1);
		List<WebElement> tableRows = driver.findElements(By.xpath("//table[@id='table1']/tbody/tr"));
		int rowSize = tableRows.size();
		Assert.assertTrue(rowSize >= 1);
		System.out.println("Total Table Rows : " + tableRows.size());
		for (int i = 0; i < rowSize; i++) {
			WebElement row = tableRows.get(i);
			String balanceDue = row.findElement(By.xpath("//tr[" + (i + 1) + "]/td[4]")).getText();
			Assert.assertTrue(balanceDue.startsWith("$"));
		}
	}

	@Test
	@Parameters({ "username", "password" })
	public void testFormAuthentication(String username, String password) {
		driver.findElement(By.cssSelector("a[href='/login']")).click();
		driver.findElement(By.id("username")).sendKeys(username);
		driver.findElement(By.id("password")).sendKeys(password);
		driver.findElement(By.cssSelector("button[class='radius'][type='submit']")).submit();
		WebElement loginMsg = driver.findElement(By.id("flash"));
		WebElement logoutBtn = driver.findElement(By.xpath("//a[@class='button secondary radius'][@href='/logout']"));
		wait.until(ExpectedConditions.visibilityOf(logoutBtn));
		Assert.assertTrue(loginMsg.getText().contains("You logged into a secure area!"));
		Assert.assertTrue(logoutBtn.isEnabled());
		if (logoutBtn.isEnabled()) {
			logoutBtn.click();
		}

		wait.until(ExpectedConditions
				.visibilityOfAllElements(driver.findElement(By.cssSelector("button[class='radius'][type='submit']"))));
		WebElement logoutMsg = driver.findElement(By.id("flash"));
		Assert.assertTrue(logoutMsg.getText().contains("You logged out of the secure area!"));
	}

	@Test(dataProvider = "form_authentication")
	public void testFormAuthentication2(User user) {

		driver.findElement(By.cssSelector("a[href='/login']")).click();
		driver.findElement(By.id("username")).sendKeys(user.getUserName());
		driver.findElement(By.id("password")).sendKeys(user.getPassword());
		driver.findElement(By.cssSelector("button[class='radius'][type='submit']")).submit();
		WebElement loginMsg = driver.findElement(By.id("flash"));
		wait.until(ExpectedConditions.visibilityOf(loginMsg));
		if (user.isValid()) {
			Assert.assertTrue(loginMsg.getText().contains(user.getExpectedMessage()));
			driver.navigate().back();
		} else {
			Assert.assertTrue(loginMsg.getText().contains(user.getExpectedMessage()));
			driver.findElement(By.id("username")).clear();
			driver.findElement(By.id("password")).clear();
		}

	}

	@org.testng.annotations.DataProvider(name = "form_authentication")
	public Object[][] dataProvider() {
		return new Object[][] { { new User("tomsmith", "SuperSecretPassword!", true, sucessMessage) },
				{ new User("tomsmith", "WrongPassword1", false, failureMessagePassword) },
				{ new User("WrongUsername", "SuperSecretPassword!", false, failureMessageUsername) } };
	}

}
