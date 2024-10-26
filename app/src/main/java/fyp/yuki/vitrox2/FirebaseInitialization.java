package fyp.yuki.vitrox2;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class FirebaseInitialization extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
    }
}
