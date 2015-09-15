package es.udc.webdriver;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebDriverTest {

	@Test
	public void testLogin() {

		WebDriver driver = new FirefoxDriver();
		driver.get("http://localhost:8080/subasta-app/es");

		driver.findElement(By.id("authenticate")).click();
		driver.findElement(By.id("loginName")).sendKeys("user");
		driver.findElement(By.id("password")).sendKeys("123");
		driver.findElement(By.id("loginBtn")).click();
		assertTrue(driver.findElement(By.tagName("body")).getText()
				.contains("Usuario"));

	}

	@Test
	public void testBid() {

		WebDriver driver = new FirefoxDriver();
		driver.get("http://localhost:8080/subasta-app/es/search/findproducts");
		driver.findElement(By.id("searchBtn")).click();
		driver.findElement(By.linkText("Producto prueba 1")).click();
		driver.findElement(By.id("bidBtn")).click();

		driver.findElement(By.id("loginName")).sendKeys("user2");
		driver.findElement(By.id("password")).sendKeys("123");
		driver.findElement(By.id("loginBtn")).click();
		driver.findElement(By.id("bidBtn")).click();

		driver.findElement(By.id("amount")).sendKeys("6000");
		driver.findElement(By.id("bidBtn")).click();

		driver.get("http://localhost:8080/subasta-app/es/search/findmybids/0");
		assertTrue(driver.findElement(By.tagName("table")).getText()
						.contains("Producto prueba 1"));
	}

}