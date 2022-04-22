import config.ServerConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;


public class MainTest {
	private WebDriver driver;
	private Wait<WebDriver> wait;
	private static final Logger logger = LogManager.getLogger(MainTest.class);
	private final ServerConfig serverConfig = ConfigFactory.create(ServerConfig.class);
	private JavascriptExecutor js;

	@BeforeClass
	public static void startUp() {
		WebDriverManager.chromedriver().setup();
	}

	@After
	public void end() {
		if (driver != null)
			driver.quit();
	}

	private void initDriver(String chromeMode) {
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments(chromeMode);
		driver = new ChromeDriver(chromeOptions);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		logger.info("Драйвер поднят в режиме = {}", chromeMode);

		wait = new WebDriverWait(driver, Duration.ofSeconds(5), Duration.ofSeconds(1));
		js = (JavascriptExecutor) driver;
	}

	//Перейти на https://otus.ru + авторизация
	private void authNew() {
		//Перейти на https://otus.ru
		driver.get(serverConfig.otusUrl());

		//Авторизоваться на сайте
		driver.findElement(By.xpath("//span[@class='header2__auth-reg']")).click();

		driver.findElement(By.xpath("//div[@class='new-log-reg__body']//input[@name='email']")).sendKeys(serverConfig.email());
		driver.findElement(By.xpath("//div[@class='new-log-reg__body']//input[@name='password']")).sendKeys(serverConfig.password());
		driver.findElement(By.xpath("//div[@class='new-log-reg__body']//button")).click();

		String newUserName = driver.findElement(By.xpath("//p[contains(@class,'header2-menu__item-text__username')]")).getText();

		Assert.assertEquals("Пользователь не верен", "toyey", newUserName);
		logger.info("Пользователь авторизован");
	}

	private void openPers() {
		WebElement authMenu = driver.findElement(By.xpath("//p[contains(@class,'header2-menu__item-text__username')]"));
		Actions actions = new Actions(driver);
		actions.moveToElement(authMenu).build().perform();
		WebElement profile = driver.findElement(By.xpath("//b[contains(@class,'header2-menu__dropdown-text_name')]"));
		actions.moveToElement(profile).build().perform();
		driver.findElement(By.xpath("//b[contains(@class,'header2-menu__dropdown-text_name')]")).click();
		logger.info("Открыта страница Персональные данные");
	}

	protected void moveToElement(WebElement element) {
		/*Actions actions = new Actions(driver);
		actions.moveToElement(element).build().perform();*/
		js.executeScript("arguments[0].scrollIntoView(false)", element);
	}

	private void waitUntilTextPresents(WebElement element, String text) {
		wait.until(ExpectedConditions.textToBePresentInElement(element, text));
	}

	private void waitUntilAttrPresents(WebElement save, String attr, String value) {
		wait.until(ExpectedConditions.attributeToBe(save, attr, value));
	}

	@Test
	public void testPersPage() {
		// Открыть Chrome в режиме полного экрана
		initDriver("start-maximized");

		//Перейти на https://otus.ru
		authNew();

		logger.info("Начали заполнять персональные данные");

		//В разделе "О себе" заполнить все поля "Личные данные" и добавить не менее двух контактов
		openPers();

		//Заполнение Персональных данных
		fillPersonalData();

		//Заполнение данных о местоположении
		fillCountryData();

		//Добавление двух контактов
		addContacts();

		//Нажать сохранить
		saveData();
		//Закрыть страницу
		driver.close();

		checkData();
	}

	private void saveData() {
		WebElement save = driver.findElement(By.xpath("//button[@title='Сохранить и заполнить позже']"));
		moveToElement(save);
		save.click();

		save = driver.findElement(By.xpath("//span[contains(@class,'messages')]"));
		waitUntilTextPresents(save, "Данные успешно сохранены");

		logger.info("Данные сохранены");
	}

	private void checkData() {
		// Открыть https://otus.ru в "чистом браузере"
		initDriver("start-maximized");
		authNew();
		openPers();
		//Проверить, что в разделе "О себе" отображаются указанные ранее данные
		String getCountry = driver.findElement(By.xpath("//input[@name='country']/following-sibling::div")).getText();
		String getCity = driver.findElement(By.xpath("//input[@name='city']/following-sibling::div")).getText();
		String getEngLevel = driver.findElement(By.xpath("//input[@name='english_level']/following-sibling::div")).getText();

		WebElement tgElement = driver.findElement(By.xpath("//input[@type='text' and @name='contact-0-value']"));
		moveToElement(tgElement);
		String getTg = tgElement.getDomAttribute("value");

		WebElement vkElement = driver.findElement(By.xpath("//input[@type='text' and @name='contact-1-value']"));
		moveToElement(vkElement);
		String getVk = vkElement.getDomAttribute("value");

		Assert.assertEquals("Город не корректен", "Россия", getCountry);
		Assert.assertEquals("Город не корректен", "Москва", getCity);
		Assert.assertEquals("Уровень языка не корректен", "Начальный уровень (Beginner)", getEngLevel);
		Assert.assertEquals("Контакт1 не корректен", "https://t.me/test", getTg);
		Assert.assertEquals("Контакт2 не корректен", "https://vk.com/test", getVk);
		logger.info("Данные отображаются корректно");
	}

