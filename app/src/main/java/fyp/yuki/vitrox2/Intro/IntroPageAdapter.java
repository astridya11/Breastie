package fyp.yuki.vitrox2.Intro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

import fyp.yuki.vitrox2.R;


public class IntroPageAdapter extends PagerAdapter {


    Context context;
    List<ScreenItem> Screenlist;

    public IntroPageAdapter(Context context, List<ScreenItem> screenlist) {
        this.context = context;
        Screenlist = screenlist;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.layout_screen,null);

        ImageView imgSlide = layoutScreen.findViewById(R.id.main_image);
        TextView title = layoutScreen.findViewById(R.id.main_title);
        TextView description = layoutScreen.findViewById(R.id.main_description);

        title.setText(Screenlist.get(position).getTitle());
        description.setText(Screenlist.get(position).getDescription());
        imgSlide.setImageResource(Screenlist.get(position).getMainImg());

        container.addView(layoutScreen);

        return layoutScreen;
    }

    @Override
    public int getCount() {
        return Screenlist.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
