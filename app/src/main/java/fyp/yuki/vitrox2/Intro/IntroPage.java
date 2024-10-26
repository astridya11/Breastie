package fyp.yuki.vitrox2.Intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import fyp.yuki.vitrox2.FoodScanningPage;
import fyp.yuki.vitrox2.MainActivity;
import fyp.yuki.vitrox2.R;
import fyp.yuki.vitrox2.bmi;

public class IntroPage extends AppCompatActivity {

    private ViewPager screen_Pager;
    IntroPageAdapter introAdapter;
    TabLayout tabIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_page);

        ImageView backBt = findViewById(R.id.introPageButton);
        backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroPage.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        screen_Pager = findViewById(R.id.screen_viewpager);
        tabIndicator = findViewById(R.id.tab_indicator);

        List<ScreenItem> mainItem = new ArrayList<>();
        mainItem.add(new ScreenItem("What is Breast Cancer?", "Breast cancer is a disease in which cells in the breast grow out of control. It can occur in either one or both the breasts in both female and male population.", R.drawable.introo1));
        mainItem.add(new ScreenItem("Symptoms", "Breast cancer symptoms include a painless lump in the breast, nipple discharge or bleeding, dimpled or puckered skin, persistent itch and rash around the nipple, a retracted nipple, and swollen, thickened skin over the breast.", R.drawable.introo2));
        mainItem.add(new ScreenItem("Risk factors", "Discover critical risk factors for breast cancer, including age, family history, obesity, reproductive factors, breast density, and specific breast conditions. Stay informed to empower your health.", R.drawable.introo3));

        introAdapter = new IntroPageAdapter(this, mainItem);
        screen_Pager.setAdapter(introAdapter);

        tabIndicator.setupWithViewPager(screen_Pager);
    }
}

