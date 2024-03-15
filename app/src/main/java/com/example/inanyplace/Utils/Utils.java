package com.example.inanyplace.Utils;

import android.graphics.Color;

import com.example.inanyplace.Model.Addon;
import com.example.inanyplace.Model.Category;
import com.example.inanyplace.Model.Food;
import com.example.inanyplace.Model.Restaurant;
import com.example.inanyplace.Model.Size;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

public class Utils {

    public static final String COMMNET_REF = "Comments";
    public static final String CATEGORY_REF = "Category";
    public static final String ORDER_REF = "Orders";
    public static final String RESTAURANT_REF = "Restaurant";
    public static final String POPULAR_CATEGORY_REF = "MostPopular";
    public static final String BEST_DEAL_REF = "BestDeals";
    public static Category categorySelected;
    public static Food selectedFood;
    public static Restaurant currentRestaurant;

    public static String formatPrice(double price) {
        if (price != 0) {
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            decimalFormat.setRoundingMode(RoundingMode.UP);
            String finalPrice = new StringBuilder(decimalFormat.format(price)).toString();
            return finalPrice.replace(".", ",");
        } else
            return "0.00";
    }

    public static Double calculatedExtraPrice(Size userSelectedSize, List<Addon> userSelectedAddon) {
        Double result = 0.0;
        if (userSelectedSize == null && userSelectedAddon == null) {
            return 0.0;
        } else if (userSelectedSize == null) {
            for (Addon addonModel : userSelectedAddon)
                result += addonModel.getPrice();
            return result;
        } else if (userSelectedAddon == null) {
            return userSelectedSize.getPrice() * 1.0;
        } else {
            //If both is selected
            result = userSelectedSize.getPrice() * 1.0;
            for (Addon addonModel : userSelectedAddon)
                result += addonModel.getPrice();
            return result;
        }
    }

    public static String createOrderNumber() {
        return new StringBuilder()
                .append(System.currentTimeMillis())
                .append(Math.abs(new Random().nextInt(50))).toString();
    }

    public static String getDateOfWeek(int i) {
        switch (i) {
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            case 7:
                return "Sunday";
            default:
                return "Unknow";

        }
    }

    public static String getMonthOfYear(int i) {
        switch (i) {
            case 0:
                return "Jan";
            case 1:
                return "Feb";
            case 2:
                return "Mar";
            case 3:
                return "Apr";
            case 4:
                return "May";
            case 5:
                return "Jun";
            case 6:
                return "Jul";
            case 7:
                return "Aug";
            case 8:
                return "Sep";
            case 9:
                return "Oct";
            case 10:
                return "Nov";
            case 11:
                return "Dec";
            default:
                return "Unknow";

        }

    }

    public static String convertStatusToText(int orderStatus) {
        switch (orderStatus) {
            case 0:
                return "Placed";
            case 1:
                return "Shipping";
            case 2:
                return "Delivered";
            default:
                return "Unknow";
        }
    }

    public static int checkStatus(int orderStatus) {
        switch (orderStatus) {
            case 0:
                return Color.parseColor("#FF0000");
            case 1:
                return Color.parseColor("#FFFF00");
            case 2:
                return Color.parseColor("#008000");
            default:
                return -1;
        }
    }
}
