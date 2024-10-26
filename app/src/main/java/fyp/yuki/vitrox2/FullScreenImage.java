package fyp.yuki.vitrox2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FullScreenImage extends AppCompatActivity {
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        image = findViewById(R.id.zoomImage);

        String imageURL = this.getIntent().getStringExtra("imageURL");
        Picasso.get().load(imageURL).into(image);
    }
}