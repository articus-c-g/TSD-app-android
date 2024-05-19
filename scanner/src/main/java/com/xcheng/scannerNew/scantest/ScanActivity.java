package com.xcheng.scannerNew.scantest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.xcheng.scannerNew.R;
import com.xcheng.scannerNew.ScanTestService;
import com.xcheng.scannerNew.ScanUtil;

public class ScanActivity extends Activity implements ScanListAdapter.OnShowItemClickListener{

    private static final String TAG = "ScanActivity";

    private ListView listView;
    private TextView selectNumView;
    private TextView totalNumView;
    private List<ScanData> dataList;
    private List<ScanData> selectList;
    private ScanListAdapter adapter;
    private ScanDatabaseControler controler;
    private boolean mFristKeyDown = true;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action: " + action);
            int num = adapter.getCount();
            if (action.equals(ScanUtil.getScanActionName(context))) {
                String barcode = intent.getStringExtra(ScanUtil
                        .getScanBarcodeData(context));
                String symbology = intent.getStringExtra(ScanUtil
                        .getScanSymbologyType(context));
                Log.d(TAG, "barcode: " + barcode);
                Log.d(TAG, "symbology: " + barcode);
                ScanData scanData = new ScanData(symbology, barcode);
                addData(num, scanData);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan);
        initView();
    }

    private void initView() {
        controler = new ScanDatabaseControler(this);
        listView = (ListView) findViewById(R.id.scan_listview);
        selectNumView = (TextView) findViewById(R.id.list_selected_num);
        totalNumView = (TextView) findViewById(R.id.list_total_num);
        dataList = new ArrayList<ScanData>();
        selectList = new ArrayList<ScanData>();
        dataList = controler.queryAll();
        adapter = new ScanListAdapter(this);
        adapter.setData(dataList);
        listView.setAdapter(adapter);
        int num = adapter.getCount();
        totalNumView.setText(String.valueOf(num));
        selectNumView.setText("0");
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, new IntentFilter(ScanUtil.SCAN_DECODING_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scan_test_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_select_all) {
            markAll(true);
            return true;
        } else if (id == R.id.menu_select_erase) {
            deleteData();
            return true;
        } else if (id == R.id.menu_select_release) {
            markAll(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown:keyCode:" + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_F3:
            case KeyEvent.KEYCODE_CAMERA:
            case KeyEvent.KEYCODE_FOCUS:
                if (mFristKeyDown) {
                    Intent serviceIntent = new Intent(this, ScanTestService.class);
                    serviceIntent.putExtra("isScanTest", true);
                    startService(serviceIntent);
                    mFristKeyDown = false;
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp.keyCode:" + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_F3:
            case KeyEvent.KEYCODE_CAMERA:
            case KeyEvent.KEYCODE_FOCUS:
                Intent serviceIntent = new Intent(this, ScanTestService.class);
                stopService(serviceIntent);
                mFristKeyDown = true;
                break;
            default:
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    public void addData(int position, ScanData scanData) {
        adapter.addData(position, scanData);
        adapter.notifyDataSetChanged();
        listView.setSelection(position);
        controler.insert(scanData);
        totalNumView.setText(String.valueOf(++position));
    }

    public void deleteData() {
        Map<Integer, Boolean> isCheck_delete = adapter.getMap();
        int count = adapter.getCount();
        int num = count;
        if (adapter.isSelectAll) {
            adapter.removeAllData();
            controler.deleteAll();
            num = 0;
        } else {
            for (int i = 0; i < count; i++) {
                int position = i - (count - adapter.getCount());
                num = position;
                if (isCheck_delete.get(i) != null && isCheck_delete.get(i)) {
                    isCheck_delete.remove(i);
                    controler.delete(adapter.getId(position));
                    adapter.removeData(position);
                }
            }
        }
        totalNumView.setText(String.valueOf(num));
        selectNumView.setText("0");
        adapter.initCheck(false);
        adapter.notifyDataSetChanged();
        refresh();
    }

    public void markAll(boolean select) {
        Map<Integer, Boolean> isCheck = adapter.getMap();
        adapter.initCheck(select);
        adapter.notifyDataSetChanged();
        selectNumView.setText(String.valueOf(adapter.selectNum));
    }

    public void refresh() {
        onCreate(null);
    }
}
