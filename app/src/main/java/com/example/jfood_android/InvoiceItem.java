package com.example.jfood_android;

public class InvoiceItem {

        private String mText0;
        private String mText1;
        private String mText2;

        public InvoiceItem(String text0, String text1, String text2) {
            mText0 = text0;
            mText1 = text1;
            mText2 = text2;
        }

        public String getText0() {
            return mText0;
        }

        public String getText1() {
            return mText1;
        }

        public String getText2() {
            return mText2;
        }

}
