import config.ServerConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;
import java.util.List;
import java.util.Set;


public class MainTest {
    private WebDriver driver;
    private static final Logger logger = LogManager.getLogger(MainTest.class);
    private final ServerConfig serverConfig = ConfigFactory.create(ServerConfig.class);
    private JavascriptExecutor js;


    @BeforeClass
    public static void startUp() {
        WebDriverManager.chromedriver().setup();
    }

//    @After
//    public void end() {
//        if (driver != null)
//            driver.quit();
//    }

    private void initDriver(String chromeMode) {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments(chromeMode);
        driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        logger.info("Драйвер поднят в режиме = {}", chromeMode);
    }
    //Перейти на https://otus.ru + авторизация
    private void AuthNew(){
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

    private void OpenPers(){
        WebElement authMenu = driver.findElement(By.xpath("//p[contains(@class,'header2-menu__item-text__username')]"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Actions actions = new Actions(driver);
        actions.moveToElement(authMenu).build().perform();
        WebElement profile = driver.findElement(By.xpath("//b[contains(@class,'header2-menu__dropdown-text_name')]"));
        actions.moveToElement(profile).build().perform();
        driver.findElement(By.xpath("//b[contains(@class,'header2-menu__dropdown-text_name')]")).click();
        logger.info("Открыта страница Персональные данные");
    }
    @Test
    public void testAuthLogCook() throws InterruptedException {
//        // Открыть Chrome в режиме полного экрана
        initDriver("start-maximized");

        //Перейти на https://otus.ru
        AuthNew();

        //В разделе "О себе" заполнить все поля "Личные данные" и добавить не менее двух контактов
        OpenPers();

        //Заполнение Персональных данных
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement addName = driver.findElement(By.xpath("//input[@type='text' and @name='fname']"));//Поле Имя
        addName.clear();
        addName.click();
        addName.sendKeys(serverConfig.name());
        WebElement addEnName = driver.findElement(By.xpath("//input[@type='text' and @name='fname_latin']"));//Поле Имя (латиницей)
        addEnName.clear();
        addEnName.click();
        addEnName.sendKeys(serverConfig.name());
        WebElement addSurname = driver.findElement(By.xpath("//input[@type='text' and @name='lname']"));//Поле Фамилия
        addSurname.clear();
        addSurname.click();
        addSurname.sendKeys(serverConfig.surname());
        WebElement addEnSurname = driver.findElement(By.xpath("//input[@type='text' and @name='lname_latin']"));//Поле Фамилия (латиницей)
        addEnSurname.clear();
        addEnSurname.click();
        addEnSurname.sendKeys(serverConfig.surname());
        WebElement addNick = driver.findElement(By.xpath("//input[@type='text' and @name='blog_name']"));//Имя в блоге
        addNick.clear();
        addNick.click();
        addNick.sendKeys(serverConfig.name());
        WebElement addBirth = driver.findElement(By.xpath("//input[@name='date_of_birth']"));//Дата рождения
        addBirth.clear();
        addBirth.click();
        addBirth.sendKeys(serverConfig.birth());
        driver.findElement(By.xpath("//label[contains(text(),'Дата рождения')]")).click();
        Thread.sleep(100);
        js.executeScript("window.scrollBy(0,100)","");
        Thread.sleep(100);
        WebElement country = driver.findElement(By.xpath("//input[@name='country']/following-sibling::div"));//Страна
        country.click();
        Thread.sleep(100);
        js.executeScript("window.scrollBy(0,-100)","");
        WebElement countryList = driver.findElement(By.xpath("//div[contains(@class,'lk-cv-block__select-scroll_country')]/button[2]"));
        js.executeScript("window.scrollBy(0,100)","");
        Thread.sleep(100);
        countryList.click();
        js.executeScript("window.scrollBy(0,100)","");
        WebElement city = driver.findElement(By.xpath("//input[@name='city']/following-sibling::div"));//Город
        city.click();
        Thread.sleep(100);
        js.executeScript("window.scrollBy(0,100)","");
        WebElement cityList = driver.findElement(By.xpath("//div[contains(@class,'lk-cv-block__select-scroll_city')]/button[2]"));
        cityList.click();
        WebElement engLevel = driver.findElement(By.xpath("//input[@name='english_level']/following-sibling::div")); //Уровень английского
        engLevel.click();
        js.executeScript("window.scrollBy(0,200)","");
        WebElement engLevelList = driver.findElement(By.xpath("//div[contains(@class,'lk-cv-block__select-scroll') and not(contains(@class,'country')) and not(contains(@class,'city'))]/button[2]"));
        engLevelList.click();


        //Добавление двух контактов
        js.executeScript("window.scrollBy(0,500)","");

        driver.findElement(By.xpath("//input[@name='contact-0-service']/following::div")).click();
        driver.findElement(By.xpath("//button[contains(text(),'VK')]")).click();
        WebElement setVk = driver.findElement(By.xpath("//input[@type='text' and @name='contact-0-value']"));
        setVk.sendKeys(serverConfig.vk());
        driver.findElement(By.xpath("//button[contains(text(),'Добавить')]")).click();
        driver.findElement(By.xpath("//input[@name='contact-1-service']/following::div")).click();
        driver.findElement(By.xpath("//div[(contains(@class,'lk-cv-block__select-options') or contains(@class,'lk-cv-block__select-options_left')) and not(contains(@class,'hide'))]//button[@title='Тelegram']")).click();
        WebElement setTg =  driver.findElement(By.xpath("//input[@type='text' and @name='contact-1-value']"));
        setTg.sendKeys(serverConfig.tg());
        logger.info("Данные добавлены");
//        Нажать сохранить
        js.executeScript("window.scrollBy(0,800)","");
        driver.findElement(By.xpath("//button[@title='Сохранить и заполнить позже']")).click();
        logger.info("Данные сохранены");
        //Закрыть страницу
        driver.close();

////        Открыть https://otus.ru в "чистом браузере"
        initDriver("start-maximized");
        AuthNew();
        OpenPers();
//        Проверить, что в разделе "О себе" отображаются указанные ранее данные
        String getCountry = driver.findElement(By.xpath("//input[@name='country']/following-sibling::div")).getText();
        String getCity = driver.findElement(By.xpath("//input[@name='city']/following-sibling::div")).getText();
        String getEngLevel = driver.findElement(By.xpath("//input[@name='english_level']/following-sibling::div")).getText();
        String getVk = driver.findElement(By.xpath("//input[@type='text' and @name='contact-1-value']")).getText();
        String getTg = driver.findElement(By.xpath("//input[@type='text' and @name='contact-0-value']")).getText();

        Assert.assertEquals("Город не корректен", "Россия", getCountry);
        Assert.assertEquals("Город не корректен", "Москва", getCity);
        Assert.assertEquals("Уровень языка не корректен", "Начальный уровень (Beginner)", getEngLevel);
//        Assert.assertEquals("Контакт1 не корректен", "https://t.me/test", getTg);
        Assert.assertEquals("Контакт2 не корректен", "vk.com/test", getVk);
        logger.info("Данные отображаются корректно");

        //









    }

}









