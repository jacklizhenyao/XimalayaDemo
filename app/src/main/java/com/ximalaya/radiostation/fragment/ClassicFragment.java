package com.lanou.radiostation.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lanou.radiostation.R;
import com.lanou.radiostation.activity.AlbumActivity;
import com.lanou.radiostation.adapter.MyAlbumAdapter;
import com.lanou.radiostation.bean.Album;
import com.lanou.radiostation.view.PullToRefreshView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/7/23.
 */
public class ClassicFragment extends BaseFragment {
    // 直接请求常量
    public static final int REQUEST = 0;
    // 下拉刷新请求常量
    public static final int REFRESH = 1;
    // 上拉加载请求常量
    public static final int LOAD = 2;
    private ListView fragment_fire_lv;
    private List<Album.Data> list;
    private MyAlbumAdapter classicAdapter;
    private PullToRefreshView pull;
    private  int pageSize;
    private String result;

    private  String url ="http://mobile.ximalaya.com/mobile/discovery/v1/category/album?calcDimension=classic&categoryId=0" +
            "&device=android&pageId=1&pageSize=" +
            pageSize +
            "&status=0&tagName=";

    @Override
    public View initView() {
        View view =View.inflate(getActivity(), R.layout.sound_new_fire_fragment,null);
        fragment_fire_lv = (ListView) view.findViewById(R.id.fragment_fire_lv);
        list = new ArrayList<>();
        pull = (PullToRefreshView) view.findViewById(R.id.fire_frag_pull);
        classicAdapter = new MyAlbumAdapter(list, getContext());
        fragment_fire_lv.setAdapter(classicAdapter);

        setPullListener();
        getNetData(0);
        clickListView();
        return view;


    }


    private void setPullListener() {
        pull.setOnFooterLoadListener(new PullToRefreshView.OnFooterLoadListener() {
            @Override
            public void onFooterLoad(PullToRefreshView view) {
                getNetData(LOAD);
            }
        });

        pull.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                getNetData(REFRESH);
            }
        });
    }

    @Override
    public void setData() {

        setPullListener();
        fragment_fire_lv.setAdapter(classicAdapter);
    }

    private void getNetData(final int type) {

        //判断当前的请求方式是什么
        switch (type) {
            case REQUEST:
                pageSize = 20;
                break;
            case REFRESH:
                pageSize = 20;
                break;
            case LOAD:
                pageSize+=20;
                break;
            default:
                break;
        }

        HttpUtils utils =new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;

                Gson gson =new Gson();
                Album album =  gson.fromJson(result,Album.class);
                for (int i = 0; i <album.list.size();  i++) {
                    list.add(album.list.get(i));
                }
                if(type == LOAD){
                    list.addAll(album.list);
                    //加载方式请求结束后，关闭加载视图
                    pull.onFooterLoadFinish();
                }else{
                    //把album对象中的d   ata数据集合赋值给adater使用的list集合
                    list = album.list;
                    //刷新方式请求结束后，关闭刷新视图
                    pull.onHeaderRefreshFinish();
                }
                classicAdapter.setList(list);
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });

    }


    private void clickListView() {
        fragment_fire_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(list!=null&&list.size()>0){
                    Intent intent =new Intent(getActivity(), AlbumActivity.class);
                    intent.putExtra("albumId",list.get(i).albumId);
                    intent.putExtra("title",list.get(i).title);
                    intent.putExtra("position",0);
                    intent.putExtra("nickname",list.get(i).nickname);
                    intent.putExtra("playsCounts",list.get(i).playsCounts);
                    intent.putExtra("picUrl",list.get(i).albumCoverUrl290);
                    startActivity(intent);
                }
            }
        });

    }


}
