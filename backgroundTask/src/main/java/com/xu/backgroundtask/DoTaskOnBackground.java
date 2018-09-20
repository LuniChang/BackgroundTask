package com.xu.backgroundtask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.xu.Log.AppLog;
import com.xu.Log.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xq on 2015/1/5.
 */
@SuppressLint("HandlerLeak")
public abstract class DoTaskOnBackground {

    public interface OnTaskErrorListener {
        public void onError(DoTaskOnBackground background, Exception e);
    }

    public interface OnTaskFinishListener {
        public void onFinish(DoTaskOnBackground background);
    }

    class ProcessHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {

                if (progressDialog != null)
                    progressDialog.dismiss();

                if (runException != null) {

                    taskError();
                }

                if (runException == null) {// 正常

                    taskFinish();
                }

            } catch (Exception maine) {
                appLog.e("DoTaskOnBackground " + "内部错误 " + getExceptionStackInfo(maine));
            }
        }

    }

    protected class UpdateProcessHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String msgStr = msg.getData().getString("msg");
            if (!isShowByActivty()) {
                if (DoTaskOnBackground.this.progressDialog != null) {

                    if (msgStr != null)
                        DoTaskOnBackground.this.progressDialog.setTitle(msgStr);
                }
            } else {
                try {
                    if (getBindActvity() != null) {
                        getBindActvity().updateTitle(msgStr);

                    }
                } catch (Exception e) {
                    appLog.e(e.toString());
                }
            }
        }

    }

    private static Log<String> appLog = new AppLog(3, DoTaskOnBackground.class.getName());



    private static List<DoTaskOnBackground> tasks = new ArrayList<DoTaskOnBackground>();

    protected boolean showByActivty = true;


    public boolean isShowByActivty() {
        return showByActivty;
    }

    public void setShowByActivty(boolean showByActivty) {
        this.showByActivty = showByActivty;
    }

    public static String showActivtyName = "com.xu.backgroundtask.ProcessActvity";

    private String myShowActivtyName = null;


    public static DoTaskOnBackground getTaskById(String taskId) {
        for (int i = tasks.size() - 1; i > -1; --i) {
            String id = tasks.get(i).getTaskId();
            if (taskId.equals(id)) {
                return tasks.get(i);
            }
        }
        return null;
    }

    public synchronized static DoTaskOnBackground regiserActivity(BackgroundTaskViewListener activity) {
        if (activity == null)
            return null;

        DoTaskOnBackground target = getTaskById(activity.getBackgroundTaskId());
        target.setBindActivity(activity);
        target.run();
        tasks.remove(target);
        return target;
    }



    protected Bundle bundle = new Bundle();
    protected Context context;
    protected Boolean isRun = false;

    protected Boolean justRunOnce = false;


    protected long lastTime = 0;



    Runnable myrun = new Runnable() {
        @Override
        public void run() {

            isRun = true;
            try {
                runException = null;
                doTask();

                Message msg = new Message();
                msg.setData(bundle);
                processHandler.sendMessage(msg);
            } catch (Exception e) {

                runException = e;
                appLog.e("DoTaskOnBackground" + e.toString());
                Message msg = new Message();
                msg.setData(bundle);
                processHandler.sendMessage(msg);

            }
            isRun = false;


        }
    };


    private static ArrayList<DoTaskOnBackground> taskQueue = new ArrayList<DoTaskOnBackground>();

    private static QueThread runQueThread;

    class QueThread extends Thread {

        @Override
        public void run() {

            while (taskQueue.size() > 0) {
                DoTaskOnBackground headItem = taskQueue.get(0);


                try {
                    runException = null;
                    headItem.doTask();

                    Message msg = new Message();
                    msg.setData(bundle);
                    processHandler.sendMessage(msg);
                } catch (Exception e) {

                    runException = e;
                    appLog.e("DoTaskOnBackground" + e.toString());
                    Message msg = new Message();
                    msg.setData(bundle);
                    processHandler.sendMessage(msg);

                }
                taskQueue.remove(0);
                if(taskQueue.size()==0){
                    try{
                        sleep(500);
                    }catch (Exception e){
                        appLog.e("DoTaskOnBackground" + e.toString());
                    }

                }

            }

        }
    }

    ;


    /**
     * 执行完毕后占用UI线程
     */
    protected OnTaskErrorListener onTaskErrorListener = new OnTaskErrorListener() {
        @Override
        public void onError(DoTaskOnBackground background, Exception e) {
            showRunException(background.context, e.getMessage());
        }
    };

    /**
     * 执行完毕后占用UI线程
     */
    protected OnTaskFinishListener onTaskFinishListener = new OnTaskFinishListener() {
        @Override
        public void onFinish(DoTaskOnBackground background) {

        }
    };

    protected ProcessHandler processHandler = new ProcessHandler();

    protected ProgressDialog progressDialog;

    protected Exception runException;

    private Thread runThread = null;

    private String taskId = "";

    protected UpdateProcessHandler updateProcessHandler = new UpdateProcessHandler();


    private boolean isInputQueue = true;

    private Boolean canCancel = true;

    public DoTaskOnBackground() {
        taskId = System.currentTimeMillis() + "" + Math.random() * 100;
    }

    public DoTaskOnBackground(Context context) {
        this.context = context;
    }

    protected DoTaskOnBackground(Context context, Boolean justRunOnce) {
        this.context = context;
        this.justRunOnce = justRunOnce;
    }

    public DoTaskOnBackground(Context context, OnTaskFinishListener onTaskFinishListener) {
        this.context = context;
        this.onTaskFinishListener = onTaskFinishListener;
    }

    protected DoTaskOnBackground(Context context, OnTaskFinishListener onTaskFinishListener, Boolean justRunOnce) {
        this.context = context;
        this.onTaskFinishListener = onTaskFinishListener;
        this.justRunOnce = justRunOnce;
    }

    public void cancel() {
        if (runThread != null) {
            try {
                runThread.interrupt();
            } catch (Exception e) {
                appLog.e(DoTaskOnBackground.class.getName() + e.toString());
            }

        }


        try {
            taskQueue.remove(this);
        } catch (Exception e) {
            appLog.e(DoTaskOnBackground.class.getName() + e.toString());
        }


    }

    /**
     * 检查全局对话权限
     *
     * @param context
     * @return
     */
    private boolean checkPermissionGranted(Context context) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        // 检查是否有全局权限ee
        if (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.SYSTEM_ALERT_WINDOW",
                context.getApplicationContext().getPackageName()))
            return true;
        return false;

    }

    private void doRun() {
        if (justRunOnce) {

            if (isRun) {
                return;
            }
            runThread = new Thread(myrun);
            runThread.start();

            return;
        }

        if (isInputQueue) {
            if(runQueThread==null||!runQueThread.isAlive()){
                runQueThread=new QueThread();
                runQueThread.run();

            }
            if(taskQueue.indexOf(this)<0){
                taskQueue.add(this);
            }


        }


    }

    abstract public void doTask() throws Exception;

    @Override
    protected void finalize() throws Throwable {
        if (this.runThread != null) {
            this.runThread.interrupt();
        }
        super.finalize();
    }

    //当activtity没有启动的时候不起作用
    protected synchronized void finishActivty() {

//		for (int i = 0; i < showActivitys.size(); ++i) {
//			BackgroundTaskViewListener act =  showActivitys.get(i);
//
//			String id =  act.getBackgroundTaskId();
//			if (taskId.equals(id)) {
//				showActivitys.remove(i);
//				tasks.remove(this);
//				act.dismiss();
//				return;
//			}
//		}

        if (this.bindActivity != null) {
            this.bindActivity.dismiss();
        }

    }

    public Bundle getBundle() {
        return bundle;
    }

    public String getExceptionStackInfo(Exception e) {
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        return out.toString();
    }

    public Boolean getJustRunOnce() {
        return justRunOnce;
    }

    public OnTaskErrorListener getOnTaskErrorListener() {
        return onTaskErrorListener;
    }

    public OnTaskFinishListener getOnTaskFinishListener() {
        return onTaskFinishListener;
    }

    public String getTaskId() {
        return taskId;
    }


    public void run() {
        doRun();
    }

    public void run(String msg) {
        if (context != null && msg != null) {
            if (showByActivty) {
                showActivty(msg);
                return;
            } else {
                showDlg(msg);
            }

        }
        doRun();

    }

    public void sendDataToHandler(Bundle mybundle) {
        Message msg = new Message();
        msg.setData(mybundle);
        processHandler.sendMessage(msg);
    }

    public void setJustRunOnce(Boolean justRunOnce) {
        this.justRunOnce = justRunOnce;
    }

    public void setOnTaskErrorListener(OnTaskErrorListener onTaskErrorListener) {
        this.onTaskErrorListener = onTaskErrorListener;
    }

    public void setOnTaskFinishListener(OnTaskFinishListener onTaskFinishListener) {
        this.onTaskFinishListener = onTaskFinishListener;
    }


    protected void showActivty(String msg) {

        Intent intent = new Intent();

        if (myShowActivtyName == null) {
            myShowActivtyName = showActivtyName;
        }

        ComponentName component = new ComponentName(context, myShowActivtyName);
        intent.setComponent(component);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("taskId", taskId);
        intent.putExtra("msg", msg);
        intent.putExtra("canCancel", canCancel);
        context.startActivity(intent);
        tasks.add(this);
    }

    protected void showDlg(String msg) {
        // 检查是否有全局权限
        if (checkPermissionGranted(context)) {
            this.progressDialog = new ProgressDialog(context);
            this.progressDialog.setMessage(msg);
            this.progressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            this.progressDialog.show();
        } else {
            this.progressDialog = ProgressDialog.show(context, null, msg);
        }

        this.progressDialog.setCancelable(canCancel);
        lastTime = System.currentTimeMillis();
        this.progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (System.currentTimeMillis() - lastTime > 5000) {
                    progressDialog.setCancelable(true);
                }
                return false;
            }
        });
        this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancel();

            }

        });
    }

    public void showErrorDialog(Context context, String msg, OnClickListener ok, OnClickListener cancel) {
        AlertDialog.Builder normalDia = new AlertDialog.Builder(context);

        normalDia.setIcon(R.drawable.ic_launcher);
        normalDia.setTitle("提示");
        normalDia.setMessage(msg);

        if (ok != null) {
            normalDia.setPositiveButton("确定", ok);

        }
        if (cancel != null) {
            normalDia.setNegativeButton("取消", cancel);
        }
        AlertDialog alertDialog = normalDia.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }

    private BackgroundTaskViewListener bindActivity;

    public BackgroundTaskViewListener getBindActvity() {
//		if(bindActivity==null){
//			bindActivity=getActivityByTaskId(taskId);
//		}
        return bindActivity;
    }


    public void setBindActivity(BackgroundTaskViewListener bindActivity) {
        this.bindActivity = bindActivity;
    }

    public void showErrorView(Context context, String msg) {

        if (showByActivty) {
            try {
                BackgroundTaskViewListener actvity = (BackgroundTaskViewListener) getBindActvity();
                actvity.whenCacthError(msg);
            } catch (Exception e) {
                appLog.e(e.toString());
            }

        } else if (checkPermissionGranted(context)) {
            showErrorDialog(context, "出现异常：" + msg + "，是否重做？", new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    run("重新运行...");
                }
            }, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        } else {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }


    }

    public void showRunException(Context context, String msg) {
        showErrorView(context, msg);
    }

    public void upDateProcessDlgText(String msgStr) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("msg", msgStr);
        msg.setData(bundle);
        msg.setTarget(updateProcessHandler);
        msg.sendToTarget();

    }

    private void taskFinish() {
        try {
            doOnTaskFinish();
            if (showByActivty) {
                finishActivty();
            }
        } catch (Exception e) {
            appLog.e("DoTaskOnBackground: " + getExceptionStackInfo(e));
            if (onTaskErrorListener != null) {
                onTaskErrorListener.onError(DoTaskOnBackground.this, runException);
            }
        }
    }

    protected void doOnTaskFinish() {
        onTaskFinishListener.onFinish(DoTaskOnBackground.this);
    }

    private void taskError() {
        appLog.e("DoTaskOnBackground: " + getExceptionStackInfo(runException));
        doOnTaskError();
    }

    protected void doOnTaskError() {
        if (onTaskErrorListener != null) {
            try {
                onTaskErrorListener.onError(DoTaskOnBackground.this, runException);
            } catch (Exception e) {

                appLog.e("DoTaskOnBackground:" + getExceptionStackInfo(e));
            }
        } else if (context != null) {

            showRunException(context, runException.getMessage());
        }
    }

    public String getMyShowActivtyName() {
        return myShowActivtyName;
    }

    public void setMyShowActivtyName(String myShowActivtyName) {
        this.myShowActivtyName = myShowActivtyName;
    }


}
