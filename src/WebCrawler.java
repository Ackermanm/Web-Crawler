import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WebCrawler {

    public static URL Url;
    public static ArrayList<String> willAnalyse = new ArrayList<>(); // The urls that are on-site valid html and will be analysed.
    public static HashSet<String> beenAnalysed = new HashSet<>(); // The urls that has been analysed.
    public static HashSet<String> noHTML = new HashSet<>();
    public static String[] smallestPage = {"", "2000"};
    public static String[] largestPage = {"", "0"};
    public static String[] oldestPage = {"", "Mon, 15 May 2020 09:00:00 GMT"};
    public static String[] latestPage = {"", "Mon, 01 Jan 1900 09:00:00 GMT"};
    public static HashSet<String> invalidURL = new HashSet<>();
    public static HashMap<String, String> onsiteRedirectURL = new HashMap<>();
    public static HashSet<String> offsiteList = new HashSet<>();
    public static HashMap<String, String> offsiteMap = new HashMap<>();




    /** Here is the main method */
    public static void main(String[] args) throws IOException, ParseException {

        String strURLToAnalyse = "http://comp3310.ddns.net:7880";
        willAnalyse.add(strURLToAnalyse);
        Url = new URL(strURLToAnalyse);
        System.out.println("\n\n");

        int i = 0;
        while (willAnalyse.size() > i) {
            currentURLAnalyse(willAnalyse.get(i));
            i++;
        }

        for (String str : offsiteList) {
            URL offUrl = new URL(str);
            try {
                offUrl.openStream();
                offsiteMap.put(str, "web server available");
            } catch (Exception e) {
                offsiteMap.put(str, "web server unavailable");
            }
        }

        System.out.println("\n");
        System.out.println("********** HERE ARE THE RESULTS **********\n\n" +
                "Total number of distinct URLs: " + (beenAnalysed.size() + noHTML.size() + offsiteList.size()) + "\n" +
                "The number of html pages: " + beenAnalysed.size() + "\n" +
                "The number of non-html objects: " + noHTML.size() + "\n" +
                "The smallest page: " + smallestPage[0] + "(" + smallestPage[1] + " bytes)\n" +
                "The biggest page: " + largestPage[0] + "(" + largestPage[1] + " bytes)\n" +
                "The oldest page: " + oldestPage[0] + "(Modified on: " + oldestPage[1] + ")\n" +
                "The latest page: " + latestPage[0] + "(Modified on: " + latestPage[1] + ")");
        System.out.println("The list of invalid URLs:");
        for (String str : invalidURL) {
            System.out.println("- " + str);
        }
        System.out.println("The table of on-site redirected URLs: ");
        for (String str : onsiteRedirectURL.keySet()) {
            System.out.println("- " + str + " -> " + onsiteRedirectURL.get(str));
        }
        System.out.println("The table of off-site URLs: ");
        for (String str : offsiteMap.keySet()) {
            System.out.println("- " + str + " -> " + offsiteMap.get(str));
        }
    }




    /** Core method for analysing one url */
    public static void currentURLAnalyse(String urlString) throws IOException, ParseException {

        URL url = new URL(urlString);
        String host = url.getHost();
        int port = url.getPort();

        Socket sock = new Socket(host, port);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        bw.write("GET " + url.toString() + " HTTP/1.0\r\n");
        bw.write("HOST:" + host + "\r\n");
        bw.write("\r\n");
        bw.flush();

        BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        String line;
        String URLInLine;
        System.out.println("The analyse of: " + urlString);
        while ((line = br.readLine()) != null) {
            System.out.println(line);

            if (line.contains("<li> <a href=")) { //For item 02. Collecting all html pages.
                URLInLine = line.substring(line.indexOf("href") + 6, line.indexOf("\">Here"));
                if (URLInLine.contains("http") && !URLInLine.contains(host)) { // For item 07. Collecting off site urls.
                    offsiteList.add(URLInLine);
                } else {
                    if (URLInLine.contains("http")) {
                    } else if (URLInLine.charAt(0) == '/') {
                        URLInLine = Url.toString() + URLInLine;
                    } else {
                        URLInLine = Url.toString() + "/" + URLInLine;
                    }
                    if (!willAnalyse.contains(URLInLine)) {
                        willAnalyse.add(URLInLine);
                    }
                }
            }

            if (line.contains("<img src=")) { // For item 02. Collecting non-html pages.
                URLInLine = line.substring(line.lastIndexOf("src") + 5, line.indexOf("\" alt"));
                String[] urlSplit = urlString.split("/");
                noHTML.add(Url.toString() + "/" + urlSplit[3] + "/" + URLInLine);
            }

            if (line.contains("Content-Length")) { // for item 03. Get the smallest and the largest page.
                String pageSize = line.substring(line.indexOf("Length") + 8);
                if (!invalidURL.contains(urlString) && !onsiteRedirectURL.containsKey(urlString)) {
                    if (Integer.parseInt(pageSize) < Integer.parseInt(smallestPage[1])) {
                        smallestPage[0] = urlString;
                        smallestPage[1] = pageSize;
                    }
                    if (Integer.parseInt(pageSize) > Integer.parseInt(largestPage[1])) {
                        largestPage[0] = urlString;
                        largestPage[1] = pageSize;
                    }
                }
            }

            if (line.contains("Last-Modified")) { // For item 04. Get the oldest and the latest page.
                String tempDateStr = line.substring(line.indexOf("Modified") + 10);
                if (dateParser(tempDateStr).before(dateParser(oldestPage[1]))) {
                    oldestPage[0] = urlString;
                    oldestPage[1] = tempDateStr;
                }
                if (dateParser(tempDateStr).after(dateParser(latestPage[1]))) {
                    latestPage[0] = urlString;
                    latestPage[1] = tempDateStr;
                }
            }

            if (line.contains("404")) { // For item 05. Get the invalid pages.
                invalidURL.add(urlString);
            }

            if (line.contains("Location")) { // For item 06. Collecting on-site redirected URLs.
                onsiteRedirectURL.put(urlString, line.substring(line.indexOf(':') + 2));
            }
        }
        beenAnalysed.add(urlString);
    }





    /** Method to parse date String to date */
    public static Date dateParser(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        Date date = sdf.parse(dateStr);
        return date;
    }
}