	private void deleteContact(WebElement contact) {
		WebElement deleteButtonDiv = contact.findElement(By.xpath(".//div[contains(@class, 'container__col container__col_12 container__col_md-0')]"));
		deleteButtonDiv.findElement(By.xpath(".//button[contains(@class, 'js-formset-delete')]")).click();
	}

	private void setContact(int cntNumber, String title, String text) {
		String xpath = String.format("//input[@name='contact-%1d-service']/following::div", cntNumber);

		WebElement contact = driver.findElement(By.xpath(xpath));
		contact.click();

		xpath = String.format("//div[(contains(@class,'lk-cv-block__select-options') or contains(@class,'lk-cv-block__select-options_left')) and not(contains(@class,'hide'))]//button[@title='%1s']", title);

		WebElement contactType = driver.findElement(By.xpath(xpath));
		contactType.click();

		xpath = String.format("//input[@type='text'][@name='contact-%1d-value']", cntNumber);

		WebElement setContact = driver.findElement(By.xpath(xpath));
		setContact.clear();
		setContact.sendKeys(text);

		waitUntilAttrPresents(setContact, "value", text);
	}

	private void addContacts() {
		//ищем все контакты,а точнее их верхние div формы
		List<WebElement> existingContacts = driver.findElements(By.xpath("//div[contains(@class,'js-formset-row')]"));
		//надо запоминать сколько было контактов,т.к. если создавать новые, то он продолжает нумерацию на форме
		int contactCount = existingContacts.size();

		//если контакты нашлись,то удаляем их
		if (CollectionUtils.isNotEmpty(existingContacts)) {
			for (WebElement existingContact : existingContacts) {
				deleteContact(existingContact);
			}
		}

		logger.info("Добавляем 1 контакт");
		WebElement addButton = driver.findElement(By.xpath("//button[contains(text(),'Добавить')]"));
		addButton.click();

		setContact(contactCount++, "VK", serverConfig.vk());

		logger.info("Добавляем 2 контакт");

		addButton = driver.findElement(By.xpath("//button[contains(text(),'Добавить')]"));
		addButton.click();
		setContact(contactCount++, "Тelegram", serverConfig.tg());
	}

	private void fillCountryData() {
		WebElement country = driver.findElement(By.xpath("//input[@name='country']/following-sibling::div"));//Страна
		country.click();
		WebElement countryList = driver.findElement(By.xpath("//div[contains(@class,'lk-cv-block__select-scroll_country')]/button[2]"));
		countryList.click();

		WebElement city = driver.findElement(By.xpath("//input[@name='city']/following-sibling::div"));//Город
		waitUntilTextPresents(city, "Город");
		city.click();
		WebElement cityList = driver.findElement(By.xpath("//div[contains(@class,'lk-cv-block__select-scroll_city')]/button[2]"));
		cityList.click();

		WebElement engLevel = driver.findElement(By.xpath("//input[@name='english_level']/following-sibling::div")); //Уровень английского

		engLevel.click();
		WebElement engLevelList = driver.findElement(By.xpath("//div[contains(@class,'lk-cv-block__select-scroll') and not(contains(@class,'country')) and not(contains(@class,'city'))]/button[2]"));
		engLevelList.click();
	}

	private void fillPersonalData() {
		WebElement addName = driver.findElement(By.xpath("//input[@type='text'][@name='fname']"));//Поле Имя
		addName.clear();
		addName.click();
		addName.sendKeys(serverConfig.name());

		WebElement addEnName = driver.findElement(By.xpath("//input[@type='text'][@name='fname_latin']"));//Поле Имя (латиницей)
		addEnName.clear();
		addEnName.click();
		addEnName.sendKeys(serverConfig.name());

		WebElement addSurname = driver.findElement(By.xpath("//input[@type='text'][@name='lname']"));//Поле Фамилия
		addSurname.clear();
		addSurname.click();
		addSurname.sendKeys(serverConfig.surname());

		WebElement addEnSurname = driver.findElement(By.xpath("//input[@type='text'][@name='lname_latin']"));//Поле Фамилия (латиницей)
		addEnSurname.clear();
		addEnSurname.click();
		addEnSurname.sendKeys(serverConfig.surname());

		WebElement addNick = driver.findElement(By.xpath("//input[@type='text'][@name='blog_name']"));//Имя в блоге
		addNick.clear();
		addNick.click();
		addNick.sendKeys(serverConfig.name());

		WebElement addBirth = driver.findElement(By.xpath("//input[@name='date_of_birth']"));//Дата рождения
		addBirth.clear();
		addBirth.click();
		addBirth.sendKeys(serverConfig.birth());

		driver.findElement(By.xpath("//label[contains(text(),'Дата рождения')]")).click();
	}

}








