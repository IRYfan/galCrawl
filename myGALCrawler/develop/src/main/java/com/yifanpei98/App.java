package com.yifanpei98;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.Tool;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class App {
    private static String URL = "https://www.zfshe1.com/game/pc/page/1";
    private static String GAL_TAG_NAME = "article";
    private static String PATH_GAL_RECORD = "myGALCrawler/records/";
    private static final Logger logger = LogManager.getLogger("App");
    private static String FILE_NAME = "gal_record.xml";

    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException {
        App app = new App();
        app.run();
    }

    void run() throws IOException, ParserConfigurationException, TransformerException {
        OkHttpClient okHttp = new OkHttpClient();
        Request request = new Request.Builder().url(URL).get().build();
        Document doc = Jsoup.parse(okHttp.newCall(request).execute().body().string());
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(date);
        Element firstGal = doc.select(GAL_TAG_NAME).first();
        String galTitle = firstGal.select("h3").attr("title");
        Tool tool = new Tool();
        if (new File(PATH_GAL_RECORD).listFiles().length == 0) {
            logger.info("No existed gal records detected, storing the current gal item...");
            tool.writeXML(PATH_GAL_RECORD + FILE_NAME, galTitle, formattedDate);
        } else {
            logger.info("Existed gal records detected, proceeding records comparison...");
            HashMap<String, String> xmlData = (HashMap<String, String>) tool.readXML(PATH_GAL_RECORD + FILE_NAME);
            Timestamp recordTime = Timestamp.valueOf(xmlData.get("time"));
            Timestamp currTime = Timestamp.valueOf(formattedDate);
            if (currTime.compareTo(recordTime) > 0) {
                // currenttime is the latest one
                if (!xmlData.get("title").equals(galTitle)) {
                    logger.info("new gal has come out!");
                } else {
                    logger.info("no new gal comes out yet...");
                }
            } else {
                logger.info("you have finished the resources detection, please try again later");
            }
        }
    }
}
