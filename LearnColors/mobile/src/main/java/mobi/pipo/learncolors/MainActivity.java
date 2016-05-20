package mobi.pipo.learncolors;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    public static final String[] COLOR_NAMES = {"Red", "Green", "Blue", "Yellow", "Cyan", "Magenta", "White", "Black"};
    public static final int[] COLORS = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.WHITE, Color.BLACK};
    private  Drawable iconNoSound ;
    private  Drawable iconUseSound;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private boolean useSound = false;
    FloatingActionButton fab;
    int exitCount =0;
    String exitText;
    private static final int TIME_INTERVAL = 4000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    private ScrollView mScrollView;

    public static AnimatedVectorDrawable crossToTick;

    @Override
    public void onEnterAnimationComplete(){
        super.onEnterAnimationComplete();
        final int startScrollPosition = getResources().getDimensionPixelSize(R.dimen.init_scroll_up_distance);
        Animator animator = ObjectAnimator.ofInt(
                mScrollView,
                "scrollY",
                startScrollPosition).setDuration (300);
        animator.start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        exitText = getString(R.string.exit);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        iconNoSound = getDrawable(android.R.drawable.ic_lock_silent_mode);
        iconUseSound = getDrawable(android.R.drawable.ic_lock_silent_mode_off);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFabBg();
            }
        });

    }

    private void changeFabBg() {
        useSound = !useSound;
        if (useSound) {
            fab.setImageDrawable(iconUseSound);
        } else {
            fab.setImageDrawable(iconNoSound);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private ScrollView mScrollView;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            rootView.setBackgroundColor(COLORS[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);

//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.chuck);
//
//            RoundedBitmapDrawable drawable  = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
//            drawable.setCircular(true);
            final ImageView roundImage = (ImageView)rootView.findViewById(R.id.imageView);
//            roundImage.setBackground(drawable);

                BounceInterpolator inter = new BounceInterpolator();

            DisplayMetrics metrix = new DisplayMetrics();

            TextView textView = (TextView) rootView.findViewById(R.id.textView);
            textView.setTranslationY(300);

            textView.animate()
                    .setInterpolator(inter)
                    .setDuration(1500)
                    .setStartDelay(500)
            .translationYBy(-300);
//            textView.setText(COLOR_NAMES[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);
            textView.setTextColor(COLORS[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);
            mScrollView = (ScrollView)rootView.findViewById(R.id.scrollView);


            crossToTick = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.avd_cross_to_tick);
            roundImage.setImageDrawable(crossToTick);
//            crossToTick.start();

            roundImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    crossToTick.start();
                }
            });
            return rootView;
        }

        private void playColor(ImageView roundImage, AnimatedVectorDrawable crossToTick) {
            roundImage.setImageDrawable(crossToTick);
            crossToTick.start();
            // Play color sound here
        }



    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }


        @Override
        public int getCount() {
            // Show 3 total pages.
            return COLOR_NAMES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return COLOR_NAMES[position];
        }
    }

        @Override
    public void onBackPressed(){
            // override back buttons
            exitMethod();
        }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_HOME)) {
            exitMethod();
            return false;
        } else if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            exitMethod();
            return true;
        }
//        return super.onKeyDown(keyCode, event);
        return false;
    }

    private void exitMethod() {
        if (exitCount==0){
            mBackPressed= System.currentTimeMillis()+TIME_INTERVAL;
            System.out.println("exitCount==0");
        }
        exitCount++;

        if (mBackPressed<System.currentTimeMillis()){
        exitCount=0;
        System.out.println("mBackPressed<System.currentTimeMillis()");
        }

        if (exitCount>3){
            finish();
        } else {
            Toast.makeText(getApplicationContext(), Integer.toString(4-exitCount) + " "+ exitText, Toast.LENGTH_SHORT).show();
        }
        System.out.println("exitCount++" + exitCount);
    }

}
