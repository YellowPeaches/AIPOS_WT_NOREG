package com.wintec.lamp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextTools {
    private static final int SUB_LONG = 15;


    public static boolean checkIsEmpty(String content) {
        return null == content || content.replace(" ", "").equals("");
    }

    public static void setSelection(AppCompatEditText compatEditText) {
        if (null != compatEditText && null != compatEditText.getText() && !checkIsEmpty(compatEditText.getText().toString())) {
            compatEditText.setSelection(compatEditText.getText().toString().length());
        }
    }

    /**
     * 时间比较 如 08:20和20:45相比较(其他格式不支持)
     *
     * @param startTime
     * @param endTime
     * @return true 结束时间大于开始时间 false小于开始时间
     */
    public static boolean compareTime(String startTime, String endTime) {
        if (!TextTools.checkIsEmpty(startTime) && !TextTools.checkIsEmpty(endTime)) {
            if (startTime.contains(":") && endTime.contains(":")) {
                String startReplace = startTime.replace(":", "");
                String endReplace = endTime.replace(":", "");
                char[] startChars = startReplace.toCharArray();
                char[] endChars = endReplace.toCharArray();
                for (int i = 0; i < startChars.length; i++) {
                    if ((int) endChars[i] > (int) startChars[i]) {
                        return true;
                    } else if ((int) endChars[i] < (int) startChars[i]) {
                        return false;
                    }
                }
                return false;
            }
        }
        return false;
    }

    /**
     * 动态设置shape(带边框)
     *
     * @param view
     */
    public static void setShapeDrawable(Context context, View view, int bgColor, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(DensityUtil.dip2px(context, 2));
        drawable.setStroke(DensityUtil.dip2px(context, 0.5f), ContextCompat.getColor(context, strokeColor));
        drawable.setColor(ContextCompat.getColor(context, bgColor));
        view.setBackground(drawable);
    }

    /**
     * 动态设置shape(没有边框)
     *
     * @param view
     */
    public static void setShapeRadiusDrawable(Context context, float radius, View view, int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(radius);
        drawable.setColor(ContextCompat.getColor(context, color));
        view.setBackground(drawable);
    }

    /**
     * 动态设置shape(叶子形状)
     *
     * @param view
     */
    public static void setShapeDrawableLeaf(Context context, View view, int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadii(new float[]{0f, 0f, 30f, 30f, 0f, 0f, 30f, 30f});
        drawable.setColor(ContextCompat.getColor(context, color));
        view.setBackground(drawable);
    }

    /**
     * 超过num个字换行
     *
     * @param string
     * @return
     */
    public static String getString(String string, int num) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        getString(string, stringBuilder, num);
        return stringBuilder.toString();
    }

    private static void getString(String string, StringBuilder stringBuilder, int num) {
        if (string.length() > num) {
            String startString = string.substring(0, num);
            String endString = string.substring(num);
            stringBuilder.append(startString).append("\n");
            getString(endString, stringBuilder, num);
        } else {
            stringBuilder.append(string);
        }
    }

    /**
     * 关键字高亮
     *
     * @param textView 控件
     * @param text     整个文本
     * @param tag      关键字
     * @param color    颜色
     */
    public static void setTextSpannable(TextView textView, String text, String tag, int color) {
        SpannableString s = new SpannableString(text);
        if (!TextUtils.isEmpty(tag)) {
            String sTag = tag.toUpperCase();
            Pattern p = Pattern.compile(sTag);
            Matcher m = p.matcher(s);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        textView.setText(s);
    }

    public static void setTextSpannable(TextView textView, String text, String tag1, String tag2, int color, ClickableSpan tag1Span, ClickableSpan tag2Span) {
        SpannableString s = new SpannableString(text);
        if (!TextUtils.isEmpty(tag1)) {
            String sTag = tag1.toUpperCase();
            Pattern p = Pattern.compile(sTag);
            Matcher m = p.matcher(s);

            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(tag1Span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            }
        }
        if (!TextUtils.isEmpty(tag2)) {
            String sTag = tag2.toUpperCase();
            Pattern p = Pattern.compile(sTag);
            Matcher m = p.matcher(s);

            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(tag2Span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        textView.setText(s);
    }

    public static void setTextSpannable(AppCompatTextView textView, String text, List<String> strList, int color, List<ClickableSpan> tag1Span) {
        SpannableString s = new SpannableString(text);
        for (int i = 0; i < strList.size(); i++) {
            if (!TextUtils.isEmpty(strList.get(i))) {
                String sTag = strList.get(i).toUpperCase();
                Pattern p = Pattern.compile(sTag);
                Matcher m = p.matcher(s);
                while (m.find()) {
                    int start = m.start();
                    int end = m.end();
                    if (tag1Span != null) {
                        s.setSpan(tag1Span.get(i), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        textView.setText(s);
    }

    /**
     * 屏蔽表情输入
     *
     * @return
     */
    public static InputFilter emojiFilters() {
        return new InputFilter() {
            Pattern emoji = Pattern
                    .compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                            Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

            @Override
            public CharSequence filter(CharSequence charSequence, int start, int end, Spanned dest, int dstart, int dend) {
                if ((" ".equals(charSequence))) {
                    return "";
                }
                Matcher emojiMatcher = emoji.matcher(charSequence);
                if (emojiMatcher.find()) {
                    return "";
                }
                return null;
            }
        };
    }

    /**
     * 禁止回车换行
     *
     * @param editText
     */
    public static void shieldEditorAction(EditText editText) {
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
    }

    /**
     * 隐藏输入法
     *
     * @param activity
     */
    public static void hideInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = activity.getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * 显示输入法
     *
     * @param activity
     */
    public static void showInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = activity.getWindow().peekDecorView();
        if (null != v) {
            imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
        }
    }

    /**
     * 保留bit位小数
     *
     * @param d
     * @return 位数
     */
    public static double doubleBit(double d, int bit) {
        BigDecimal bg = new BigDecimal(d);
        return bg.setScale(bit, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 后补0
     *
     * @param num
     * @return
     */
    public static String secondString(int num) {
        return num < 10 ? "0" + num : String.valueOf(num);
    }

    /**
     * EditText默认不获取焦点，不自动弹出键盘
     * 29及以上版本会出现该问题
     */
    public static void editTextRequestFocus(EditText editText) {
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
            }
        });
    }

    /**
     * 为文字设置风格（粗体、斜体）
     */
    public static void setStyleSpan(Context context, String content, TextView tvSpan, int ColorIds) {
        SpannableString spannableString = new SpannableString(content);
        StyleSpan colorSpan = new StyleSpan(Typeface.BOLD);
        StyleSpan colorSpanit = new StyleSpan(Typeface.ITALIC);
        spannableString.setSpan(colorSpan, 0, content.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(colorSpanit, 7, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvSpan.setHighlightColor(context.getResources().getColor(ColorIds));
        tvSpan.setText(spannableString);
    }

    /**
     * 格式化空数据
     *
     * @param content
     * @return
     */
    public static String formatTextEmpty(String content) {
        if (TextTools.checkIsEmpty(content)) {
            return "--";
        }
        return content;
    }

    /**
     * 格式化P标签body
     *
     * @param body
     * @return
     */
    public static String formatHtmlP(String body, boolean isNeedHead) {
        String head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<style>img{max-width: 100%; width:auto; height:auto!important;}</style>" +
                "</head>";
        return "<html>" + (isNeedHead ? head : "") + "<body>" + body + "</body></html>";
    }

    public static String initHtml(String webHtml) {

        return "<html>\n" +
                "<head>\n" +
                "   <meta charset='utf-8' />\n" +
                "   <meta name=\"viewport\" content=\"width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no\" />\n" +
                "<style>img{max-width: 100%; width:auto; height:auto;}</style>" +
                "</head>\n" +
                "<body>\n" +
                "   <style>\n" +
                "       section {\n" +
                "           max-width: 100%;\n" +
                "       }\n" +
                "   </style>\n" +
                webHtml +
                "</body>\n" +
                "</html>";
    }

    /**
     * 根据用户名的不同长度，来进行替换 ，达到保密效果
     *
     * @param userName 用户名
     * @return 替换后的用户名
     */
    public static String userNameReplaceWithStar(String userName) {
        String userNameAfterReplaced = "";

        if (userName == null) {
            userName = "";
        }

        int nameLength = userName.length();

        if (nameLength <= 1) {
            userNameAfterReplaced = "*";
        } else if (nameLength == 2) {
            userNameAfterReplaced = replaceAction(userName, "(?<=\\w{0})\\w(?=\\w{1})");
        } else if (nameLength <= 6) {
            userNameAfterReplaced = replaceAction(userName, "(?<=\\w{1})\\w(?=\\w{1})");
        } else if (nameLength == 7) {
            userNameAfterReplaced = replaceAction(userName, "(?<=\\w{1})\\w(?=\\w{2})");
        } else if (nameLength == 8) {
            userNameAfterReplaced = replaceAction(userName, "(?<=\\w{2})\\w(?=\\w{2})");
        } else if (nameLength == 1) {
            userNameAfterReplaced = replaceAction(userName, "(?<=\\w{2})\\w(?=\\w{3})");
        } else if (nameLength == 10) {
            userNameAfterReplaced = replaceAction(userName, "(?<=\\w{3})\\w(?=\\w{3})");
        } else {
            userNameAfterReplaced = replaceAction(userName, "(?<=\\w{3})\\w(?=\\w{4})");
        }

        return userNameAfterReplaced;

    }

    /**
     * 实际替换动作
     *
     * @param username username
     * @param regular  正则
     * @return
     */
    private static String replaceAction(String username, String regular) {
        return username.replaceAll(regular, "*");
    }
}
