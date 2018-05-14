package org.lingg.learn.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class JsoupTest1 {

    public static void main(String[] args) throws Exception {

        // String bookmark1 = getFileContent("C://360bookmarks_2018_5_11.html");
        // String bookmark2 = getFileContent("C://UC书签备份_2018-5-11.html");

        Set<String> links1 = getHtmlLinks("C://360bookmarks_2018_5_11.html");
        Set<String> uclinks = getHtmlLinks("C://UC书签备份_2018-5-11.html");

        System.out.println(links1.size());
        System.out.println(uclinks.size());

        for(String link : links1){
            if(!uclinks.contains(link)){
                System.out.println(link);
            }
        }
    }

    private static Set<String> getHtmlLinks(String filePath) throws IOException {
        Document mark1 = Jsoup.parse(new File(filePath), "gbk");

        Set<String> linkSet = new HashSet<>();

        Elements links = mark1.getElementsByTag("a");
        //System.out.println(links.size());

        for (Element link : links) {
            String linkHref = link.attr("href");
            // String linkText = link.text();
            linkSet.add(linkHref);
        }

        return linkSet;
    }

    private String getFileContent(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            sb.append(line + System.lineSeparator());
        }

        reader.close();
        return sb.toString();
    }
}
