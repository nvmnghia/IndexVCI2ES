package util;

public class StringUtl {
    public static int[] listStrToIntArr(String str) {
        if (str.endsWith(",")) {
            str = str.substring(0, str.length() - 1);
        }
        String[] temp = str.split(", ");
        int[] id = new int[temp.length];

        for (int i = 0; i < id.length; ++i) {
            id[i] = Integer.valueOf(temp[i]);
        }

        return id;
    }
}
