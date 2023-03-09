package modelos;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

public class AlertProgress {

    private Context ctx;
    private String title;
    private String message;
    private ProgressDialog alertProgress;

    public AlertProgress(Context ctx, String title, String message){
        this.ctx = ctx;
        this.title = title;
        this.message = message;
        this.alertProgress = new ProgressDialog(ctx);
        this.alertProgress.setTitle(this.title);
        this.alertProgress.setMessage(this.message);
    }

    public AlertProgress(Context ctx, String title, String message, int theme){
        this.ctx = ctx;
        this.title = title;
        this.message = message;
        this.alertProgress = new ProgressDialog(ctx, theme);
        this.alertProgress.setTitle(this.title);
        this.alertProgress.setMessage(this.message);
    }

    public AlertProgress(Context ctx){
        this.ctx = ctx;
        this.alertProgress = new ProgressDialog(ctx);
    }

    public AlertProgress(Context ctx, int theme){
        this.ctx = ctx;
        this.alertProgress = new ProgressDialog(ctx,theme);
    }

    public AlertProgress content(String title, String message){
        this.title = title;
        this.message = message;
        this.alertProgress.setTitle(this.title);
        this.alertProgress.setMessage(this.message);
        return this;
    }

    public AlertProgress style(int theme){
        this.alertProgress.setProgressStyle(theme);
        return this;
    }

    public void show(){
        this.alertProgress.show();
    }

    public void show(boolean cancelable){
        this.alertProgress.setCanceledOnTouchOutside(cancelable);
        this.alertProgress.setCancelable(cancelable);
        this.alertProgress.show();

    }

    public void close(){
        this.alertProgress.dismiss();
    }

}
