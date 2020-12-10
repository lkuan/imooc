package com.amos.weather.data;

import com.amos.weather.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.*;

import java.util.ArrayList;

public class ListItemProvider extends RecycleItemProvider {

    private AbilitySlice mSlice;
    private ArrayList<DataMo> mDataMos = new ArrayList<>();
    private OnItemClickListener mListener;

    public ListItemProvider(AbilitySlice mSlice, OnItemClickListener mListener) {
        this.mSlice = mSlice;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return mDataMos.size();
    }

    @Override
    public DataMo getItem(int i) {
        return mDataMos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public Component getComponent(int i, Component component, ComponentContainer componentContainer) {
        Component component1 = LayoutScatter.getInstance(mSlice).parse(ResourceTable.Layout_list_item, null, false);
        if (!(component1 instanceof ComponentContainer)) {
            return null;
        }
        ComponentContainer rootLayout = (ComponentContainer) component1;
        DataMo dataMo = mDataMos.get(i);
        for (CityMo mo : dataMo.cityMos) {
            Text titleItem = (Text) LayoutScatter.getInstance(mSlice).parse(ResourceTable.Layout_item_title, null, false);
            titleItem.setText(mo.cityName);
            rootLayout.addComponent(titleItem);
            titleItem.setClickedListener(new Component.ClickedListener() {
                @Override
                public void onClick(Component component) {
                    mListener.onItemClick(mo, i);
                }
            });
        }
        return component1;
    }

    public void setData(ArrayList<CityMo> cityMos) {
        mDataMos.clear();
        int i = 0;
        ArrayList<CityMo> tempMos = new ArrayList<>();
        for (CityMo mo : cityMos) {
            if (i == 3) {
                i = 0;
                mDataMos.add(new DataMo(tempMos));
                tempMos = new ArrayList<>();
            }
            tempMos.add(mo);
            i++;
        }
        mDataMos.add(new DataMo(tempMos));
        notifyDataChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(CityMo mo, int position);
    }
}
