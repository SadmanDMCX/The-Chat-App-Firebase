package meme.app.dmcx.chatapp.Fragment.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;
import meme.app.dmcx.chatapp.Firebase.AppFirebaseVariables;
import meme.app.dmcx.chatapp.Fragment.FragmentsContents.Home.SectionsPagerAdapter;
import meme.app.dmcx.chatapp.LocalDatabase.AppLocalDatabaseVariables;
import meme.app.dmcx.chatapp.R;

public class Home extends Fragment {

    // Variables
    private TabLayout homeTabLayout;
    private ViewPager tabViewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;

    // Program
    private void Program(View view) {
        // Initialization
        tabViewPager = view.findViewById(R.id.tabViewPager);
        homeTabLayout = view.findViewById(R.id.homeTabLayout);

        sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        // Program
        tabViewPager.setAdapter(sectionsPagerAdapter);
        int pos = SuperVariables._AppLocalDatabase.retrive().containsKey(AppLocalDatabaseVariables.TAB_POSITION) == true ?
                Integer.valueOf(SuperVariables._AppLocalDatabase.retrive().get(AppLocalDatabaseVariables.TAB_POSITION).toString()) : 1;

        tabViewPager.setCurrentItem(pos);
        homeTabLayout.setupWithViewPager(tabViewPager);

        // Event
        tabViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                SuperVariables._AppLocalDatabase.store(AppLocalDatabaseVariables.TAB_POSITION, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Program(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        SuperVariables._AppFirebase.getUsersDatabase().child(SuperVariables._AppFirebase.getCurrenctUserId()).child(AppFirebaseVariables.uonline).setValue(true);
    }
}
