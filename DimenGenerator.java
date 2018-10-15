import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

public class DimenGenerator {
	 /**
     * design width
     */
    private static final int DESIGN_WIDTH = 720;
	 /**
     * design height
     */
    private static final int DESIGN_HEIGHT = 1080;

    public enum DimenTypes {
		// add you dimen , 300-460 every 10
        DP_sw__300(300),
        DP_sw__310(310),
        DP_sw__320(320),
        DP_sw__330(330),
        DP_sw__340(340),
        DP_sw__350(350),
	DP_sw__360(360),
	DP_sw__370(370),
	DP_sw__380(380),
        DP_sw__390(390);

        private int swWidthDp;

        DimenTypes(int swWidthDp) {
            this.swWidthDp = swWidthDp;
        }

        public int getSwWidthDp() {
            return swWidthDp;
        }

        public void setSwWidthDp(int swWidthDp) {
            this.swWidthDp = swWidthDp;
        }
    }

    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n";
    private static final String XML_RESOURCE_START = "<resources>\r\n";
    private static final String XML_RESOURCE_END = "</resources>\r\n";
    private static final String XML_DIMEN_TEMPLETE = "<dimen name=\"qb_%1$spx_%2$d\">%3$.2fdp</dimen>\r\n";

    private static final String XML_BASE_DPI = "<dimen name=\"base_dpi\">%ddp</dimen>\r\n";
    private static final int MAX_SIZE = 1080;

    private static final String dirStr = "./res";
    private static final String XML_NAME = "dimens.xml";

    public static void main(String[] args) {
        int smallest = DESIGN_WIDTH > DESIGN_HEIGHT ? DESIGN_HEIGHT : DESIGN_WIDTH;
        DimenTypes[] values = DimenTypes.values();
        for (DimenTypes value : values) {
            File directory = new File(dirStr);
            if (!directory.exists()) {
                directory.mkdir();
            }
            makeAll(smallest, value, directory.getAbsolutePath());
        }
    }

    private static String makeAllDimens(DimenTypes type, int designWidth) {
        float dpValue;
        String temp;
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(XML_HEADER);
            sb.append(XML_RESOURCE_START);
            temp = String.format(XML_BASE_DPI, type.getSwWidthDp());
            sb.append(temp);
            for (int i = 0; i <= MAX_SIZE; i++) {

                dpValue = px2dip((float) i, type.getSwWidthDp(), designWidth);
                temp = String.format(XML_DIMEN_TEMPLETE, "", i, dpValue);
                sb.append(temp);
            }

            sb.append(XML_RESOURCE_END);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    private static void makeAll(int designWidth, DimenTypes type,
                                String buildDir) {
        try {
            final String folderName;
            if (type.getSwWidthDp() > 0) {
                folderName = "values-sw" + type.getSwWidthDp() + "dp";
            } else {
                return;
            }

            File file = new File(buildDir + File.separator + folderName);
            if (!file.exists()) {
                file.mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(file.getAbsolutePath()
                    + File.separator + XML_NAME);
            fos.write(makeAllDimens(type, designWidth).getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static float px2dip(float pxValue, int sw, int designWidth) {
        float dpValue = (pxValue / (float) designWidth) * sw;
        BigDecimal bigDecimal = new BigDecimal(dpValue);
        float finDp = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP)
                .floatValue();
        return finDp;
    }
}
