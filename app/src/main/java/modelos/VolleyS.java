package modelos;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
public class VolleyS {

    private static VolleyS mVolleyS = null;

    private RequestQueue mRequestQueue;

    private VolleyS(Context ctx){
        mRequestQueue = Volley.newRequestQueue(ctx);

    }

    public static VolleyS getInstance(Context ctx){
        if(mVolleyS == null){
            mVolleyS = new VolleyS(ctx);
        }
        return  mVolleyS;
    }

    public RequestQueue getRequestQueue(){
        return mRequestQueue;
    }

}
