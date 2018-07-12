package com.MockarooDataValidation;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class MackarooDataValidation {
	WebDriver driver;
	ArrayList<String> eachLine = new ArrayList<String>();
	ArrayList<String> cities = new ArrayList<String>();
	ArrayList<String> countries = new ArrayList<String>();
	Set<String> allCities = new HashSet<String>();
	Set<String> allCountries = new HashSet<String>();

	@BeforeClass
	public void setUp() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		driver.get("https://mockaroo.com/");

	}

	@AfterClass
	public void down() throws InterruptedException {
		Thread.sleep(3000);
		driver.quit();
	}

	@Test(priority = 1)
	public void checkTitle() {
		String ActualTitle = driver.getTitle();
		// System.out.println(ActualTitle);
		String ExpectedTitle = "Mockaroo - Random Data Generator and API Mocking Tool | JSON / CSV / SQL / Excel";
		assertEquals(ActualTitle, ExpectedTitle);

		boolean isDisplayed = driver.findElement(By.xpath("//div[.='mockaroo']")).isDisplayed()
				&& driver.findElement(By.xpath("//div[.='realistic data generator']")).isDisplayed();
		assertTrue(isDisplayed);

	}

	@Test(priority = 2)
	public void removeExistingRows() throws InterruptedException {
		List<WebElement> rows = driver.findElements(By.xpath("//a[@class='close remove-field remove_nested_fields']"));
		for (WebElement del : rows) {
			del.click();
		}
		Thread.sleep(2000);
		// String checkheads="";
		// List<WebElement> heads =
		// driver.findElements(By.xpath("//div[starts-with(@class,'column
		// column-header')]"));
		// for(WebElement head : heads) {
		// checkheads = checkheads.concat(head.getText());
		// }
		// assertEquals(checkheads, "Field NameTypeOptions");

		List<WebElement> heads = driver.findElements(By.xpath("//div[starts-with(@class,'column column-header')]"));
		for (WebElement head : heads) {
			assertTrue(head.isDisplayed());
		}

	}

	@Test(priority = 3)
	public void addAnotherFieldButton() {
		assertTrue(driver.findElement(By.xpath("//a[@class='btn btn-default add-column-btn add_nested_fields']"))
				.isDisplayed());
	}

	@Test(priority = 4)
	public void defaultNumber() {
		assertEquals(driver.findElement(By.xpath("//input[@id='num_rows']")).getAttribute("value"), "1000");
	}

	@Test(priority = 5)
	public void formatCSV() {
		boolean select = driver.findElement(By.xpath("//option[@value='csv']")).isDisplayed();
		if (!select) {
			driver.findElement(By.xpath("//option[@value='csv']")).click();
		}
		assertTrue(driver.findElement(By.xpath("//option[@value='csv']")).isDisplayed());
	}

	@Test(priority = 6)
	public void LineEndingUnix() {
		assertEquals(driver.findElement(By.xpath("//option[@value='unix']")).getText(), "Unix (LF)");
	}

	@Test(priority = 7)
	public void isChecked() {
		assertTrue(driver.findElement(By.xpath("//input[@id='schema_include_header']")).isSelected());
		assertTrue(!driver.findElement(By.xpath("//input[@id='schema_bom']")).isSelected());
	}

	@Test(priority = 8)
	public void TypeDialogBox() throws InterruptedException {
		driver.findElement(By.xpath("//a[@class='btn btn-default add-column-btn add_nested_fields']")).click();
		driver.findElement(By.xpath("(//input[@placeholder='enter name...'])[7]")).sendKeys("City");
		driver.findElement(By.xpath("(//input[@class='btn btn-default'])[7]")).click();
		Thread.sleep(1000);
		assertEquals(driver.findElement(By.xpath("(//h3[@class='modal-title'])[1]")).getText().trim(), "Choose a Type");

	}

	@Test(priority = 9)
	public void Operations() throws InterruptedException {
		driver.findElement(By.xpath("//input[@id='type_search_field']")).sendKeys("City");
		Thread.sleep(500);
		driver.findElement(By.xpath("//div[@class='examples']")).click();
		Thread.sleep(500);
		driver.findElement(By.xpath("//a[@class='btn btn-default add-column-btn add_nested_fields']")).click();
		WebElement secondField = driver.findElement(By.xpath(
				"/html[1]/body[1]/div[3]/div[1]/div[2]/form[1]/div[2]/div[3]/div[2]/div[1]/div[8]/div[2]/input[1]"));
		secondField.clear();
		secondField.sendKeys("Country");
		driver.findElement(By.xpath("//div[@id='fields']//div[8]//div[3]//input[3]")).click();
		Thread.sleep(500);
		driver.findElement(By.xpath("//input[@id='type_search_field']")).sendKeys("Country");
		Thread.sleep(500);
		driver.findElement(By.xpath("//div[@id='type_list']//div[1]//div[3]")).click();
		Thread.sleep(500);
		driver.findElement(By.xpath("//button[@id='download']")).click();
		// csv data file downloaded
		Thread.sleep(500);
	}
	// step17 Open the downloaded file using BufferedReader.

	@Test(priority = 10)

	public void bufferingFile() throws IOException {
		FileReader fr = new FileReader("/Users/Asus/Downloads/MOCK_DATA.csv");
		BufferedReader br = new BufferedReader(fr);
		String firstLine = br.readLine();
		String actual = br.readLine();

		// 18. Assert that first row is matching with Field names that we selected.
		Assert.assertTrue(firstLine.equalsIgnoreCase("City,Country"));

		// loading all lines to allList arrayList

		while (actual != null) {
			eachLine.add(actual);
			actual = br.readLine();
		}
		// 19. Assert that there are 1000 records
		System.out.println("number of lines in file : " + eachLine.size());
		Assert.assertEquals(eachLine.size(), 1000);

		// 20. From file add all Cities to Cities ArrayList
		int numberOfUnCity = 0;
		int numberOfUnCountry = 0;
		for (String eachCities : eachLine) {
			cities.add(eachCities.substring(0, eachCities.indexOf(",")));
			if (!allCities.contains(eachCities))
				numberOfUnCity++;
			allCities.add(eachCities);
		}

		// 21. Add all countries to Countries ArrayList
		for (String eachCountries : eachLine) {
			countries.add(eachCountries.substring(eachCountries.indexOf(",") + 1));
			if (!allCountries.contains(eachCountries))
				numberOfUnCountry++;
			allCountries.add(eachCountries);
		}
		// 22. Sort all cities and find the city with the longest name and shortest name
		Collections.sort(cities);
		Collections.sort(countries);

		// int count =0;
		// for (int i = 0; i < cities.size(); i++) {
		// if(cities.get(i).length()==3) {
		// count ++;
		// }
		// }
		System.out.println(getShortedList(cities));

		System.out.println(getLongestList(cities));

		System.out.println(getShortedList(countries));

		System.out.println(getLongestList(countries));

		// 23. In Countries ArrayList, find how many times each Country is mentioned.

		getNumberOfDublicates(countries);

		// 24. From file add all Cities to citiesSet HashSet
		// line 178
		// 25. Count how many unique cities are in Cities ArrayList and assert that
		// it is matching with the count of citiesSet HashSet
		assertEquals(numberOfUnCity, allCities.size());

		// 26. Add all Countries to countrySet HashSet
		// line 183
		// 27. Count how many unique cities are in Countries ArrayList and assert that
		// it is matching with the count of countrySet HashSet.
		assertEquals(numberOfUnCountry, allCountries.size());

	}

	// ===============================SomeMethods=============================================//

	private String getShortedList(ArrayList<String> cities) {
		int shortestLength;
		String shortestCityName = cities.get(0);
		List<String> shortestCityNames = new ArrayList<String>();
		for (int i = 0; i < cities.size(); i++) {
			if (cities.get(i).length() < shortestCityName.length()) {
				shortestCityName = cities.get(i);
			}
		}
		shortestLength = shortestCityName.length();
		for (String eachCity : cities) {
			if (shortestLength == eachCity.length()) {
				if (!shortestCityNames.contains(eachCity))
					shortestCityNames.add(eachCity);
			}
		}

		return shortestCityNames.toString().trim();

	}

	private String getLongestList(ArrayList<String> cities) {
		String longestCityName = cities.get(0);
		List<String> longestCityNames = new ArrayList<String>();
		int longestLength;
		for (int i = 0; i < cities.size(); i++) {
			if (cities.get(i).length() > longestCityName.length()) {
				longestCityName = cities.get(i);
			}
		}
		longestLength = longestCityName.length();
		for (String eachCity : cities) {
			if (longestLength == eachCity.length()) {
				if (!longestCityNames.contains(eachCity))
					longestCityNames.add(eachCity);
			}
		}

		return longestCityNames.toString().trim();

	}

	private void getNumberOfDublicates(ArrayList<String> countries) {
		Map<String, Integer> UniqueCounties = new HashMap<String, Integer>();
		String toReturn = "";
		for (String eachCountry : countries) {
			String cntry = eachCountry;
			if (!UniqueCounties.containsKey(cntry)) {
				UniqueCounties.put(cntry, 1);
			} else {
				UniqueCounties.put(cntry, UniqueCounties.get(cntry) + 1);
			}
		}

		for (String key : UniqueCounties.keySet()) {
			System.out.println((key + "-" + UniqueCounties.get(key)).toString());

		}

	}

}
