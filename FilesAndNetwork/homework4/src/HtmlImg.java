import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class HtmlImg
{
    public static void downloadAllImg(String url, String path)
    {
        Document document;
        try
        {
            document = Jsoup.connect(url).get();
            List<ImgInfo> imgInfoList = getImgLinks(document);
            for (ImgInfo imgInfo : imgInfoList)
            {
                InputStream in = new URL(imgInfo.getLink()).openStream();
                Files.copy(in, Paths.get(path + "/" + imgInfo.getName()), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    private static List<ImgInfo> getImgLinks(Document document)
    {
        ArrayList<ImgInfo> imgInfoList = new ArrayList<>();

        Elements elements = document.select("img");
        elements.forEach(e ->
        {
            String link = e.attr("abs:src");
            String[] tags = link.split("/");
            if (link.matches("http.*"))
            {
                imgInfoList.add(new ImgInfo(tags[tags.length - 1], link));
            }
        });
        return  imgInfoList;
    }
}
