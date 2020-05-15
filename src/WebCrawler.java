import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WebCrawler {

    public static ArrayList<String> willAnalyse = new ArrayList<>() {
        {
            add("http://comp3310.ddns.net:7880\n");
        }
    };
    public static HashSet<String> beenAnalysed = new HashSet<>();
    public static int HTMLPage = 0;
    public static int nonHTML = 0;
    public static String[] smallestPage = {"", "2000"};
    public static String[] largestPage = {"", "0"};
    public static String[] oldestPage = {"", "15 May 2020 09:00:00"};
    public static String[] latestPage = {"", "01 Jan 1900 09:00:00"};
    public static HashSet<String> invalidURL = new HashSet<>();
    public static HashMap<String, String> onsiteURL = new HashMap<>();
    public static HashSet<String> offsiteList = new HashSet<>();
    public static HashMap<String, String> offsiteMap = new HashMap<>();


    public static void main(String[] args) throws IOException, ParseException {

        int i = 0;
        while (willAnalyse.size() > i) {
            currentURLAnalyse(willAnalyse.get(i));
            beenAnalysed.add(willAnalyse.get(i));
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
                "Total number of distinct URLs: " + (beenAnalysed.size() + offsiteList.size()) + "\n" +
                "The number of html pages: " + HTMLPage + "\n" +
                "The number of non-html objects: " + nonHTML + "\n" +
                "The smallest page: " + smallestPage[0] + "(" + smallestPage[1] + " bytes)\n" +
                "The biggest page: " + largestPage[0] + "(" + largestPage[1] + " bytes)\n" +
                "The oldest page: " + oldestPage[0] + "(Modified on: " + oldestPage[1] + ")\n" +
                "The latest page: " + latestPage[0] + "(Modified on: " + latestPage[1] + ")");
        System.out.println("The list of invalid URLs:");
        for (String str : invalidURL) {
            System.out.println("- " + str);
        }
        System.out.println("The table of on-site redirected URLs: ");
        for (String str : onsiteURL.keySet()) {
            System.out.println("- " + str + " -> " + onsiteURL.get(str));
        }
        System.out.println("The table of off-site URLs: ");
        for (String str : offsiteMap.keySet()) {
            System.out.println("- " + str + " -> " + offsiteMap.get(str));
        }
    }


    public static void currentURLAnalyse(String urlStr) throws IOException, ParseException {
        URL url = new URL(urlStr);
        String host = url.getHost();
        int port = url.getPort();

        Socket sock = new Socket(host, port);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        bw.write("GET " + url.toString() + " HTTP/1.0\r\n");
        bw.write("HOST:" + host + "\r\n");
        bw.write("\r\n");
        bw.flush();

        String document = "new StringBuilder()";

        BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        String line;
        String URLInLine;
        while ((line = br.readLine()) != null) {
            document += line;

            if (line.contains("src=")) {
                nonHTML++;
            }

            if (line.contains("Content-Length")) {
                String pageSize = line.substring(line.indexOf(':') + 2);
                if (Integer.parseInt(pageSize) < Integer.parseInt(smallestPage[1])) {
                    smallestPage[0] = urlStr;
                    smallestPage[1] = pageSize;
                }
                if (Integer.parseInt(pageSize) > Integer.parseInt(largestPage[1])) {
                    largestPage[0] = urlStr;
                    largestPage[1] = pageSize;
                }
            }

            if (line.contains("Last-Modified")) {
                String tempDateStr = line.substring(line.indexOf(',') + 2, line.length() - 4);
                if (dateParser(tempDateStr).before(dateParser(oldestPage[1]))) {
                    oldestPage[0] = urlStr;
                    oldestPage[1] = tempDateStr;
                }
                if (dateParser(tempDateStr).after(dateParser(latestPage[1]))) {
                    latestPage[0] = urlStr;
                    latestPage[1] = tempDateStr;
                }
            }

            if (line.contains("404")) {
                invalidURL.add(urlStr);
            }

            if (line.contains("Location")) {
                onsiteURL.put(urlStr, line.substring(line.indexOf(':') + 2));
            }

            if (line.contains("href")) {
                URLInLine = line.substring(line.indexOf('"') + 1, line.lastIndexOf('"'));
                if (URLInLine.contains("http") && !URLInLine.contains("3310")) {
                    offsiteList.add(URLInLine);
                } else {
                    if (URLInLine.contains("http")) {
                    } else if (URLInLine.charAt(0) == '/') {
                        URLInLine = "http://comp3310.ddns.net:7880" + URLInLine;
                    } else {
                        URLInLine = "http://comp3310.ddns.net:7880/" + URLInLine;
                    }
                    if (!willAnalyse.contains(URLInLine)) {
                        willAnalyse.add(URLInLine);
                    }
                }

            }
        }
        if (document.contains("<html>")) {
            HTMLPage++;
        }
    }

    public static Date dateParser(String dateStr) throws ParseException {
        String month = dateStr.substring(3, 6);
        String nMonth = "";
        switch (month) {
            case "Jan":
                nMonth = "01";
                break;
            case "Feb":
                nMonth = "02";
                break;
            case "Mar":
                nMonth = "03";
                break;
            case "Apr":
                nMonth = "04";
                break;
            case "May":
                nMonth = "05";
                break;
            case "Jun":
                nMonth = "06";
                break;
            case "Jul":
                nMonth = "07";
                break;
            case "Aug":
                nMonth = "08";
                break;
            case "Sep":
                nMonth = "09";
                break;
            case "Oct":
                nMonth = "10";
                break;
            case "Nov":
                nMonth = "11";
                break;
            case "Dec":
                nMonth = "12";
                break;
        }
        dateStr = dateStr.substring(0, 3) + nMonth + dateStr.substring(6);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
        return sdf.parse(dateStr);
    }
}
