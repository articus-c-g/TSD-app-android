package com.xcheng.scannerNew.scantest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.xcheng.scannerNew.R;


public class ScanListAdapter extends BaseAdapter {

    public interface OnShowItemClickListener {

    }

    private List<ScanData> scanList = new ArrayList<ScanData>();
    private Context mContext;
    private Map<Integer, Boolean> isCheck = new HashMap<Integer, Boolean>();

    public boolean isSelectAll = false;
    public int selectNum = 0;

    public ScanListAdapter(Context mContext) {
        super();
        this.mContext = mContext;
        initCheck(false);
    }

    public void initCheck(boolean flag) {
        for (int i = 0; i < scanList.size(); i++) {
            isCheck.put(i, flag);
        }
        if (flag) {
            selectNum = getCount();
        } else {
            selectNum = 0;
        }
        isSelectAll = flag;
    }

    public void setData(List<ScanData> data) {
        this.scanList = data;
    }

    public int getId(int position) {
        ScanData data = scanList.get(position);
        return data.getId();
    }

    public void addData(int position, ScanData data) {
        scanList.add(position, data);
    }

    public void removeData(int position) {
        scanList.remove(position);
    }

    public void removeAllData() {
        scanList.clear();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return scanList != null ? scanList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return scanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        View view = null;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.scan_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.cbCheckBox = (CheckBox) view.findViewById(R.id.checkbox);
            viewHolder.numView = (TextView) view.findViewById(R.id.list_num);
            viewHolder.symbologyView = (TextView) view.findViewById(R.id.list_symbology);
            viewHolder.barcodeView = (TextView) view.findViewById(R.id.list_barcode);

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        ScanData data = scanList.get(position);
        viewHolder.numView.setText(String.valueOf(position + 1));
        viewHolder.symbologyView.setText(data.getSymbology());
        viewHolder.barcodeView.setText(data.getBarcode());
        viewHolder.cbCheckBox
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        isCheck.put(position, isChecked);
                        selectNum = isChecked ? selectNum++ : selectNum--;
                    }
                });
        if (isCheck.get(position) == null) {
            isCheck.put(position, false);
        }
        viewHolder.cbCheckBox.setChecked(isCheck.get(position));
        return view;
    }

    public static class ViewHolder {
        public CheckBox cbCheckBox;
        public TextView numView;
        public TextView symbologyView;
        public TextView barcodeView;

    }

    public Map<Integer, Boolean> getMap() {
        return isCheck;
    }

}
