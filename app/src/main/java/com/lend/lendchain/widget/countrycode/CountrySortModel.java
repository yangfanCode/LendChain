package com.lend.lendchain.widget.countrycode;

import com.lend.lendchain.widget.countrycode.tools.CountryModel;
/**
 * Created by admin on 2016/7/22.
 */

/**
 * 类简要描述
 * <p/>
 * <p>
 * 类详细描述
 * </p>
 *
 * @author duanbokan
 */

public class CountrySortModel extends CountryModel {
    // 显示数据拼音的首字母
    public String sortLetters;

    public CountrySortToken sortToken = new CountrySortToken();

    public CountrySortModel(String name, String number, String countrySortKey) {
        super(name, number, countrySortKey);
    }

}
