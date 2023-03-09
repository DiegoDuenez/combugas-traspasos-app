package modelos;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import com.example.app_combugas_nueva.MainActivity;
import com.example.app_combugas_nueva.NavigationActivity;
import com.example.app_combugas_nueva.R;

import java.util.concurrent.Callable;

public class Alert {

    private Context ctx;
    private String title;
    private String message;
    private AlertDialog.Builder alert;
    private AlertDialog dialog;
    public boolean isOpen = false;

    public Alert(Context ctx, String title, String message){
        this.ctx = ctx;
        this.title = title;
        this.message = message;
        this.alert = new AlertDialog.Builder(this.ctx);
        this.alert.setMessage(this.message).setTitle(this.title);

    }

    public Alert(Context ctx){
        this.ctx = ctx;
        this.alert = new AlertDialog.Builder(this.ctx);
    }


    public Alert content(String title, String message){
        this.title = title;
        this.message = message;
        this.alert.setMessage(this.message).setTitle(this.title);
        return this;
    }

    public Alert possitiveButton(String textButton,String func){
        alert.setPositiveButton(textButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    switch (func) {
                        case "close" : isOpen = false; dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        return this;
    }

    public Alert possitiveButton(String textButton, String func, Activity activity) {
        alert.setPositiveButton(textButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    switch (func) {
                        case "goto" : isOpen = false; dialog.dismiss();
                        case "kill" : isOpen = false; activity.finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return this;
    }

    public Alert possitiveButton(String textButton, String func, Bundle bundle, Activity activity, int goTo) {
        alert.setPositiveButton(textButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    switch (func) {
                        case "close" : isOpen = false; dialog.dismiss();
                    }
                    Navigation.findNavController(activity, R.id.nav_host_fragment)
                            .navigate(goTo, bundle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return this;
    }

    public Alert possitiveButton(String textButton, String func, Activity activity, int goTo) {
        alert.setPositiveButton(textButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    switch (func) {
                        case "close" : isOpen = false; dialog.dismiss();
                    }
                    Navigation.findNavController(activity, R.id.nav_host_fragment)
                            .navigate(goTo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return this;
    }

    public Alert negativeButton(String textButton,String func){
        alert.setNegativeButton(textButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    switch (func) {
                        case "close" : isOpen = false; dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        return this;
    }

    public Alert negativeButton(String textButton, String func, Bundle bundle, Activity activity, int goTo) {
        alert.setNegativeButton(textButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    switch (func) {
                        case "close" : isOpen = false; dialog.dismiss();
                    }
                    Navigation.findNavController(activity, R.id.nav_host_fragment)
                            .navigate(goTo, bundle);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        return this;
    }

    public Alert negativeButton(String textButton, String func, Activity activity, int goTo) {
        alert.setNegativeButton(textButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    switch (func) {
                        case "close" : isOpen = false; dialog.dismiss();
                    }
                    Navigation.findNavController(activity, R.id.nav_host_fragment)
                            .navigate(goTo);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        return this;
    }



    public void style(String typeButton, int color){
        if(typeButton.equals("Possitive") || typeButton.equals("P")){
            this.dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ctx, color));
        }
        else if(typeButton.equals("Negative") || typeButton.equals("N")){
            this.dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ctx, color));
        }

    }

    public Alert show(){
        this.dialog = this.alert.create();
        this.dialog.show();
        this.isOpen = true;
        return this;
    }

    public Alert show(boolean cancelable){
        this.dialog = this.alert.create();
        this.dialog.setCanceledOnTouchOutside(cancelable);
        this.dialog.setCancelable(cancelable);
        this.dialog.show();
        this.isOpen = true;
        return this;

    }



}

