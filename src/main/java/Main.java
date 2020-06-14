import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;


class User {
    String username;
    String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

public class Main {
    private static WebDriver driver;
    private static User user;
    private static String url;
    private static Logger log;
    private static ChromeOptions chromeOptions;
    private static String cookie_filename;
    private static int chrome_waiting;
    private static int firefox_waiting;


    static void SwitchToLastTab() {
        Set<String> allWindowsId = driver.getWindowHandles();
        driver.switchTo().window(allWindowsId.toArray()[allWindowsId.size() - 1].toString());
    }

    static void Init() {
        chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--start-maximized");
        url = "https://bilibili.com";
        user = new User("13916062410", "xxxxxxx");
        cookie_filename = "bilibili.cookie.txt";
        log = Logger.getLogger(Main.class);
        chrome_waiting = 1000;
        firefox_waiting = 5000;
    }

    static void Login() throws InterruptedException {
        // click login
        WebElement loginPageButton = driver.findElement(By.className("mini-login"));
        loginPageButton.click();

        // switch to last tab
        SwitchToLastTab();

        // input message
        WebElement loginUserNameInput = driver.findElement(By.id("login-username"));
        loginUserNameInput.sendKeys(user.username);
        WebElement passwordInput = driver.findElement(By.id("login-passwd"));
        passwordInput.sendKeys(user.password);
        WebElement loginButton = driver.findElement(By.className("btn-login"));
        loginButton.click();

        // verification
        log.info("breakpoint here for captcha");
        log.info("login successfully");

        // save cookie
        File cookieFile = new File(cookie_filename);
        try {
            cookieFile.delete();
            cookieFile.createNewFile();
            FileWriter fileWriter = new FileWriter(cookieFile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (Cookie cookie : driver.manage().getCookies()) {
                bufferedWriter.write((cookie.getName() + ";" +
                        cookie.getValue() + ";" +
                        cookie.getDomain() + ";" +
                        cookie.getPath() + ";" +
                        cookie.getExpiry() + ";" +
                        cookie.isSecure()));
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        log.info("cookie save successfully");
    }

    static void GenerallySearch() {
        // search 吹梦到西洲
        String searchText = "吹梦到西洲";
        WebElement serachBarInput = driver.findElement(By.className("nav-search-keyword"));
        serachBarInput.sendKeys(searchText);
        WebElement searchButton = driver.findElement(
                By.className("nav-search-btn"));
        searchButton.click();

        // switch to last tab
        SwitchToLastTab();
    }

    static void Play(int moment) throws InterruptedException {
        // wait for switch
        Thread.sleep(moment);

        // select the first one
        WebElement videoButton = driver.findElement(
                By.xpath("//ul[@class='video-list clearfix']/li[1]/a/div"));
        videoButton.click();

        // wait for switch
        Thread.sleep(moment);

        // switch to last tab
        SwitchToLastTab();

        // wait for switch
        Thread.sleep(moment);

        // click to play
        WebElement playButton = driver.findElement(
                By.xpath("//button[@class='bilibili-player-iconfont bilibili-player-iconfont-start']"));
        playButton.click();

        // play 5 seconds
        Thread.sleep(5000);

        // click to stop
        WebElement stopButton = driver.findElement(
                By.xpath("//div[@class='bilibili-player-video']"));
        stopButton.click();

        // wait for switch
        Thread.sleep(moment);
    }

    static void Browse(int moment) throws InterruptedException {
        log.info("browse begin");

        // search
        GenerallySearch();

        // play
        Play(moment);

        log.info("browse test finish");

        // Mouse hover
        Actions action = new Actions(driver);


        // video quality adjustment
        WebElement volumeElement = driver.findElement(By.xpath("//span[@class='bui-select-result']"));
        action.moveToElement(volumeElement).perform();

        // wait for switch
        Thread.sleep(moment*5);


        // close
        driver.close();

        SwitchToLastTab();


        driver.close();

        SwitchToLastTab();

        // wait for switch
        Thread.sleep(moment);

        log.info("browse test finish");
    }

    static void Download(int moment) throws InterruptedException {
        WebElement el = driver.findElement(By.xpath("//a[@href='//app.bilibili.com']"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", el);

        Thread.sleep(2000);

        SwitchToLastTab();

        WebElement downloadAPK = driver.findElement(By.xpath("//a[@title='安卓版']"));
        JavascriptExecutor js2 = (JavascriptExecutor) driver;
        js2.executeScript("arguments[0].click();", downloadAPK);

        driver.close();

        SwitchToLastTab();

        // wait for switch
        Thread.sleep(moment);

        log.info("download test finish");
    }

    static void Resolution(int moment) throws InterruptedException {
        // resolution
        driver.manage().window().setSize(new Dimension(480, 800));

        // test again
        try {
            Browse(moment);
        } catch (Exception e) {
            log.info("Resolution Exception. And Browse by another way");
            // click search button
            WebElement videoButton = driver.findElement(
                    By.xpath("//i[@class='bilifont bili-icon_dingdao_sousuo']"));
            videoButton.click();

            SwitchToLastTab();

            // search 吹梦到西洲
            String searchText = "吹梦到西洲";
            WebElement serachBarInput = driver.findElement(By.id("search-keyword"));
            serachBarInput.sendKeys(searchText);
            WebElement searchButton = driver.findElement(
                    By.className("searchBtn"));
            searchButton.click();

            // switch to last tab
            SwitchToLastTab();

            // play
            Play(moment);

            log.info("Resolution Exception Handle Finish.");
        }


        log.info("resolution test finish");

    }

    static void Compatibility() throws InterruptedException {
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
        driver.get(url);

        Browse(firefox_waiting);
        driver.quit();

    }

    static void Upload() throws InterruptedException {
        driver = new ChromeDriver(chromeOptions);
        driver.get(url);

        // get cookie
        BufferedReader bufferedReader;
        try {
            File cookieFile = new File(cookie_filename);
            FileReader fileReader = new FileReader(cookieFile);
            bufferedReader = new BufferedReader(fileReader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                StringTokenizer stringTokenizer = new StringTokenizer(line, ";");
                while (stringTokenizer.hasMoreTokens()) {

                    String name = stringTokenizer.nextToken();
                    String value = stringTokenizer.nextToken();
                    String domain = stringTokenizer.nextToken();
                    String path = stringTokenizer.nextToken();
                    Date expiry = null;
                    String dt;

                    if (!(dt = stringTokenizer.nextToken()).equals("null")) {
                        expiry = new Date(dt);
                    }

                    boolean isSecure = new Boolean(stringTokenizer.nextToken()).booleanValue();
                    Cookie cookie = new Cookie(name, value, domain, path, expiry, isSecure);
                    driver.manage().addCookie(cookie);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        driver.get(url);

        String imageURL = "C:\\1.jpg";
        String area = "游戏";
        String subArea = "单机游戏";

        //点击"投稿"
        WebElement uploadButton = driver.findElement(By.className("mini-upload"));
        uploadButton.click();

        //driver切换到投稿页面
        Set<String> allWindowsId = driver.getWindowHandles();//获取所有窗口句柄
        driver.switchTo().window(allWindowsId.toArray()[allWindowsId.size() - 1].toString());   //跳到最后一个tab页

        //显示等待遮罩形成
        WebDriverWait waitMask = new WebDriverWait(driver, 10, 1);
        waitMask.until(new ExpectedCondition<WebElement>() {
            public WebElement apply(WebDriver d) {
                return d.findElement(By.className("jump"));
            }
        });

        //点击遮罩的"跳过"。属性为JavaScript，这类元素需要用js点击
        WebElement el = driver.findElement(By.className("jump"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", el);

        //投稿框为iframe，切换driver
        WebElement videoUploadFrame = driver.findElement(By.name("videoUpload"));
        driver.switchTo().frame(videoUploadFrame);

        // waiting
        Thread.sleep(chrome_waiting);

        WebElement btn = driver.findElement(By.xpath("//div[@id='bili-upload-btn']"));
        btn.click();

        // waiting
        Thread.sleep(chrome_waiting);

        // execute autoIt script to upload file
        Runtime rt = Runtime.getRuntime();
        try{
            rt.exec("C:\\1.exe");
        }catch (Exception e){
            log.info("upload file error");
        }

        //上传封面
        driver.findElement(By.className("cover-v2-preview")).findElement(By.tagName("input")).sendKeys(imageURL);
        //显示等待上传图片的窗口生成
        WebDriverWait waitImage = new WebDriverWait(driver,10,1);
        waitImage.until(new ExpectedCondition<WebElement>(){
            public WebElement apply(WebDriver d) {
                return d.findElement(By.className("cover-chop-modal-v2-foot")).findElement(By.tagName("div"));
            }
        });

        Thread.sleep(chrome_waiting*2);

        List<WebElement> els = driver.findElement(By.className("cover-chop-modal-v2-foot")).findElements(By.tagName("div"));
        for (int i = 0; i < els.size(); i++) {
            if (els.get(i).getText().equals("确认")) {
                els.get(i).click();
                break;
            }
        }

        Thread.sleep(chrome_waiting);

        //分区选择（下拉框）
        driver.findElement(By.className("select-item-cont")).click();
        List<WebElement> areaList = driver.findElement(By.className("drop-cascader-pre-wrp")).findElements(By.className("drop-cascader-pre-item"));
        for (int i = 0; i< areaList.size(); i++) {
            if (areaList.get(i).findElement(By.className("pre-item-content")).getText().equals(area)) {
                areaList.get(i).click();
                List<WebElement> subAreaList = driver.findElement(By.className("drop-cascader-list-wrp")).findElements(By.className("drop-cascader-list-item"));
                for (int j = 0; j < subAreaList.size(); j++) {
                    if (subAreaList.get(j).findElement(By.className("item-main")).getText().equals(subArea)) {
                        subAreaList.get(j).click();
                        break;
                    }
                }
                break;
            }
        }

        WebElement tagInput = driver.findElement(By.id("content-tag-v2-container")).findElement(By.className("input-box-v2-1-val"));
        tagInput.sendKeys("单机联机");
        tagInput.sendKeys(Keys.ENTER);
        Thread.sleep(chrome_waiting);
        tagInput.sendKeys("游戏");
        tagInput.sendKeys(Keys.ENTER);
        Thread.sleep(chrome_waiting);
        tagInput.sendKeys("SJTU");
        tagInput.sendKeys(Keys.ENTER);

        // waiting
        Thread.sleep(chrome_waiting*5);

        log.info("upload successfully");

        // switch back
        SwitchToLastTab();
        WebElement el2 =  driver.findElement(By.xpath("//a[@href='#/upload/text']"));
        JavascriptExecutor js2 = (JavascriptExecutor) driver;
        js2.executeScript("arguments[0].click();",el2);

        // waiting
        Thread.sleep(chrome_waiting);

        // handle alert
        WebElement anotherbtn = driver.findElement(By.xpath("//button[@class='bili-btn ok']"));
        anotherbtn.click();

        log.info("handle alert successfully");

        // waiting
        Thread.sleep(chrome_waiting*5);

        driver.close();
    }


    public static void main(String[] args) {
        // argument init
        Init();

        //chrome driver
        driver = new ChromeDriver(chromeOptions);
        driver.get(url);

        try {
            Browse(chrome_waiting);
        } catch (Exception e) {
            log.info("Browse error");
        }

        try {
            Download(chrome_waiting);
        } catch (Exception e) {
            log.info("Download error");
        }

        try {
            Resolution(chrome_waiting);
        } catch (Exception e) {
            log.info("Resolution error");
        }

        try {
            Login();
        } catch (Exception e) {
            log.info("Don't need Login for cookie?");
        }

        // quit driver
        driver.quit();

        try {
            Upload();
        } catch (Exception e) {
            log.info("Upload error");
        }

        try{
            driver.quit();
        }catch (Exception e){
            log.info("Driver quit");
        }

        try {
            Compatibility();
        } catch (Exception e) {
            log.info("Compatibility error");
        }

        try{
            driver.quit();
        }catch (Exception e){
            log.info("Driver quit");
        }

        log.info("All Tests Finish");
    }
}
