package meme.app.dmcx.chatapp.Fragment.FragmentsContents.Home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import meme.app.dmcx.chatapp.Fragment.FragmentsContents.Home.Tabs.TabChats;
import meme.app.dmcx.chatapp.Fragment.FragmentsContents.Home.Tabs.TabFriends;
import meme.app.dmcx.chatapp.Fragment.FragmentsContents.Home.Tabs.TabRequests;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private List<String> fragmentNames;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);

        // Initialize
        fragments = new ArrayList<>();
        fragmentNames = new ArrayList<>();

        // Contents
        fragmentNames.add("REQUESTS");
        fragments.add(new TabRequests());

        fragmentNames.add("CHATS");
        fragments.add(new TabChats());

        fragmentNames.add("FRIENDS");
        fragments.add(new TabFriends());

    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public CharSequence getPageTitle(int position) {
        return fragmentNames.get(position);
    }

}
