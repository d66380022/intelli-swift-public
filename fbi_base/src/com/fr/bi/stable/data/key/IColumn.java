package com.fr.bi.stable.data.key;

import com.fr.json.JSONTransform;

/**
 * Created by GUY on 2015/3/10.
 */
public interface IColumn extends JSONTransform {
    int getType();

    String getFieldName();

    boolean isUsable();

}