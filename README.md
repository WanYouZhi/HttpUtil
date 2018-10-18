在android中的网络请求如果用Handler来传，看代码跳来跳去太累。首先呢，这个是我实际项目中的封装，各位不用担心能不能用的问题。

支持的功能：
1.无缝在主线程调用回到主线程 
2.自动Gson泛型机械，并支持到二级泛型 
3.支持Activity结束时，取消所有创建的网络操作 
4.支持单个网络操作取消 
5.使用链式调用，使用方便

Get请求
HttpUtil.newBuilder(this)//每个请求都绑定一个activity
        .url("https://www.baidu.com/s")
        .addParam("wd","哈哈")
        .get(new HttpUtil.HttpResult<String>() {
              @Override
              public void onSuccess(String obj) {//可直接处理成功情况，不用onFailed
                  TextView textView = (TextView) findViewById(R.id.text);
                  textView.setText(obj);
              }
        });

Post请求
HttpUtil.newBuilder(this)//只是展现用法，不能获取数据
        .url("https://www.baidu.com/")
        .addParam(new HashMap<String, String>())//也可以直接添加Map参数
        .post(new HttpUtil.HttpResult<ArrayList<DriverPropertyInfo>>() {//支持两级泛型
             @Override
             public void onSuccess(ArrayList<DriverPropertyInfo> infos) {
             //很多时候我们需要list<>形式，
             }

             @Override
             public void onFailed(String msg) {
                   super.onFailed(msg);
             }
        });

取消请求
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get请求
        HttpUtil.HttpBuilder builder = HttpUtil.newBuilder(this)//每个请求都绑定一个activity
                .url("https://www.baidu.com/s")
                .addParam("wd","哈哈")
                .get(new HttpUtil.HttpResult<String>() {
                    @Override
                    public void onSuccess(String obj) {//可只处理成功情况
                        TextView textView = (TextView) findViewById(R.id.text);
                        textView.setText(obj);
                    }
                });

       HttpUtil.remove(builder);//取消单个请求
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpUtil.remove(this);//在结束取消activity所有网络请求，建议在baseActivity里
    }
}
