package cbg.BrendeEr.RPG;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Erik Brendel
 */
public class RPGMap {

    private final RPGMapSegment[][] segmentMap;
    private Point dimensions = new Point(10, 10); //how much segments
    private final String name;
    public final ArrayList<RPGMapEntity> entities = new ArrayList<>();
    private final int maxSegmentsViewable;

    public RPGMap(String name, int width, int height, int maxSegmentsViewable) {
        dimensions = new Point(width, height);
        segmentMap = new RPGMapSegment[width][height];
        this.name = name;
        this.maxSegmentsViewable = maxSegmentsViewable;
    }

    public RPGMapSegment getSegment(int x, int y) {
        return segmentMap[x][y];
    }

    public void setSegment(int x, int y, RPGMapSegment segment) {
        segmentMap[x][y] = segment;
    }

    public int getMaxSegmentsViewable() {
        return maxSegmentsViewable;
    }

    public void setDimensions(Point newDimensions) {
        dimensions = newDimensions;
    }

    /**
     * returns the dimensions of mapSegments[][] --> no pixel value!
     *
     * @return the count of segments
     */
    public Point getDimension() {
        return dimensions;
    }

    public BufferedImage getImage() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int side = (int) (screenSize.getWidth() / (double) (maxSegmentsViewable * 2 + 1));

        Point segmentSize = new Point(side, side); // in pixels

        BufferedImage img = new BufferedImage(dimensions.x * segmentSize.x, dimensions.y * segmentSize.y, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();

        for (int x = 0; x < dimensions.x; x++) {
            for (int y = 0; y < dimensions.y; y++) {
                int imgX = x * segmentSize.x;
                int imgY = y * segmentSize.y;
                segmentMap[x][y].setDimensions(segmentSize);
                g.drawImage(segmentMap[x][y].getImage(), imgX, imgY, null);

            }
        }
        return img;
    }

    public BufferedImage getTopImage() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int side = (int) (screenSize.getWidth() / (double) (maxSegmentsViewable * 2 + 1));

        Point segmentSize = new Point(side, side); // in pixels

        BufferedImage img = new BufferedImage(dimensions.x * segmentSize.x, dimensions.y * segmentSize.y, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        for (int x = 0; x < dimensions.x; x++) {
            for (int y = 0; y < dimensions.y; y++) {
                int imgX = x * segmentSize.x;
                int imgY = y * segmentSize.y;
                segmentMap[x][y].setDimensions(segmentSize);
                g.drawImage(segmentMap[x][y].getTopImage(), imgX, imgY, null);

            }
        }
        return img;
    }

    public String getName() {
        return name;
    }

    /**
     *
     * Loads a map from a String.
     *
     * Structure:
     * "BrendeErRPGMap";mapName;(comment);colsCount(x);rowsCount(y);oneRow;nextRow;end;
     * while one row is like segmentNamesegmentNamesegmentName..... name should
     * be max 50 letters long
     *
     * spaces will be removed (" " --> "")
     *
     * @param mapContent the file-content. Please load file manually
     * @param segmentAssignment a HashMap of String (the mapSegment name in the
     * File) and MapSegment (the assigned object)
     * @param entityAssignment a HashMap for EntityName - Entity
     * @return
     */
    public static RPGMap loadMap(String mapContent, HashMap<String, RPGMapSegment> segmentAssignment, HashMap<String, RPGMapEntity> entityAssignment) {
        mapContent = mapContent.replaceAll(" ", "");
        //System.out.println("mapContent = " + mapContent);
        RPGMapSegment errorSegment = new RPGMapSegment(Color.RED, true);
        String[] mapData = mapContent.split(";");
        for (String line : mapData) {
            //System.out.println("DataLine: \"" + line + "\"");
        }

        int mapWidth = Integer.valueOf(mapData[4]);
        int mapHeight = Integer.valueOf(mapData[5]);

        //load all segments
        RPGMap map = new RPGMap(mapData[1], mapWidth, mapHeight, Integer.valueOf(mapData[3]));
        for (int row = 0; row < mapHeight; row++) {
            //for each row
            String rowNow = mapData[6 + row];
            String symbols[] = new String[mapWidth];
            int startIndex = 0;
            int symbolIndex = 0;
            int loops = 0;
            //split it into the segment-names
            while (symbolIndex < (mapWidth - 0)) { //bis symbols[] foll ist
                for (int sLength = 10; sLength > 0; sLength--) { //maximum length of 50 symbols for one segmentName
                    String sNow;
                    try {
                        sNow = rowNow.substring(startIndex, startIndex + sLength);
                    } catch (Exception ex) {//rowNow too short
                        sNow = rowNow.substring(startIndex);
                    }
                    //System.out.println("sNow = " + sNow);
                    if (segmentAssignment.keySet().contains(sNow)) {
                        //FOUND ONE!
                        symbols[symbolIndex] = sNow;
                        symbolIndex++;
                        startIndex += sLength;
                        break;
                    }
                }
                loops++;
                if (loops >= 100) {
                    System.err.println("Loading error --> no mapSegment found!");
                    symbols[symbolIndex] = "ERROR";
                    symbolIndex++;
                    startIndex += 1;
                }
            }

            for (int symbol = 0; symbol < mapWidth; symbol++) {
                //String token = String.valueOf(mapData[6 + row].toCharArray()[symbol]);
                String token = symbols[symbol];
                RPGMapSegment loaded = segmentAssignment.get(token);

                if (loaded == null) {
                    loaded = errorSegment;
                }
                map.setSegment(symbol, row, loaded);

            }
        }

        //load all o' dem entities
        int entityID = 6 + mapHeight;
        while (!mapData[entityID].equals("end")) {
            String entityString[] = mapData[entityID].split(":");
            String eName = entityString[0];
            for (int i = 1; i < entityString.length; i++) {
                String eCoords[] = entityString[i].split("-");
                int x = Integer.valueOf(eCoords[0]);
                int y = Integer.valueOf(eCoords[1]);
                RPGMapEntity e = entityAssignment.get(eName).createNew();
                e.setLocation(new Point(x, y));
                map.entities.add(e);
            }
            entityID++;
        }

        return map;
    }
}
