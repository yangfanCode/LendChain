package com.lend.lendchain.ui.activity.common;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.singleton.CountryCode;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.widget.TitledListView;
import com.lend.lendchain.widget.countrycode.CharacterParserUtil;
import com.lend.lendchain.widget.countrycode.CountryComparator;
import com.lend.lendchain.widget.countrycode.CountrySortAdapter;
import com.lend.lendchain.widget.countrycode.CountrySortModel;
import com.lend.lendchain.widget.countrycode.GetCountryNameSort;
import com.lend.lendchain.widget.countrycode.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 国家码选择界面
 */

public class SelectCountryCodeActivity extends BaseActivity {

    private List<CountrySortModel> mAllCountryList;
    private CountrySortAdapter adapter;

    private CountryComparator pinyinComparator;
    private GetCountryNameSort countryChangeUtil;
    private CharacterParserUtil characterParserUtil;
    @BindView(R.id.country_et_search)
    EditText etSerch;
    @BindView(R.id.country_lv_list)
    TitledListView lv;
    @BindView(R.id.country_sidebar)
    SideBar sideBar;
    @BindView(R.id.country_iv_cleartext)
    ImageView country_iv_clearText;
    @BindView(R.id.country_tvDialog)
    TextView tvDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_country_code);
        StatusBarUtil.setStatusBarColor(SelectCountryCodeActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(SelectCountryCodeActivity.this);
    }


    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setTitle(getString(R.string.select_country_area));
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        initData();
        getCountryList();
        initListener();
    }


    private void initData() {
        mAllCountryList = new ArrayList<CountrySortModel>();
        pinyinComparator = new CountryComparator();
        countryChangeUtil = new GetCountryNameSort();
        characterParserUtil = new CharacterParserUtil();

        // 将联系人进行排序，按照A~Z的顺序
        Collections.sort(mAllCountryList, pinyinComparator);
        adapter = new CountrySortAdapter(this, mAllCountryList);
        lv.setAdapter(adapter);
        sideBar.setTextView(tvDialog);
    }

    private void initListener() {
        country_iv_clearText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                etSerch.setText("");
                Collections.sort(mAllCountryList, pinyinComparator);
                adapter.updateListView(mAllCountryList);
            }
        });

        etSerch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchContent = etSerch.getText().toString();
                if (searchContent.equals("")) {
                    country_iv_clearText.setVisibility(View.INVISIBLE);
                } else {
                    country_iv_clearText.setVisibility(View.VISIBLE);
                }

                if (searchContent.length() > 0) {
                    // 按照输入内容进行匹配
                    ArrayList<CountrySortModel> fileterList = (ArrayList<CountrySortModel>) countryChangeUtil
                            .search(searchContent, mAllCountryList);

                    adapter.updateListView(fileterList);
                } else {
                    adapter.updateListView(mAllCountryList);
                }
                lv.setSelection(0);
            }
        });

        // 右侧sideBar监听
        sideBar.setOnTouchingLetterChangedListener((SideBar.OnTouchingLetterChangedListener) s -> {
            // 该字母首次出现的位置
            int position = adapter.getPositionForSection(s.charAt(0));
            if (position != -1) {
                lv.setSelection(position);
            }
        });

        lv.setOnItemClickListener((adapterView, view, position, arg3) -> {
            String countryName = null;
            String countryNumber = null;
            String searchContent = etSerch.getText().toString();

            if (searchContent.length() > 0) {
                // 按照输入内容进行匹配
                ArrayList<CountrySortModel> fileterList = (ArrayList<CountrySortModel>) countryChangeUtil
                        .search(searchContent, mAllCountryList);
                countryName = fileterList.get(position).countryName;
                countryNumber = fileterList.get(position).countryNumber;
            } else {
                // 点击后返回
                countryName = mAllCountryList.get(position).countryName;
                countryNumber = mAllCountryList.get(position).countryNumber;
            }
            CountryCode.setCountryCode(countryNumber);
            CountryCode.setCountryName(countryName);
//            Intent intent = new Intent();
////            intent.setClass(SelectCountryCodeActivity.this, MainActivity.class);
//            intent.putExtra("countryName", countryName);
//            intent.putExtra("countryNumber", countryNumber);
//            setResult(RESULT_OK, intent);
            finish();

        });
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.e("yangfan", "onScroll: " + firstVisibleItem);
                if (adapter.getCount()==0) {//只有一条结果的时候
                    ((TitledListView) view).updateTitle("#");
                }else if(adapter.getCount()==1){
                    ((TitledListView) view).updateTitle(adapter.getItem(0).sortLetters);
                }else{
                    // 第一项与第二项标题不同，说明标题需要移动
                    if (adapter.getPositionForSection(adapter.getSectionForPosition(firstVisibleItem))!=adapter.getPositionForSection(adapter.getSectionForPosition(firstVisibleItem+1))) {
                        ((TitledListView) view).moveTitle(adapter.getItem(firstVisibleItem).sortLetters);
                        //title.setText(mAllCountryList.get(firstVisibleItem).listTitle);
                    } else {
                        ((TitledListView) view).updateTitle(adapter.getItem(firstVisibleItem).sortLetters);
                    }
                }
            }

        });
    }

    /**
     * 获取国家列表
     */
    private void getCountryList() {
        String[] countryList = getResources().getStringArray(R.array.country_code_list);

        for (int i = 0, length = countryList.length; i < length; i++) {
            String[] country = countryList[i].split("\\*");

            String countryName = country[0];
            String countryNumber = country[1];
            String countrySortKey = characterParserUtil.getSelling(countryName);
            CountrySortModel countrySortModel = new CountrySortModel(countryName, countryNumber,
                    countrySortKey);
            String sortLetter = countryChangeUtil.getSortLetterBySortKey(countrySortKey);
            if (sortLetter == null) {
                sortLetter = countryChangeUtil.getSortLetterBySortKey(countryName);
            }

            countrySortModel.sortLetters = sortLetter;
            mAllCountryList.add(countrySortModel);
        }

        Collections.sort(mAllCountryList, pinyinComparator);
        adapter.updateListView(mAllCountryList);
    }

}
