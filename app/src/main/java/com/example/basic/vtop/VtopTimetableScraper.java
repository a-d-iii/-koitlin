package com.example.basic.vtop;

import com.example.basic.ClassEntry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import okhttp3.*;
import okhttp3.JavaNetCookieJar;

/**
 * Minimal Java port of vitap_vtop_client timetable scraping logic.
 * Only implements methods required for the planner page.
 */
public class VtopTimetableScraper {
    private static final String BASE_URL = "https://vtop.vitap.ac.in";
    private static final String VTOP_URL = "/vtop/open/page";
    private static final String PRELOGIN_URL = "/vtop/prelogin/setup";
    private static final String LOGIN_URL = "/vtop/login";
    private static final String CONTENT_URL = "/vtop/content";
    private static final String TIME_TABLE_URL = "/vtop/academics/common/StudentTimeTable";
    private static final String GET_TIME_TABLE_URL = "/vtop/processViewTimeTable";

    private final OkHttpClient client;
    private final String username;
    private final String password;
    private String csrfToken;

    private static final Headers HEADERS = new Headers.Builder()
            .add("User-Agent", "Mozilla/5.0")
            .build();

    public VtopTimetableScraper(String username, String password) {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        this.client = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .followRedirects(true)
                .build();
        this.username = username;
        this.password = password;
    }

    /** Fetch the captcha image and CSRF token from the login page. */
    public Captcha fetchCaptcha() throws IOException {
        Request req = new Request.Builder()
                .url(BASE_URL + LOGIN_URL)
                .headers(HEADERS)
                .build();
        try (Response res = client.newCall(req).execute()) {
            String body = Objects.requireNonNull(res.body()).string();
            Document doc = Jsoup.parse(body);
            Element img = doc.selectFirst("img[src^=data:image]");
            Element csrf = doc.selectFirst("input[name=_csrf]");
            String base64 = img != null ? img.attr("src") : null;
            if (base64 != null && base64.contains(",")) {
                base64 = base64.substring(base64.indexOf(',') + 1);
            }
            String token = csrf != null ? csrf.attr("value") : null;
            return new Captcha(base64, token);
        }
    }

    /** Perform full login sequence. Caller must supply captcha solution. */
    public void login(String captchaValue) throws IOException {
        if (csrfToken == null) {
            csrfToken = fetchInitialCsrf();
        }
        // pre login
        FormBody preBody = new FormBody.Builder()
                .add("_csrf", csrfToken)
                .add("flag", "VTOP")
                .build();
        Request pre = new Request.Builder()
                .url(BASE_URL + PRELOGIN_URL)
                .headers(HEADERS)
                .post(preBody)
                .build();
        try (Response r = client.newCall(pre).execute()) {
            // ignore
        }

        // get login page to refresh captcha and csrf
        Captcha cap = fetchCaptcha();
        if (cap.csrf != null) {
            csrfToken = cap.csrf;
        }
        // login
        FormBody body = new FormBody.Builder()
                .add("_csrf", csrfToken)
                .add("username", username)
                .add("password", password)
                .add("captchaStr", captchaValue)
                .build();
        Request loginReq = new Request.Builder()
                .url(BASE_URL + LOGIN_URL)
                .headers(HEADERS)
                .post(body)
                .build();
        try (Response r = client.newCall(loginReq).execute()) {
            // ignore response
        }
        // fetch content page to obtain post login CSRF token
        Request content = new Request.Builder()
                .url(BASE_URL + CONTENT_URL)
                .headers(HEADERS)
                .build();
        try (Response res = client.newCall(content).execute()) {
            String b = Objects.requireNonNull(res.body()).string();
            Document doc = Jsoup.parse(b);
            Element csrf = doc.selectFirst("input[name=_csrf]");
            if (csrf != null) {
                csrfToken = csrf.attr("value");
            }
        }
    }

    private String fetchInitialCsrf() throws IOException {
        Request req = new Request.Builder()
                .url(BASE_URL + VTOP_URL)
                .headers(HEADERS)
                .build();
        try (Response res = client.newCall(req).execute()) {
            String body = Objects.requireNonNull(res.body()).string();
            Document doc = Jsoup.parse(body);
            Element csrf = doc.selectFirst("input[name=_csrf]");
            return csrf != null ? csrf.attr("value") : null;
        }
    }

    /** Retrieve timetable for the given semester. Assumes login() was called. */
    public Map<String, List<ClassEntry>> fetchTimetable(String semSubId) throws IOException {
        if (csrfToken == null) {
            throw new IllegalStateException("Not logged in");
        }
        // initialize timetable page
        FormBody initBody = new FormBody.Builder()
                .add("verifyMenu", "true")
                .add("authorizedID", username)
                .add("_csrf", csrfToken)
                .add("nocache", Long.toString(System.currentTimeMillis()))
                .build();
        Request initReq = new Request.Builder()
                .url(BASE_URL + TIME_TABLE_URL)
                .headers(HEADERS)
                .post(initBody)
                .build();
        try (Response r = client.newCall(initReq).execute()) {
            // ignore
        }

        // actual timetable fetch
        FormBody body = new FormBody.Builder()
                .add("_csrf", csrfToken)
                .add("semesterSubId", semSubId)
                .add("authorizedID", username)
                .add("x", ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .build();
        Request ttReq = new Request.Builder()
                .url(BASE_URL + GET_TIME_TABLE_URL)
                .headers(HEADERS)
                .post(body)
                .build();
        try (Response res = client.newCall(ttReq).execute()) {
            String html = Objects.requireNonNull(res.body()).string();
            return TimetableParser.parse(html);
        }
    }

    /** Simple container for captcha data. */
    public static class Captcha {
        public final String imageBase64;
        public final String csrf;
        public Captcha(String img, String csrf) {
            this.imageBase64 = img;
            this.csrf = csrf;
        }
    }
}
