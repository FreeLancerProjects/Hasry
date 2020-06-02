package com.hasry.interfaces;
public interface Listeners {
    interface BackListener
    {
        void back();
    }
    interface LoginListener{
        void validate();
        void showCountryDialog();
    }

    interface SignUpListener{

        void openSheet();
        void closeSheet();
        void checkDataValid();
        void checkReadPermission();
        void checkCameraPermission();
    }

    interface SettingActions
    {
        void contactUs();
        void terms();
        void aboutApp();
        void logout();
        void share();
        void rateApp();
        void arLang();
        void enLang();

    }

    interface HomeListener{

        void main();
        void profile();
        void myOrder();
        void notification();
        void cart();
        void more();
    }


    interface NextPreviousAction
    {
        void onNext();
        void onPrevious();
    }




}
