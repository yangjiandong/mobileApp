package com.ek.mobileapp.utils.download;

//String url = "http://zhangmenshiting5.baidu.com/data2/music/1837238/2460371341806461.mp3?xcode=74d240eb72f4e01b578f247b5baed415&lid=0";
//DownLoad dl = new DownLoad(url,"xyzth.mp3", new CallBack());
//dl.DownLoadFile();
final class CallBack implements DownLoadCallBack
{
    @Override
    public void UpdateUIProgress(int cur)
    {
        //setTitle("线程执行了");
        //sb.setProgress(cur);
    }

    @Override
    public void SetMaxProgress(int val)
    {
        //sb.setMax(val);
    }

    @Override
    public void Success() {
        //setTitle("下载完成！");
    }

    @Override
    public void Error() {
        //setTitle("下载出错！");
    }

    @Override
    public void ReturnPercent(String percent) {
        //tv_kb_state.setText(percent);
    }
}