package sailboatScraper.services.implementations;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import sailboatScraper.entities.Sailboat;
import sailboatScraper.services.ScrappingService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SailboatScrappingServiceImpl implements ScrappingService {

    @Override
    public List<Sailboat> scrapeSite() {

        List<Sailboat> sailboats = new ArrayList<>();
        Document document;

        try {
            document = Jsoup.connect("https://www.yachtworld.com/core/listing/cache/searchResults.jsp?cit=true&slim=quick&sm=3&searchtype=advancedsearch&Ntk=boatsEN&hmid=0&ftid=0&enid=0&type=Sail&fromLength=38&toLength=45&fromYear=2013&toYear=2019&fromPrice=100000&toPrice=300000&luom=126&currencyid=100&boatsAddedSelected=-1&fracts=1").get();
        } catch (IOException ioe) {
            return sailboats;
        }

        Elements listings = document.getElementsByClass("information");

        for (Element listing : listings) {

            Elements makeModelElements = listing.getElementsByClass("make-model");

            if (makeModelElements.isEmpty()) {
                continue;
            }

            Elements priceElements = listing.getElementsByClass("price");

            if (priceElements.isEmpty()) {
                continue;
            }

            Elements locationElements = listing.getElementsByClass("location");

            if (locationElements.isEmpty()) {
                continue;
            }

            sailboats.add(
                    new Sailboat(
                            getId(makeModelElements.get(0)),
                            getLength(makeModelElements.get(0)),
                            getYear(makeModelElements.get(0)),
                            getMake(makeModelElements.get(0)),
                            getModel(makeModelElements.get(0)),
                            getPrice(priceElements.get(0)),
                            getLocation(locationElements.get(0)),
                            LocalDateTime.now()));
        }

        return sailboats;
    }

    private long getId(Element makeModelElement) {

        String linkHref = makeModelElement.selectFirst("a").attr("href");
        String[] linkNodes = linkHref.split("-");
        String rawId = linkNodes[linkNodes.length - 1];
        String id = rawId.substring(0, rawId.length() - 1);

        return Long.valueOf(id);
    }

    private float getLength(Element makeModelElement) {

        String rawLength = makeModelElement.getElementsByClass("length feet").get(0).text();
        String[] rawLengthParts = rawLength.split(" ");

        return Integer.valueOf(rawLengthParts[0]);
    }

    private int getYear(Element makeModelElement) {

        String linkText = makeModelElement.selectFirst("a").text();
        String[] linkTextNodes = linkText.split(" ");

        return Integer.valueOf(linkTextNodes[2]);
    }

    private String getMake(Element makeModelElement) {

        String linkText = makeModelElement.selectFirst("a").text();
        String[] linkTextNodes = linkText.split(" ");

        return linkTextNodes[3];
    }

    private String getModel(Element makeModelElement) {

        String linkText = makeModelElement.selectFirst("a").text();
        int index = StringUtils.ordinalIndexOf(linkText, " ", 4);

        return linkText.substring(index + 1);
    }

    private float getPrice(Element priceElement) {
        return 202413;
    }

    private String getLocation(Element locationElement) {

        return locationElement.text();
    }
}
