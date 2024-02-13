package ba.sum.fpmoz.exploreacityapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import ba.sum.fpmoz.exploreacityapp.fragments.AddCityFragment;
import ba.sum.fpmoz.exploreacityapp.fragments.CityFragment;

public class CityViewPagerAdapter extends FragmentStateAdapter {

    public CityViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        switch (position) {
            case 0:
                return new CityFragment();
            case 1:
                return new AddCityFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Number of tabs
    }
}