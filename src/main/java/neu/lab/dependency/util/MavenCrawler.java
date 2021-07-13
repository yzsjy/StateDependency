package neu.lab.dependency.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * the crawler to acquire all versions of a specify dependency from the maven central
 * @author yzsjy
 */
public class MavenCrawler {

    private static final String mavenArtifactUrl = "https://repo1.maven.org/maven2/";

    public static ArrayList<String> getVersionList(String depInfo) {
        ArrayList<String> versionList = new ArrayList<>();
        String MvnRepositoryUrl = "https://mvnrepository.com/artifact/";
        String MavenRepoUrl = "https://repo1.maven.org/maven2/";
        String groupId = depInfo.split(":")[0];
        String artifactId = depInfo.split(":")[1];
        String artifactUrl_1 = MvnRepositoryUrl + groupId + "/" + artifactId;
        String artifactUrl_2 = MavenRepoUrl + groupId.replace(".", "/") + "/" + artifactId.replace(".", "/") + "/";
        Document document = null;
        boolean canVisit = true;
        try {
            document = Jsoup.connect(artifactUrl_1).userAgent("Mozilla").timeout(5000).get();
        } catch (Exception e) {
            MavenUtil.i().getLog().warn("MvnRepository can not be visited!");
//            System.out.println("MvnRepository can not be visited!");
            canVisit = false;
        }
        if (canVisit && document != null) {
            Elements gridVersions = document.getElementsByClass("grid versions");
            for (Element tbody : gridVersions.select("tbody")) {
                for (Element td : tbody.select(".vbtn")) {
                    versionList.add(td.text());
                }
            }
            return versionList;
        } else {
            try {
                document = Jsoup.connect(artifactUrl_2).userAgent("Mozilla").timeout(5000).get();
                canVisit = true;
            } catch (Exception e) {
                MavenUtil.i().getLog().warn("Maven Repo can not be visited!");
//                System.out.println("Maven Repo can not be visited!");
                canVisit = false;
            }
        }

        if (canVisit && document != null) {
            Elements gridVersions = document.select("a");
            for (Element tbody : gridVersions) {
                if (tbody.text().equals("../")||tbody.text().equals("maven-metadata.xml")||tbody.text().equals("maven-metadata.xml.md5")||tbody.text().equals("maven-metadata.xml.sha1")
                        || tbody.text().equals("maven-metadata.xml.sha256") || tbody.text().equals("maven-metadata.xml.sha512")) {
                    continue;
                }
                versionList.add(tbody.text().split("/")[0]);
            }
            Collections.reverse(versionList);
        }
        return versionList;
    }

    /**
     * get versions of specify dependency
     * @param groupId
     * @param artifactId
     * @return List<String>
     */
    public static ArrayList<String> getMavenRepoVersionList(String groupId, String artifactId) {
        String artifactUrl = groupId.replace(".", "/") + "/" + artifactId.replace(".", "/") + "/";
        ArrayList<String> versionList = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(mavenArtifactUrl + artifactUrl).userAgent("Mozilla").get();
            Elements gridVersions = doc.select("a");
            for (Element tbody : gridVersions) {
                if (tbody.text().equals("../")||tbody.text().equals("maven-metadata.xml")||tbody.text().equals("maven-metadata.xml.md5")||tbody.text().equals("maven-metadata.xml.sha1")
                || tbody.text().equals("maven-metadata.xml.sha256") || tbody.text().equals("maven-metadata.xml.sha512")) {
                    continue;
                }
                versionList.add(tbody.text().split("/")[0]);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return versionList;
    }

    /**
     * the url of the specify dependency
     * @param groupId
     * @param artifactId
     * @return
     */
    public static String getUrl(String groupId, String artifactId) {
        String artifactUrl = groupId.replace(".", "/") + "/" + artifactId.replace(".", "/") + "/";
        return mavenArtifactUrl + artifactUrl;
    }

    /**
     * another way to acquire the version
     * @param preNode
     * @return
     */
    public static ArrayList<String> getMvnRepositoryVersionList(String preNode) {
        String mavenUrl = "https://mvnrepository.com/artifact/";
        String groupId = preNode.split(":")[0];
        String artifactId = preNode.split(":")[1];
        String artifactUrl = groupId + "/" + artifactId;
        Document html = null;
        ArrayList<String> versionList = new ArrayList<>();
        try {
            //暂停一秒，防止被反爬
            Thread.sleep(1000);
//            MavenUtil.i().getLog().info("artifact url : " + mavenUrl + artifactUrl);
            System.out.println("artifact url : " + mavenUrl + artifactUrl);
            html = Jsoup.connect(mavenUrl + artifactUrl).userAgent("Mozilla").timeout(5000).get();
        } catch (Exception e) {
//            MavenUtil.i().getLog().error("connect error, message : " + e.getMessage());
            System.out.println("connect error, message : " + e.getMessage());
            return versionList;
        }
        if (html != null) {
            Elements gridVersions = html.getElementsByClass("grid versions");
            for (Element tbody : gridVersions.select("tbody")) {
                for (Element td : tbody.select(".vbtn")) {
                    versionList.add(td.text());
                }
            }
        }
        return versionList;
    }

    /**
     * test the connect of the crawler
     * @param args
     */
    public static void main(String[] args) {
        String groupId = "com.google.guava";
        String artifactId = "guava";
        String preNode = "com.google.guava:guava";
//        List<String> versionList = getMavenRepoVersionList(groupId, artifactId);
//        List<String> versionList = getMavenRepoVersionList(preNode);
        List<String> versionList = getVersionList(preNode);
        for (String version : versionList) {
            System.out.println(version);
        }
    }
}
