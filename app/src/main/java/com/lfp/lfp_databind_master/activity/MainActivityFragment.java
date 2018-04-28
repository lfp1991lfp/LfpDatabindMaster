package com.lfp.lfp_databind_master.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lfp.lfp_databind_master.R;
import com.lfp.lfp_databind_master.adapter.HomeAdapter;
import com.lfp.lfp_databind_master.databinding.FragmentMainBinding;
import com.lfp.lfp_databind_master.xml.WeakHandler;
import com.lfp.lfp_databind_master.xml.XmlParseTools;
import com.lfp.lfp_databind_master.xml.bean.ContentBean;
import com.lfp.lfp_databind_master.xml.file.FileTools;
import com.lfp.lfp_databind_recycleview_library.MultiItemTypeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements Handler.Callback {

    public static final String TAG = MainActivityFragment.class.getSimpleName();
    FragmentMainBinding binding;
    private HomeAdapter adapter;
    private WeakHandler handler;

    public MainActivityFragment() {
    }

    public static MainActivityFragment newInstance() {

        Bundle args = new Bundle();

        MainActivityFragment fragment = new MainActivityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     *
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
            return false;
        }
        return true;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        adapter = new HomeAdapter(getContext());
        binding.idRecyclerview.setAdapter(adapter);

        adapter.setOnItemClickListener(new MultiItemTypeAdapter.SimpleOnItemClickListener() {
            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                ContentBean contentBean = adapter.getDataList().get(position);

                return true;
            }
        });
        handler = new WeakHandler(this);

        initData();
        return binding.getRoot();
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case 0:
                int position = message.arg1;
                binding.progressBar.setVisibility(View.GONE);
                binding.idRecyclerview.setVisibility(View.VISIBLE);
//                DiffUtil.DiffResult result = (DiffUtil.DiffResult) message.obj;
                ArrayList<ContentBean> beans = message.getData().getParcelableArrayList("list");
//                mAdapter.refresh(result, beans);
                List<ContentBean> newData = new ArrayList<>(beans);

                if (adapter.getDataList().size() > 0) {
                    newData.addAll(adapter.getDataList());
                }

                adapter.setDateResult(newData);
                binding.idRecyclerview.smoothScrollToPosition(position);
                break;
            case 1:
                if (adapter.getDataList().size() == 0) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                }
                binding.idRecyclerview.setVisibility(View.GONE);
                break;
            case -1:
                Toast.makeText(getContext(), "文件解析错误，请重新选择文件", Toast.LENGTH_LONG).show();
                break;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    Uri uri = data.getData();
                    if (uri != null) {
                        if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                            read(uri.getPath(), 0);
                        }
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                            String path = FileTools.getPath(getContext(), uri);
                            read(path, 0);
                        }

                    }
                    break;
            }
        }
    }

    //lfp end
    protected void initData() {
        binding.idRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.idRecyclerview.setAdapter(adapter = new HomeAdapter(getContext()));

        adapter.setOnItemClickListener(new MultiItemTypeAdapter.SimpleOnItemClickListener() {
            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                ContentBean contentBean = adapter.getDataList().get(position);

                return true;
            }
        });
        read("enable.txt", 1);
    }

    /**
     * 可以从assets中读取，也可以从file中读取
     *
     * @param name 文件名或者路径
     * @param type 类型
     */
    private void read(final String name, final int type) {
        //开启线程读取数据，防止主线程解析卡死
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
                ArrayList<ContentBean> beans = (ArrayList<ContentBean>) XmlParseTools.xmlParse(name, type);
                Message message = Message.obtain();
                if (beans != null) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("list", beans);
                    message.what = 0;
                    message.arg1 = 0;

                    message.setData(bundle);
                } else {
                    message.what = -1;
                }
                handler.sendMessage(message);
            }
        }).start();
    }

    public void importData() {
        if (isGrantExternalRW(getActivity())) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/plain");//只选择xml文件。
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 1);
        } else {
            Toast.makeText(getActivity(), "请检查是否开启读写权限", Toast.LENGTH_LONG).show();
        }
    }
}
