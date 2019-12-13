import pojo.ProductItem;
import javax.xml.parsers.*;
import java.io.*;
import java.util.Calendar;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PageExtractor {
    public static volatile HashSet<ProductItem> productItems = new HashSet<ProductItem>();

    public static void main(String[] args) throws IOException, ParserConfigurationException, org.xml.sax.SAXException {
        Long start = Calendar.getInstance().getTimeInMillis();
        //create threads pool
        ExecutorService executor = Executors.newFixedThreadPool(10);
        // hardcoded value of product pages (maybe not total)
        for (int m = 1; m <=453; m++){
            String url = "https://www.aboutyou.de/maenner/bekleidung?sort=topseller&page="+m;
            // generate task for each page
            Runnable task = new PageThread(url);
            //execute task
            executor.execute(task);
        }
        executor.shutdown();
        // waiting for complete all tasks
        while (!executor.isTerminated()) {   }

        System.out.println("Total parsed products: " + productItems.size());

        /**write results to a file**/
        PrintWriter writer = new PrintWriter(new File("ProductList.csv"));
        // write columns name
        writer.append("product name; brand; color; price; article ID\r\n");
        // write products
        for (ProductItem p : productItems) {
            writer.append(p.toCSVString());
        }
        writer.append("Total: " + productItems.size() + " items.");
        writer.flush();
        writer.close();

        System.out.println("Parse time (minutes) : " + (Calendar.getInstance().getTimeInMillis() - start) / 60000);
    }


}


