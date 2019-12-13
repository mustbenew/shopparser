import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pojo.ProductItem;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Random;
import static java.lang.Thread.sleep;

public class PageThread implements Runnable {
    private volatile String url;

    public PageThread(final String url) {
        this.url = url;
    }

    public void run() {
        Random r = new Random();
        String str = null;
        try {
            // sleep random time, antibot security detection
            sleep(r.nextInt(9000) + 1000);
            // get page content (16-20 product per page)
            str = getPageContent(url);
            // delete bad symbols
            String test = str.substring(str.indexOf("<main id=\"app\">"), str.indexOf("</main>") + 7).replaceAll("&amp;", " ").replaceAll("&#x27;", "'").replaceAll("& ", " ");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            // parse string into XML
            Document document = builder.parse(new InputSource(new StringReader(
                    test)));

            // get product nodes using xpath
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile("//a[@data-test-id=\"ProductTile\"]");
            NodeList elements = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            // NodeList elements = document.getElementsByTagName("*"); // all nodes
            //
            for (int i = 0; i < elements.getLength(); i++) {
                Element element = (Element) elements.item(i);

                if (element.getAttribute("data-test-id").equals("ProductTile")) {


                    String path = "https://www.aboutyou.de" + element.getAttribute("href");
                    // get product detailed page content
                    String content = getPageContent(path);
                    // detect if something wrong with page
                    if (content != null && content.contains("ProductName")) {
                        ProductItem pi = new ProductItem();
                        // get product name
                        String detailsContent = content.substring(content.indexOf("ProductName"));
                        String productName = detailsContent.substring(detailsContent.indexOf(">") + 1, detailsContent.indexOf("<")).replaceAll("&#x27;", "'");
                        pi.setName(productName);
                        // get article
                        detailsContent = content.substring(content.indexOf("ArticleNumber"));
                        String articleNumber = detailsContent.substring(detailsContent.indexOf(">") + 1, detailsContent.indexOf("<") + 33)
                                .replaceAll("<!-- -->", "").replaceAll("</p></", "").replaceAll("</p", "");
                        pi.setArticleNumber(articleNumber);

                        NodeList nodeList = element.getElementsByTagName("*");
                        HashSet<String> colors = new HashSet<>();
                        // iterate by nodes with product information (products list page)
                        for (int k = 0; k < nodeList.getLength(); k++) {
                            Element param = (Element) nodeList.item(k);

                            if (param.getAttribute("data-test-id").equals("BrandName")) {
                                pi.setBrand(param.getTextContent());
                            }
                            // get price value or discount value
                            if (param.getAttribute("data-test-id").equals("ProductPriceFormattedBasePrice") || param.getAttribute("data-test-id").equals("FormattedSalePrice")) {
                                pi.setPrice(param.getTextContent());
                            }
                            // get colors(1 per iteration)
                            if (param.getAttribute("data-test-id").equals("ColorBubble")) {
                                colors.add(param.getAttribute("color"));
                            }
                        }
                        // set colors
                        pi.setColor(colors);
                        // set result product data bean in to HashSet
                        synchronized (this) {
                            PageExtractor.productItems.add(pi);
                        }

                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

    }

    /**Get http page as string using 'get' method*/
    public String getPageContent(String page) throws IOException {
        URL newUrl = new URL(page);
        StringBuffer result = new StringBuffer();
        HttpsURLConnection conn = (HttpsURLConnection) newUrl.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        //String input1;

        /*while ((input1 = bufferedReader.readLine()) != null) {
            result.append(input1);
        }
        bufferedReader.close();*/
        int BUFFER_SIZE = 1024;
        char[] buffer = new char[BUFFER_SIZE]; // or some other size,
        int charsRead = 0;
        while ((charsRead = bufferedReader.read(buffer, 0, BUFFER_SIZE)) != -1) {
            result.append(buffer, 0, charsRead);
        }
        return result.toString();
    }

}


