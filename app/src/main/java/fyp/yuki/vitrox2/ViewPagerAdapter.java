package fyp.yuki.vitrox2;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 0: return new MalaysiaTab();
            case 1: return new UnitedStatesTab();
            case 2: return new JapanTab();
            case 3: return new ChinaTab();
            case 4: return new SingaporeTab();
            default: return new MalaysiaTab();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
