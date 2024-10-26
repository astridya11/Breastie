package fyp.yuki.vitrox2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import fyp.yuki.vitrox2.Intro.IntroPage;

public class SplashScreen extends AppCompatActivity {

    private static int Splash_screen = 2800;

    Animation upanimation, downanimation;
    TextView title, subtitle;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Find ID
        title = findViewById(R.id.splashScreen_title);
        subtitle = findViewById(R.id.splashScreen_subtitle);
        image = findViewById(R.id.splashScreen_image);

        //Animations
        upanimation = AnimationUtils.loadAnimation(this, R.anim.up_animation);
        downanimation = AnimationUtils.loadAnimation(this, R.anim.down_animation);

        //Set Animation
        title.setAnimation(downanimation);
        subtitle.setAnimation(downanimation);
        image.setAnimation(downanimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, LogOrSignActivity.class);
                startActivity(intent);
                finish();
            }
        },Splash_screen);
    }
}