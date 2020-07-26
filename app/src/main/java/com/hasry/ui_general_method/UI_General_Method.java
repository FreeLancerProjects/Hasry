package com.hasry.ui_general_method;

import android.graphics.Paint;
import android.net.Uri;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.hasry.R;
import com.hasry.models.OfferModel;
import com.hasry.models.OrderModel;
import com.hasry.share.Time_Ago;
import com.hasry.tags.Tags;
import com.makeramen.roundedimageview.RoundedImageView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class UI_General_Method {

    @BindingAdapter("error")
    public static void setErrorUi(View view, String error) {
        if (view instanceof EditText) {
            EditText editText = (EditText) view;
            editText.setError(error);

        } else if (view instanceof TextView) {
            TextView textView = (TextView) view;
            textView.setError(error);

        }
    }


    @BindingAdapter("image")
    public static void image(View view, String endPoint) {
        if (view instanceof CircleImageView) {
            CircleImageView imageView = (CircleImageView) view;

            if (endPoint != null) {

                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).into(imageView);
            }
            else {
                Picasso.get().load(R.drawable.logo).into(imageView);

            }
        } else if (view instanceof RoundedImageView) {
            RoundedImageView imageView = (RoundedImageView) view;

            if (endPoint != null) {

                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).fit().into(imageView);
            }
            else {
                Picasso.get().load(R.drawable.logo).into(imageView);

            }
        } else if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;

            if (endPoint != null) {

                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).fit().into(imageView);
            }
            else {
                Picasso.get().load(R.drawable.logo).into(imageView);

            }
        }

    }


    @BindingAdapter("day")
    public static void displayDay(TextView textView, long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.ENGLISH);
        String sTime = dateFormat.format(new Date(time * 1000));
        textView.setText(sTime);
    }

    @BindingAdapter("date")
    public static void displayDate(TextView textView, long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
        String sTime = dateFormat.format(new Date(date * 1000));
        textView.setText(sTime);
    }

    @BindingAdapter("time")
    public static void displayTime(TextView textView, long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        String sTime = dateFormat.format(new Date(time * 1000));
        textView.setText(sTime);
    }


    @BindingAdapter("timeAgo")
    public static void displayTimeAgo(TextView textView, long time) {
        String timeAgo = Time_Ago.getTimeAgo(time * 1000, textView.getContext());
        textView.setText(timeAgo);
    }

    @BindingAdapter({"time", "date"})
    public static void displayDateTime(TextView textView, long times, long dates) {


        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        String time = dateFormat.format(new Date(times * 1000));


        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
        String date = dateFormat2.format(new Date(dates * 1000));

        textView.setText(String.format("%s %s %s", date, "-", time));
    }


    @BindingAdapter({"orderTime", "orderDate"})
    public static void displayOrderDate(TextView textView, long time, long date) {

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
        String d = dateFormat2.format(new Date(date * 1000));

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        String t = dateFormat.format(new Date(time * 1000));
        textView.setText(String.format("%s %s %s", d, "-", t));
    }


    @BindingAdapter("month")
    public static void displayMonth(TextView textView, long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
        String sTime = dateFormat.format(new Date(time * 1000));
        textView.setText(sTime);
    }

    @BindingAdapter({"time", "duration"})
    public static void addDate(TextView textView, String time, String duration) {
        if (time != null && !time.isEmpty() && duration != null && !duration.isEmpty()) {
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

            String[] split = duration.split(":", 0);
            int hours = 0;
            int min = 0;
            if (Integer.parseInt(split[0]) > 0) {
                hours = Integer.parseInt(split[0]);
            }

            if (Integer.parseInt(split[1]) > 0) {
                min = Integer.parseInt(split[1]);
            }

            try {
                Date date = dateFormat1.parse(time);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.HOUR_OF_DAY, hours);
                calendar.add(Calendar.MINUTE, min);


                SimpleDateFormat dateFormat3 = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
                String d = dateFormat3.format(new Date(calendar.getTimeInMillis()));

                textView.setText(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }


    @BindingAdapter("time_AM_BM")
    public static void timeAM_BM(TextView textView, String time_AM_BM) {
        if (time_AM_BM != null && !time_AM_BM.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

            try {
                Date date = dateFormat.parse(time_AM_BM);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
                String d = dateFormat2.format(new Date(calendar.getTimeInMillis()));
                textView.setText(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    @BindingAdapter("notification_date")
    public static void notificationDate(TextView textView, String date) {
        if (date != null && !date.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy\nhh:mm aa", Locale.ENGLISH);

            String d = dateFormat.format(new Date(Long.parseLong(date) * 1000));
            textView.setText(d);
        }

    }


    @BindingAdapter("order_status")
    public static void orderStatus(TextView textView,String status) {
        if (status.equals("new_order")){
            textView.setText(textView.getContext().getString(R.string.new_order));
        }else if (status.equals("driver_accept")){
            textView.setText(textView.getContext().getString(R.string.accepted));

        }else if (status.equals("driver_delivery")){
            textView.setText(textView.getContext().getString(R.string.in_way));

        }else if (status.equals("driver_refuser")){
            textView.setText(textView.getContext().getString(R.string.refused));

        }
        else if (status.equals("driver_end")){
            textView.setText(textView.getContext().getString(R.string.completed));

        } else if (status.equals("marketer_accept")){
            textView.setText(textView.getContext().getString(R.string.marker_accept));

        }

    }

    @BindingAdapter({"price"})
    public static void productDiscount(TextView textView, OrderModel.OrderDetails model) {
        if (model.getOffer()==null){
            textView.setText(String.format(Locale.ENGLISH,"%s %s",model.getProduct_info().getPrice(),textView.getContext().getString(R.string.sar)));

        }else {
            if (model.getOffer().getOffer_status().equals("open")){
                if (model.getOffer().getOffer_type().equals("per")){
                    double price_before_discount = Double.parseDouble(model.getProduct_info().getPrice());
                    double price =  -(price_before_discount*(model.getOffer().getOffer_value()/100));
                    textView.setText(String.format(Locale.ENGLISH,"%s %s",String.valueOf(price),textView.getContext().getString(R.string.sar)));
                }else {
                    double price_before_discount = Double.parseDouble(model.getProduct_info().getPrice());
                    double price =  price_before_discount-model.getOffer().getOffer_value();
                    textView.setText(String.format(Locale.ENGLISH,"%s %s",String.valueOf(price),textView.getContext().getString(R.string.sar)));

                }
            }else {
                textView.setText(String.format(Locale.ENGLISH,"%s %s",model.getProduct_info().getPrice(),textView.getContext().getString(R.string.sar)));

            }

        }

    }


    @BindingAdapter({"priceOffer"})
    public static void productOfferDiscount(TextView textView, OfferModel model) {
        if (model.getOffer()==null){
            textView.setText(String.format(Locale.ENGLISH,"%s %s",model.getPrice(),textView.getContext().getString(R.string.sar)));

        }else {
            if (model.getOffer().getOffer_status().equals("open")){
                if (model.getOffer().getOffer_type().equals("per")){
                    double price_before_discount = Double.parseDouble(model.getPrice());
                    double price = price_before_discount- (price_before_discount*(model.getOffer().getOffer_value()/100));
                    textView.setText(String.format(Locale.ENGLISH,"%s %s",String.format(Locale.ENGLISH,"%.1f",price),textView.getContext().getString(R.string.sar)));

                }else {
                    double price_before_discount = Double.parseDouble(model.getPrice());
                    double price =  price_before_discount-model.getOffer().getOffer_value();
                    textView.setText(String.format(Locale.ENGLISH,"%s %s",String.format(Locale.ENGLISH,"%.1f",price),textView.getContext().getString(R.string.sar)));

                }
            }else {
                textView.setText(String.format(Locale.ENGLISH,"%s %s",model.getPrice(),textView.getContext().getString(R.string.sar)));

            }

        }

    }

    @BindingAdapter({"priceBeforeDiscount"})
    public static void productDiscount(TextView textView, OfferModel model) {
       textView.setText(String.format(Locale.ENGLISH,"%s %s",model.getPrice(),textView.getContext().getString(R.string.sar)));
       textView.setPaintFlags(textView.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);

    }

}
