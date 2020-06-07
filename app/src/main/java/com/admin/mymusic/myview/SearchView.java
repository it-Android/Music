package com.admin.mymusic.myview;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.ListPopupWindow;

import com.admin.mymusic.R;
import com.admin.mymusic.network.DownLoad;
import com.admin.mymusic.utils.Setting;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/4/3 17:32
 **/
public class SearchView extends RelativeLayout implements View.OnClickListener {
    private TextView textView;
    private EditText editText;
    private ImageView imageView;
    private FrameLayout frameLayout;
    private ListPopupWindow mListPop;
    private SearchAdapter adapter;//适配器
    private List<String> list = new ArrayList<>();//智能搜索的数据
    private OnSearchListen onSearchListen;//搜索监听
    private Gson gson = new Gson();

    public void setOnSearchListen(OnSearchListen onSearchListen) {
        this.onSearchListen = onSearchListen;
    }

    public SearchView(Context context) {
        super(context);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inivUiViews(context);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void closekeyboard() {
        InputMethodManager inputmanger = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmanger.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        mListPop.dismiss();
        editText.clearFocus();//清除焦点
    }

    private void inivUiViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.searchview_layout, this, true);
        frameLayout = findViewById(R.id.search_view_box);
        textView = findViewById(R.id.search_view_title);
        editText = findViewById(R.id.search_view_input);
        imageView = findViewById(R.id.search_view_close);
        textView.setOnClickListener(this);
        imageView.setOnClickListener(this);
        adapter = new SearchAdapter(list);
        mListPop = new ListPopupWindow(context);
        mListPop.setAdapter(adapter);
        mListPop.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        mListPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mListPop.setBackgroundDrawable(null);
        mListPop.setAnchorView(editText);//设置ListPopupWindow的锚点，即关联PopupWindow的显示位置和这个锚点
        mListPop.setModal(false);//设置是否是模式
        //mListPop.show();
        //点击
        mListPop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                editText.setText(list.get(position));//修改editview的数据
                editText.setSelection(editText.getText().length());//光标移动到最后面
                closekeyboard();//关闭软键盘
                if (onSearchListen != null) {
                    onSearchListen.onItemClick(position, list.get(position));
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.e("数据0",s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.e("数据1",s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (str == null || str.equals("")) {
                    imageView.setVisibility(GONE);
                    if (mListPop.isShowing()) {
                        mListPop.dismiss();
                        list.clear();
                    }
                } else {
                    if (imageView.getVisibility() != VISIBLE) {
                        imageView.setVisibility(VISIBLE);
                    }
                    if (!mListPop.isShowing()) {
                        try {
                        mListPop.show();
                        }catch (Exception e){
                        }
                    }
                }
                if (onSearchListen != null) {
                    onSearchListen.onTextChange(str);
                }
                try {
                    getData(str);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        editText.setOnEditorActionListener((v, id, even) -> {
            if (id == EditorInfo.IME_ACTION_SEARCH) {
                //关闭软键盘
                closekeyboard();
                if (onSearchListen != null) {
                    onSearchListen.onClick(v, editText.getText().toString());
                }
                return true;
            }
            return false;
        });
    }

    private boolean isKo = true;

    private void getData(String str) throws UnsupportedEncodingException {
        if (str == null || str.equals("")) return;
        if (!isKo) return;
        isKo = false;
        Headers headers = DownLoad.getHeaders(Setting.set().getCsrf());
        headers = headers.newBuilder().add("Referer", "http://www.kuwo.cn/search/list").build();
        Call call = DownLoad.okGet("http://www.kuwo.cn/api/www/search/searchKey?key=" + URLEncoder.encode(str, "UTF-8"), headers);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isKo = true;
                String edData = editText.getText().toString();
                if (edData != null && !edData.equals(str)) {
                    try {
                        getData(edData);
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                isKo = true;
                String data = response.body().string();
                KeyWork keyWork = gson.fromJson(data, KeyWork.class);
                List<String> list = new ArrayList<>();
                for (String str : keyWork.data) {
                    list.add(str.split("\r\n")[0].replace("RELWORD=", ""));
                }
                replaceData(list);
                String edData = editText.getText().toString();
                if (edData != null && !edData.equals(str)) {
                    try {
                        getData(edData);
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_view_title:
                if (mListPop.isShowing())
                    mListPop.dismiss();
                if (this.onSearchListen != null) {
                    this.onSearchListen.onClick(v, editText.getText().toString());
                }
                break;
            case R.id.search_view_close:
                editText.setText("");
                closekeyboard();
                break;
        }
    }

    public void replaceData(List<String> list) {
        this.list.clear();
        this.list.addAll(list);
        ((Activity) getContext()).runOnUiThread(() -> {
            adapter.notifyDataSetChanged();
        });
    }

    public void addData(String data) {
        this.list.add(data);
        ((Activity) getContext()).runOnUiThread(() -> {
            adapter.notifyDataSetChanged();
        });
    }

    //获取输入框数据
    public String getText() {
        return editText.getText().toString();
    }

    //设置输入框数据
    public void setText(String data) {
        editText.setText(data);
    }

    public interface OnSearchListen {
        void onClick(View view, String data);

        void onTextChange(String str);

        void onItemClick(int position, String str);
    }

    private class KeyWork {
        private int code;
        private long curTime;
        private String msg;
        private String profileId;
        private String reqId;
        private List<String> data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public long getCurTime() {
            return curTime;
        }

        public void setCurTime(long curTime) {
            this.curTime = curTime;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getProfileId() {
            return profileId;
        }

        public void setProfileId(String profileId) {
            this.profileId = profileId;
        }

        public String getReqId() {
            return reqId;
        }

        public void setReqId(String reqId) {
            this.reqId = reqId;
        }

        public List<String> getData() {
            return data;
        }

        public void setData(List<String> data) {
            this.data = data;
        }
    }

}
