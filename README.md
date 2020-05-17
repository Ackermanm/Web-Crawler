# comp6331-assignment02
A simple web crawler

Using Java version 11.0.6
Total 3 methods in this assignment. 
The main() methods for executing webcrawler and iteratively collecting Urls by using method currentURLAnalyse(String urlString).
Method currentURLAnalyse(String urlString) is to analyse the unique url. This is the core method to collecting data.
Method dateParser is to parse a date string to a Date. Being used in method currentURLAnalyse(String urlString).
Before the final results, there will print informations about each pages.
Below is the final results for http://comp3310.ddns.net:7880 in curreent edition:

********** HERE ARE THE RESULTS **********

Total number of distinct URLs: 49
The number of html pages: 38
The number of non-html objects: 9
The smallest page: http://comp3310.ddns.net:7880/B/23.html(1443 bytes)
The biggest page: http://comp3310.ddns.net:7880/C/307.html(7261 bytes)
The oldest page: http://comp3310.ddns.net:7880/A/10.html(Modified on: Tue, 01 Jan 2019 05:05:00 GMT)
The latest page: http://comp3310.ddns.net:7880/C/30.html(Modified on: Sun, 05 May 2019 01:01:00 GMT)
The list of invalid URLs:
- http://comp3310.ddns.net:7880/F/10.html
- http://comp3310.ddns.net:7880/F/20.html
- http://comp3310.ddns.net:7880/F/30.html
The table of on-site redirected URLs: 
- http://comp3310.ddns.net:7880/A/1A.html -> http://comp3310.ddns.net:7880/B/29.html
- http://comp3310.ddns.net:7880/C/3A.html -> http://comp3310.ddns.net:7880/A/19.html
- http://comp3310.ddns.net:7880/B/2A.html -> http://comp3310.ddns.net:7880/C/39.html
The table of off-site URLs: 
- http://www.canberratimes.com.au/ -> web server available
- http://comp3311.ddns.net:7880/B/207.html -> web server unavailable
